package com.example.damimall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.product.thread-pool")
@Data
public class ThreadPoolProperties {
    private int corePoolSize;

    private int maximumPoolSize;

    private long keepAliveTime;
}
