package com.example.damimall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//@MapperScan("com.example.damimall.coupon.dao")
@EnableDiscoveryClient
@EnableTransactionManagement
public class CouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponApplication.class);
    }
}
