package com.example.damimall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
//@MapperScan("com.example.damimall.ware.dao")
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableFeignClients(basePackages = "com.example.damimall.ware.feign")
@EnableRabbit
public class WareApplication {
    public static void main(String[] args) {
        SpringApplication.run(WareApplication.class);
    }
}
