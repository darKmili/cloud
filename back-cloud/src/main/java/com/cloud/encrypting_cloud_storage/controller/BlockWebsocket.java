package com.cloud.encrypting_cloud_storage.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
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
import com.cloud.encrypting_cloud_storage.service.UserService;
import com.cloud.encrypting_cloud_storage.util.MyStringUtil;
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
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
    private static final String FILE_UPLOAD = "fileMetadata";
    private static final String BLOCK_UPLOAD = "block";

    /**
     * 业务对象要变为静态，不然无法注入。。当有连接接入时，会创建一个新的服务器类对象，而spring只会给IOC容器启动时创建的对象注入userService，连接接入时创建的对象并没有注入
     */
    private static BlockService blockService;
    private static FileService fileService;
    private static RedisTemplate<String, String> redisTemplate;

    private static UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        BlockWebsocket.userService = userService;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        BlockWebsocket.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setFileService(FileService fileService) {
        BlockWebsocket.fileService = fileService;
    }

    @Autowired
    @Qualifier(value = "cephFileBlockService")
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
        log.info("创建连接:" + "当前连接数" + onlineCount);
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
        // 前端传进来一个字符串信息
        TransVo transVo = JSONObject.parseObject(message, TransVo.class);

        String opt = transVo.getOpt();

        // 文件上传开始
        if (FILE_UPLOAD.equals(opt)) {
            FileDto fileDto = new FileDto();
            BeanUtil.copyProperties(transVo.getData(), fileDto, false);
            // 保存文件信息
            FilePo filePo = new FilePo();
            BeanUtil.copyProperties(fileDto, filePo, true);
            filePo.setState(FileState.UPLOADED);
            filePo.setType(FileType.FILE);
            filePo.setParentDir(new FilePo(fileDto.getParentInode()));
            filePo.setUser(new UserPo(Long.parseLong(userId)));
            this.filePo = fileService.save(filePo);
        }

//        else if (BLOCK_UPLOAD.equals(opt)) {
//            final JSONObject data = (JSONObject) transVo.getData();
//            // 检查文件状态
//            FileBlockPo blockPo = new FileBlockPo();
//            blockPo.setData(data.getString("encryptedData"));
//            blockPo.setParentFilePo(this.filePo);
//            blockPo.setFingerprint((String) data.getString("fingerprint"));
//            blockPo.setIdx((Integer) data.getInteger("idx"));
//            blockPo.setSize(data.getLong("size"));
//            // 将块元数据保存到数据库
//            // 检查 当前块的 数据是否已经存在与redis中
//            String size = redisTemplate.opsForValue().get(blockPo.getFingerprint());
//
//            // 如果当前块存在于缓存中，表示当前数据已经存在，不存需要进行接下来的上传操作,只需将块的缓数据加1
//            if (size != null) {
//                redisTemplate.opsForValue().increment(blockPo.getFingerprint());
//                blockPo.setUrl(blockService.getFingerprintUrl(blockPo.getFingerprint()));
//                blockPo = blockService.save(blockPo);
//            } else {
//                // 不存在与缓存中
//                redisTemplate.opsForValue().set(blockPo.getFingerprint(), String.valueOf(1));
//                final String url;
//                try {
//                    url = blockService.uploadBlock(blockPo);
//                    if (!StrUtil.isBlank(url)) {
//                        blockPo.setUrl(url);
//                        blockPo = blockService.save(blockPo);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//            blockPo.setData(null);
//            try {
//                sendMessage(JSONObject.toJSONString(blockPo));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

//        }
    }

    /**
     * 接受到字节流流时创建
     * @param message
     */
    /**
     * 接受到字节流流时创建
     *
     * @param message
     */
    @OnMessage
    public void onMessage(byte[] message) {

        log.info(String.valueOf(System.currentTimeMillis()));



//        if (message.length>=1){
//            return;
//        }
        // 解析字节数据 20指纹+4索引+4块大小+数据
        String fingerprint =  new String(message,0,20, StandardCharsets.US_ASCII);




        int idx = MyStringUtil.bytesToInt(Arrays.copyOfRange(message,20,24));
        int size = MyStringUtil.bytesToInt(Arrays.copyOfRange(message,24,28));
        byte[] data = Arrays.copyOfRange(message,28,message.length);
        // 将数据保存到后端(数据库+Ceph集群)

        FileBlockPo blockPo = new FileBlockPo();
        blockPo.setFingerprint(fingerprint);
        blockPo.setData(data);
        blockPo.setSize((long)size);
        blockPo.setIdx(idx);
        blockPo.setParentFilePo(filePo);
        // 将块元数据保存到数据库
        // 检查 当前块的 数据是否已经存在与redis中
        String blockSize = redisTemplate.opsForValue().get(blockPo.getFingerprint());

        // 如果当前块存在于缓存中，表示当前数据已经存在，不存需要进行接下来的上传操作,只需将块的缓数据加1
        if (blockSize != null) {
            redisTemplate.opsForValue().increment(blockPo.getFingerprint());
            blockPo.setUrl(blockService.getFingerprintUrl(blockPo.getFingerprint()));
            blockPo = blockService.save(blockPo);
        } else {
            // 不存在与缓存中
            redisTemplate.opsForValue().set(blockPo.getFingerprint(), String.valueOf(1));
            final String url;
            try {
                url = blockService.uploadBlock(blockPo);
                if (!StrUtil.isBlank(url)) {
                    blockPo.setUrl(url);
                    blockPo = blockService.save(blockPo);
                }
                blockPo.setData(null);
                try {
                    sendMessage(JSONObject.toJSONString(blockPo));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                redisTemplate.opsForValue().set(blockPo.getFingerprint(), String.valueOf(0));
                e.printStackTrace();
            }
        }





    }

    /**
     * 断开连接时触发
     */
    @OnClose
    public void closeSession() {
        subOnlineCount();
        log.info("关闭socket通信:" + "当前连接数" + onlineCount);
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
