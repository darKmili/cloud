package com.cloud.encrypting_cloud_storage.config;

import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author leon
 * @Description: 七牛云配置
 * @date 2022年04月26日 13:44
 */
@Configuration
public class QiniuConfig {
    @Value("${qiniu.accessKey}")
    private String accessKey;
    @Value(("${qiniu.secretKey}"))
    private String secretKey;

    /**
     * 配置，自动配置区域
     */
    @Bean
    public com.qiniu.storage.Configuration configuration() {
        return new com.qiniu.storage.Configuration(Region.autoRegion());
    }

    /**
     * 桶管理实例
     */
    @Bean
    public BucketManager bucketManager() {
        return new BucketManager(auth(), configuration());
    }

    /**
     * 授权
     */
    @Bean
    public Auth auth() {
        return Auth.create(accessKey, secretKey);
    }

    /**
     * 上传管理实例
     */
    @Bean
    public UploadManager uploadManager() {
        return new UploadManager(configuration());
    }
}
