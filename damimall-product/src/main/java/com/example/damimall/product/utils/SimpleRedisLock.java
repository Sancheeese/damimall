package com.example.damimall.product.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class SimpleRedisLock implements ILock{
    private String lockKey;

    private StringRedisTemplate redisTemplate;

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

    static{
        UNLOCK_SCRIPT = new DefaultRedisScript<>();
        UNLOCK_SCRIPT.setLocation(new ClassPathResource("redisLua/unlock.lua"));
        UNLOCK_SCRIPT.setResultType(Long.class);
    }

    public SimpleRedisLock(String key, StringRedisTemplate redisTemplate){
        this.lockKey = key;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean tryLock(Long expireSec, String value) {
        Boolean ret = redisTemplate.opsForValue().setIfAbsent(lockKey, value, expireSec, TimeUnit.SECONDS);

        return Boolean.TRUE.equals(ret);
    }

//    使用Lua脚本
    @Override
    public void unLock(String value) {
        redisTemplate.execute(UNLOCK_SCRIPT,
                Collections.singletonList(lockKey),
                value);
    }
}
