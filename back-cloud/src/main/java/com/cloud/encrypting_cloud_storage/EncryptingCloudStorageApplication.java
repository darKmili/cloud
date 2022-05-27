package com.cloud.encrypting_cloud_storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author liyihong
 */
@SpringBootApplication
public class EncryptingCloudStorageApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(EncryptingCloudStorageApplication.class, args);

    }

}
