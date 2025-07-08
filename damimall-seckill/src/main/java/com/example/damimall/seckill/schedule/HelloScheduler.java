package com.example.damimall.seckill.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@EnableAsync
public class HelloScheduler {

//    @Async
//    @Scheduled(cron = "* * * * * ?")
//    public void hello() throws InterruptedException {
//        System.out.println("hello");
//        Thread.sleep(3000);
//    }
}
