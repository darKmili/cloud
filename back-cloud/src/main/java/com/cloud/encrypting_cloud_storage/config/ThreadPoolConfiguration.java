package com.cloud.encrypting_cloud_storage.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created with IntelliJ IDEA.
 *
 * @author： leon
 * @description：
 * @date： 2023/2/11
 * @version: 1.0
 */
@Configuration
@Slf4j
public class ThreadPoolConfiguration {
    @Bean(name = "defaultThreadPoolExecutor", destroyMethod = "shutdown")
    public ThreadPoolExecutor systemCheckPoolExecutorService() {

        return new ThreadPoolExecutor(3, 10, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(10000),
                new ThreadFactoryBuilder().setNamePrefix("default-executor-").build(),
                (r, executor) -> log.error("system pool is full! "));
    }
}
