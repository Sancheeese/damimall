package com.example.com.damimall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cart.thread-pool")
@Data
public class ThreadPoolProperties {
    private int corePoolSize;

    private int maximumPoolSize;

    private long keepAliveTime;
}
