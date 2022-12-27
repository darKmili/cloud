package com.cloud.encrypting_cloud_storage.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.CORSRule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * @author leon
 * @Description: ceph集群配置
 * @date 2022年04月27日 19:30
 */
@Configuration
public class CephConfig {
    @Value("${ceph.accessKey}")
    private String accessKey;
    @Value("${ceph.secretKey}")
    private String secretKey;
    @Value("${ceph.host}")
    private String host;
    @Value("${ceph.bucket}")
    private String bucketName;
    @Bean
    public AWSCredentials  awsCredentials(){
        return new BasicAWSCredentials(accessKey,secretKey);
    }
    @Bean
    public ClientConfiguration cephConfiguration(){
        return new ClientConfiguration().withProtocol(Protocol.HTTP);
    }
    @Bean
    public AmazonS3Client amazonS3Client(){
        AmazonS3Client amazonS3Client = new AmazonS3Client(awsCredentials(), cephConfiguration());
        amazonS3Client.setEndpoint(host);
        // 创建bucket
        List<Bucket> buckets = amazonS3Client.listBuckets();
        Set<String> buckets_set = new HashSet<>();
        for (Bucket bucket : buckets) {
            buckets_set.add(bucket.getName());
        }
        if (!buckets_set.contains(bucketName)) {
            amazonS3Client.createBucket(bucketName);
            List<CORSRule.AllowedMethods> rule1AM = new ArrayList<CORSRule.AllowedMethods>();
            rule1AM.add(CORSRule.AllowedMethods.PUT);
            rule1AM.add(CORSRule.AllowedMethods.POST);
            rule1AM.add(CORSRule.AllowedMethods.DELETE);
            rule1AM.add(CORSRule.AllowedMethods.GET);
            CORSRule rule1 = new CORSRule().withId("CORSRule1")
                    .withAllowedMethods(rule1AM)
                    .withAllowedOrigins(Arrays.asList("*"))
                    .withAllowedHeaders(Arrays.asList("*"))
                    .withMaxAgeSeconds(3000);
            List<CORSRule> rules = new ArrayList<CORSRule>();
            rules.add(rule1);
            // Add the rules to a new CORS configuration.
            BucketCrossOriginConfiguration configuration = new BucketCrossOriginConfiguration();
            configuration.setRules(rules);
            // Add the configuration to the bucket.
            amazonS3Client.setBucketCrossOriginConfiguration(bucketName, configuration);
        }

        return amazonS3Client;
    }

}
