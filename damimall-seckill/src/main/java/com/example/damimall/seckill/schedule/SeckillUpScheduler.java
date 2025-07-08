package com.example.damimall.seckill.schedule;

import com.example.damimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@EnableScheduling
@EnableAsync
public class SeckillUpScheduler {
    @Autowired
    SeckillService seckillService;

    @Autowired
    RedissonClient redissonClient;

    private static final String UPLOAD_LOCK = "seckill:upload:lock";

    @Scheduled(cron = "* * 3 * * ?")
    public void seckillUpLatest3Days(){
        log.info("上架秒杀商品");
        RLock lock = redissonClient.getLock(UPLOAD_LOCK);
        try {
            lock.lock(10, TimeUnit.SECONDS);
            seckillService.uploadSeckillLatest3Days();
        }finally {
            lock.unlock();
        }

    }
}
