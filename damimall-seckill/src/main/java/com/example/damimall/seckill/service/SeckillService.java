package com.example.damimall.seckill.service;

import com.example.common.to.seckill.SeckillSkuRedisTo;

import java.util.List;

public interface SeckillService {
    void uploadSeckillLatest3Days();

    List<SeckillSkuRedisTo> getCurrentSeckillSkus();

    SeckillSkuRedisTo getSeckillInfo(Long skuId);

    String seckill(String killId, String key, Integer num);
}
