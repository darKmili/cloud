package com.cloud.encrypting_cloud_storage.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.CORSRule;

import lombok.extern.slf4j.Slf4j;

/**
 * @author leon
 * @Description: ceph集群配置
 * @date 2022年04月27日 19:30
 */
@Slf4j
@Configuration
public class CephConfig {
    @Value("${ceph.accessKey}")
    private String accessKey;
    @Value("${ceph.secretKey}")
    private String secretKey;
    @Value("${ceph.host}")
    private String host;
    @Value("${ceph.bucket}")
    private String bucket;

    @Bean
    public AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    public ClientConfiguration cephConfiguration() {
        return new ClientConfiguration().withProtocol(Protocol.HTTP);
    }

    @Bean
    public AmazonS3Client amazonS3Client() {
        AmazonS3Client amazonS3 = new AmazonS3Client(awsCredentials(), cephConfiguration());
        amazonS3.setEndpoint(host);
        if (amazonS3.doesBucketExist(bucket)) {
            log.debug("Storage s3 api, bucketName is found: " + bucket);
        } else {
            amazonS3.createBucket(bucket);
            log.warn("Storage s3 api, bucketName is not exist, create it: " + bucket);
            List<CORSRule.AllowedMethods> rule1AM = new ArrayList<CORSRule.AllowedMethods>();
            rule1AM.add(CORSRule.AllowedMethods.PUT);
            rule1AM.add(CORSRule.AllowedMethods.POST);
            rule1AM.add(CORSRule.AllowedMethods.DELETE);
            rule1AM.add(CORSRule.AllowedMethods.GET);
            CORSRule rule1 = new CORSRule().withId("CORSRule1").withAllowedMethods(rule1AM)
                .withAllowedOrigins(Arrays.asList("*")).withAllowedHeaders(Arrays.asList("*")).withMaxAgeSeconds(3000);
            List<CORSRule> rules = new ArrayList<CORSRule>();
            rules.add(rule1);
            // Add the rules to a new CORS configuration.
            BucketCrossOriginConfiguration configuration = new BucketCrossOriginConfiguration();
            configuration.setRules(rules);
            // Add the configuration to the bucket.
            amazonS3.setBucketCrossOriginConfiguration(bucket, configuration);
        }
        return amazonS3;
    }

}
