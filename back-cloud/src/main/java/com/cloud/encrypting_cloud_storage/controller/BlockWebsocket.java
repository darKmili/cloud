package com.cloud.encrypting_cloud_storage.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONObject;
import com.cloud.encrypting_cloud_storage.enums.FileState;
import com.cloud.encrypting_cloud_storage.enums.FileType;
import com.cloud.encrypting_cloud_storage.exceptions.ApiException;
import com.cloud.encrypting_cloud_storage.models.dto.FileDto;
import com.cloud.encrypting_cloud_storage.models.po.FileBlockPo;
import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import com.cloud.encrypting_cloud_storage.models.po.UserPo;
import com.cloud.encrypting_cloud_storage.models.vo.BlockVo;
import com.cloud.encrypting_cloud_storage.models.vo.TransVo;
import com.cloud.encrypting_cloud_storage.service.BlockService;
import com.cloud.encrypting_cloud_storage.service.FileService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author leon
 * @Description: 文件块控制器，基于websocket协议
 * @date 2022年05月11日 19:37
 */
@Slf4j
@ServerEndpoint("/upload/{userId}")
@Api(tags = "文件块上传")
@Component
public class BlockWebsocket {


    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;

    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。主键是每个用户的ID
     */
    private static final ConcurrentHashMap<Long, BlockWebsocket> blockWebsockets = new ConcurrentHashMap<>();


    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;


    /**
     * 当前文件的情况
     */
    private FilePo filePo;

    /**
     * 当前块的情况
     */
    private FileBlockPo blockPo;


    /**
     * 结束标识判断
     */
    private static final String END_UPLOAD = "over";
    private static final String FILE_UPLOAD = "fileUpload";
    private static final String BLOCK_UPLOAD = "block";

    /**
     *   业务对象要变为静态，不然无法注入。。当有连接接入时，会创建一个新的服务器类对象，而spring只会给IOC容器启动时创建的对象注入userService，连接接入时创建的对象并没有注入
     */
    private static BlockService blockService;
    private static FileService fileService;
    private static RedisTemplate<String, String> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        BlockWebsocket.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setFileService(FileService fileService) {
        BlockWebsocket.fileService = fileService;
    }

    @Autowired
    @Qualifier(value = "QiniuUploadService")
    public void setBlockService(BlockService blockService) {
        BlockWebsocket.blockService = blockService;
    }


    /**
     * onopen 在连接创建时触发
     *
     * @param session
     */
    @OnOpen
    public void openSession(Session session, @PathParam("userId") Long userId) {
        this.session = session;
        blockWebsockets.put(userId, this);
        addOnlineCount();
        log.info("创建连接:"+"当前连接数"+onlineCount);
    }

    /**
     * websocket能发送三种请求，一种是字符串，一种是字节流（用于上传文件），一种是ping-pong（乒乓机制）(这里没有涉及到)
     * <p>
     * 接收到字符消息时响应
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId) {
        log.info("字符串消息" + message);
        // 前端传进来一个字符串信息
        TransVo transVo = JSONObject.parseObject(message, TransVo.class);

        String opt = transVo.getOpt();

        // 文件上传开始
        if (FILE_UPLOAD.equals(opt)) {
            FileDto fileDto = new FileDto();
            BeanUtil.copyProperties(transVo.getData(),fileDto,false);
            // 保存文件信息
            FilePo filePo = new FilePo();
            BeanUtil.copyProperties(fileDto,filePo,true);
            filePo.setState(FileState.NEW);
            filePo.setType(FileType.FILE);
            filePo.setParentDir(new FilePo(fileDto.getParentInode()));
            filePo.setUser(new UserPo(fileDto.getUserId()));
            this.filePo = fileService.save(filePo);
            try {
                this.sendMessage(JSONObject.toJSONString(new BlockVo("blockMetadata",0)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (BLOCK_UPLOAD.equals(opt)) {
            // 检查文件状态是否为新建状态，将其改为上传状态
            if (filePo.getState() == FileState.NEW) {
                filePo.setState(FileState.UPLOADING);
                fileService.save(filePo);
            }
            FileBlockPo blockPo = new FileBlockPo();
            BeanUtil.copyProperties(transVo.getData(),blockPo,false);
            blockPo.setParentFilePo(this.filePo);
            blockPo.setFingerprint(Base64.encode(blockPo.getFingerprint()));

            // 将块元数据保存到数据库
            // 检查 当前块的 数据是否已经存在与redis中
            String size = redisTemplate.opsForValue().get(blockPo.getFingerprint());
            this.blockPo = blockService.save(blockPo);

            // 如果当前块存在于缓存中，表示当前数据已经存在，不需要进行接下来的上传操作
            if (size != null) {
                try {
                    redisTemplate.opsForValue().set(blockPo.getFingerprint(), String.valueOf(Integer.parseInt(size) + 1));
                    sendMessage(JSONObject.toJSONString(new BlockVo("blockMetadata",this.blockPo.getIdx()+1)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 不存在与缓存中
            }else {
                redisTemplate.opsForValue().set(blockPo.getFingerprint(), String.valueOf(1));
                try {
                    sendMessage(JSONObject.toJSONString(new BlockVo("blockData",this.blockPo.getIdx())));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        } else if (END_UPLOAD.equals(opt)) {
            filePo.setState(FileState.UPLOADED);
            fileService.save(filePo);
            try {
                sendMessage(JSONObject.toJSONString(new BlockVo("uploaded",null)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 接受到字节流流时创建
     *
     * @param message
     */
    @OnMessage
    public void onMessage(byte[] message) {
        if (this.blockPo == null) {
            throw new ApiException(5000, "服务端错误");
        }
        /**
         * 将流存储到桶(ceph),或者其他云设备
         */
        this.blockPo.setData(message);

        try {
            boolean b = blockService.uploadBlock(blockPo);
            if (!b) {
                sendMessage("数据上传失败");
            }
            // next 表示当前传输完成，请客户端继续传输
            sendMessage(JSONObject.toJSONString(new BlockVo("blockMetadata",this.blockPo.getIdx()+1)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.blockPo = null;
    }

    /**
     * 断开连接时触发
     *
     */
    @OnClose
    public void closeSession() {
        subOnlineCount();
        log.info("关闭socket通信:"+"当前连接数"+onlineCount);
    }

    /**
     * 发生错误时触发
     *
     * @param session
     * @param throwable
     */
    @OnError
    public void sessionError(Session session, Throwable throwable) {
        log.info("发生异常事件");
        throwable.printStackTrace();
    }

    /**
     * 服务器主动提推送消息
     *
     * @param message 消息内容
     * @throws IOException io异常抛出
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 原子性的++操作
     */
    public static synchronized void addOnlineCount() {
        BlockWebsocket.onlineCount++;
    }

    /**
     * 原子性的--操作
     */
    public static synchronized void subOnlineCount() {
        BlockWebsocket.onlineCount--;
    }

}
