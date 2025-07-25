package com.example.damimall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
//@MapperScan("com.example.damimall.member.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.example.damimall.member.feign")
public class MemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(MemberApplication.class);
    }
}
