package com.cloud.encrypting_cloud_storage.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author leon
 * @Description: ceph集群配置
 * @date 2022年04月27日 19:30
 */
@Configuration
public class CephConfig {
    @Value("ceph.accessKey")
    private String accessKey;
    @Value("ceph.secretKey")
    private String secretKey;
    @Value("${ceph.host}")
    private String host;
    @Bean
    public AWSCredentials  awsCredentials(){
        return new BasicAWSCredentials(accessKey,secretKey);
    }
    @Bean
    public ClientConfiguration cephConfiguration(){
        return new ClientConfiguration().withProtocol(Protocol.HTTP);
    }
    @Bean
    public AmazonS3 amazonS3Client(){
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(host,"region"))
                .withClientConfiguration(cephConfiguration())
                .build();
    }

}
