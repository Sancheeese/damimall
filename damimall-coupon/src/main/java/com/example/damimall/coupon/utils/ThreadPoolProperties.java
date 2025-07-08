package com.example.damimall.coupon.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.coupon.thread-pool")
@Data
public class ThreadPoolProperties {
    private int corePoolSize;

    private int maximumPoolSize;

    private long keepAliveTime;
}
