package com.cloud.encrypting_cloud_storage.controller;

import com.alibaba.fastjson.JSONObject;
import com.cloud.encrypting_cloud_storage.exceptions.ApiException;
import com.cloud.encrypting_cloud_storage.models.po.FileBlockPo;
import com.cloud.encrypting_cloud_storage.models.po.FilePo;
import com.cloud.encrypting_cloud_storage.models.vo.BlockVo;
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文件下载
 *
 * @author： leon
 * @description：
 * @date： 2022/6/1
 * @version: 1.0
 */
@Slf4j
@ServerEndpoint("/download/{userId}")
@Api(tags = "文件块上传")
@Component
public class BlockDownloadWebSocket {
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;

    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。主键是每个用户的ID
     */
    private static final ConcurrentHashMap<Long, BlockDownloadWebSocket> BlockDownloadWebsockets = new ConcurrentHashMap<>();


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
     * 业务对象要变为静态，不然无法注入。。当有连接接入时，会创建一个新的服务器类对象，而spring只会给IOC容器启动时创建的对象注入userService，连接接入时创建的对象并没有注入
     */
    private static BlockService blockService;
    private static FileService fileService;
    private static RedisTemplate<String, String> redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        BlockDownloadWebSocket.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setFileService(FileService fileService) {
        BlockDownloadWebSocket.fileService = fileService;
    }

    @Autowired
    @Qualifier(value = "QiniuUploadService")
    public void setBlockService(BlockService blockService) {
        BlockDownloadWebSocket.blockService = blockService;
    }


    /**
     * onopen 在连接创建时触发
     *
     * @param session
     */
    @OnOpen
    public void openSession(Session session, @PathParam("userId") Long userId) {
        this.session = session;
        BlockDownloadWebsockets.put(userId, this);
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
        log.info("字符串消息" + message);
        // 前端传进来一个字符串信息
        FilePo filePo = JSONObject.parseObject(message, FilePo.class);
        Set<FileBlockPo> allBlock = null;
        if (filePo.getFileBlocks() == null) {
            allBlock = blockService.findFileAllBlock(filePo);
        } else {
            allBlock = filePo.getFileBlocks();
        }

        for (FileBlockPo fileBlockPo : allBlock) {
            try {
                FileBlockPo downloadBlock = blockService.downloadBlock(fileBlockPo);
                this.sendMessage(JSONObject.toJSONString(downloadBlock, false));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            this.sendMessage("over");
        } catch (IOException e) {
            e.printStackTrace();
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
            sendMessage(JSONObject.toJSONString(new BlockVo("blockMetadata", this.blockPo.getIdx() + 1)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.blockPo = null;
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
        BlockDownloadWebSocket.onlineCount++;
    }

    /**
     * 原子性的--操作
     */
    public static synchronized void subOnlineCount() {
        BlockDownloadWebSocket.onlineCount--;
    }
}
