package com.cloud.encrypting_cloud_storage.config;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * @author leon
 * @Description: 上传配置
 * @date 2022年04月26日 10:14
 */
@Configuration
@ConditionalOnClass({Servlet.class, StandardServletMultipartResolver.class, MultipartConfigElement.class})
@ConditionalOnProperty(prefix = "spring.servlet.multipart", name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties(MultipartProperties.class)
public class UploadConfig {

    private final MultipartProperties multipartProperties;

    @Autowired
    public UploadConfig(MultipartProperties multipartProperties) {
        this.multipartProperties = multipartProperties;
    }

    /**
     * 文件上传配置
     * 
     * @return 配置
     */
    @Bean
    @ConditionalOnMissingBean
    public MultipartConfigElement multipartConfigElement() {
        return this.multipartProperties.createMultipartConfig();
    }

    /**
     * 注册解析器
     * 
     * @return 解析器
     */
    @Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
    @ConditionalOnMissingBean
    public StandardServletMultipartResolver multipartResolver() {
        StandardServletMultipartResolver standardServletMultipartResolver = new StandardServletMultipartResolver();
        standardServletMultipartResolver.setResolveLazily(this.multipartProperties.isResolveLazily());
        return standardServletMultipartResolver;
    }

}
