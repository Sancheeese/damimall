package com.example.damimall.seckill.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.seckill.thread-pool")
@Data
public class ThreadPoolProperties {
    private int corePoolSize;

    private int maximumPoolSize;

    private long keepAliveTime;
}
