package com.cloud.encrypting_cloud_storage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * @author leon
 * @Description: webSocket配置类
 * @date 2022年05月11日 19:34
 */
@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * 必须要有的
     *
     * @return
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter()
    {
        return new ServerEndpointExporter();
    }

    /**
     * websocket 配置信息
     *
     * @return
     */
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer()
    {
        ServletServerContainerFactoryBean bean = new ServletServerContainerFactoryBean();
        //文本缓冲区大小 8MB
        bean.setMaxTextMessageBufferSize(1024*1024*8);
        //字节缓冲区大小 8MB
        bean.setMaxBinaryMessageBufferSize(1024*1024*8);
        return bean;
    }
    /**
     * 注册stomp端点。起到的作用就是添加一个服务端点，来接收客户端的连接，
     * registry.addEndpoint("/tmax/ws")
     * 表示添加了一个 /tmax/ws 端点，客户端可以通过这个端点来进行连接。
     * withSockJS() 的作用是开启 SockJS 访问支持，即可通过http://IP:PORT/tmax/ws 来和服务端 websocket 连接。
     */
    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        // Handshake endpoint
        registry.addEndpoint("stomp").withSockJS();
    }

}
