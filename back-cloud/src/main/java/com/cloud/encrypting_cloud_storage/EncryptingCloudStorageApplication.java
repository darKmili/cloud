package com.cloud.encrypting_cloud_storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author liyihong
 */
@SpringBootApplication
@EnableAsync
public class EncryptingCloudStorageApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(EncryptingCloudStorageApplication.class, args);

    }

}
