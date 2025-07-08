package com.example.damimall.order.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.order.thread-pool")
@Data
public class ThreadPoolProperties {
    private int corePoolSize;

    private int maximumPoolSize;

    private long keepAliveTime;
}
