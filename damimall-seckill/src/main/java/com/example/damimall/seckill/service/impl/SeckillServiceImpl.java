package com.example.damimall.seckill.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.common.to.seckill.*;
import com.example.common.utils.ObjectMapperUtils;
import com.example.common.utils.R;
import com.example.damimall.seckill.feign.CouponFeign;
import com.example.damimall.seckill.feign.ProductFeign;
import com.example.damimall.seckill.service.SeckillService;
import com.example.damimall.seckill.utils.UserInfoUtils;
import com.example.damimall.seckill.utils.UserInfoVo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    CouponFeign couponFeign;

    @Autowired
    ProductFeign productFeign;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RabbitTemplate rabbitTemplate;

    private static final String SECKILL_SESSION_PREFIX = "seckill:session:";
    private static final String SECKILL_SKU_KEY = "seckill:sku";
    private static final String SECKILL_STOCK_SEMAPHORE_PREFIX = "seckill:stock:";
    private static final String SECKILL_STOCK_USER_PREFIX = "seckill:stock:user:";
    private static final String SECKILL_STOCK_USER_LOCK_PREFIX = "seckill:stock:user:lock:";

    @Override
    public void uploadSeckillLatest3Days() {
        // 查询最近三天的秒杀商品
        R r = couponFeign.latest3DaysSeckill();
        if (r == null || r.getCode() != 0) return ;
        List<SeckillSessionWithRelationTo> sessions = r.getData(new TypeReference<List<SeckillSessionWithRelationTo>>() {
        });

        try {
            saveSession(sessions);
            saveSessionSku(sessions);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
        long now = new Date().getTime();
        Set<String> keys = redisTemplate.keys(SECKILL_SESSION_PREFIX + "*");
        for (String key : keys) {
            long start = Long.parseLong(key.split(":")[2].split("_")[0]);
            long end = Long.parseLong(key.split(":")[2].split("_")[1]);
            if (now > start && now < end){
                List<String> skuKeys = redisTemplate.opsForList().range(key, 0, -1);
                BoundHashOperations<String, String, String> skuOps = redisTemplate.boundHashOps(SECKILL_SKU_KEY);
                List<SeckillSkuRedisTo> tos = skuOps.multiGet(skuKeys).stream()
                        .map(json -> {
                            SeckillSkuRedisTo to = ObjectMapperUtils.readValue(json, new TypeReference<SeckillSkuRedisTo>() {});
                            return to;
                        })
                        .collect(Collectors.toList());

                return tos;
            }
        }

        return new ArrayList<>();
    }

    public void saveSession(List<SeckillSessionWithRelationTo> sessions){
        for (SeckillSessionWithRelationTo session : sessions) {
            List<String> sessionWithId = new ArrayList<>();
            String key = session.getStartTime().getTime() + "_" + session.getEndTime().getTime();

            // 如果该场次已上架就跳过
            if (redisTemplate.hasKey(SECKILL_SESSION_PREFIX + key)) continue;
            for (SeckillRelationTo sku : session.getRelationSkus()) {
                sessionWithId.add(session.getId() + "_" + sku.getSkuId());
            }
            redisTemplate.opsForList().leftPushAll(SECKILL_SESSION_PREFIX + key, sessionWithId);
        }
    }

    public void saveSessionSku(List<SeckillSessionWithRelationTo> sessions) throws InvocationTargetException, IllegalAccessException {
        // 查询所有sku的详细信息
        Set<Long> skuIds = new HashSet<>();
        for (SeckillSessionWithRelationTo session : sessions) {
            for (SeckillRelationTo sku : session.getRelationSkus()) {
                skuIds.add(sku.getSkuId());
            }
        }
        List<SkuInfoTo> skuInfos = productFeign.getBatchInfo(new ArrayList<>(skuIds));
        // 转成map
        Map<Long, SkuInfoTo> id2Info = skuInfos.stream().collect(Collectors.toMap(SkuInfoTo::getSkuId, info -> info));
        // 构建存入redis的数据
        Map<String, String> redisDate = new HashMap<>();
        for (SeckillSessionWithRelationTo session : sessions) {
            for (SeckillRelationTo sku : session.getRelationSkus()) {
                String key = session.getId() + "_" + sku.getSkuId();
                if (redisTemplate.opsForHash().hasKey(SECKILL_SKU_KEY, key)) continue;

                // sku基本信息
                SeckillSkuRedisTo to = new SeckillSkuRedisTo();
                BeanUtils.copyProperties(to, sku);
                to.setSkuInfo(id2Info.get(to.getSkuId()));

                // 秒杀信息
                to.setStartTime(session.getStartTime().getTime());
                to.setEndTime(session.getEndTime().getTime());

                // 随机码
                String code = UUID.randomUUID().toString().replace("_", "");
                to.setRandomCode(code);
                RSemaphore semaphore = redissonClient.getSemaphore(SECKILL_STOCK_SEMAPHORE_PREFIX + code);
                semaphore.trySetPermits(Integer.parseInt(to.getSeckillCount().toString()));

                redisDate.put(key, ObjectMapperUtils.writeValueAsString(to));
            }
        }
        redisTemplate.opsForHash().putAll(SECKILL_SKU_KEY, redisDate);
    }

    @Override
    public SeckillSkuRedisTo getSeckillInfo(Long skuId) {
        Set<Object> keys = redisTemplate.opsForHash().keys(SECKILL_SKU_KEY);
        SeckillSkuRedisTo to = null;
        long latestStart = Long.MAX_VALUE;
        long now = new Date().getTime();
        for (Object o : keys) {
            String key = o.toString();
            if (skuId.equals(Long.parseLong(key.split("_")[1]))){
                String json = redisTemplate.opsForHash().get(SECKILL_SKU_KEY, key).toString();
                SeckillSkuRedisTo sku = ObjectMapperUtils.readValue(json, new TypeReference<SeckillSkuRedisTo>() {
                });
                // 取开始时间最近一场的秒杀信息
                if (sku.getStartTime() > latestStart) continue;
                latestStart = sku.getStartTime();
                to = sku;
                if (now < sku.getStartTime()) to.setRandomCode(null);
            }
        }

        return to;
    }

    @Override
    public String seckill(String killId, String key, Integer num) {
        //获取登录用户
        UserInfoVo user = UserInfoUtils.getUser();
        if (user == null) return null;

        Object o = redisTemplate.opsForHash().get(SECKILL_SKU_KEY, killId);
        if (o == null) return null;
        String skuJson = o.toString();
        SeckillSkuRedisTo sku = ObjectMapperUtils.readValue(skuJson, new TypeReference<SeckillSkuRedisTo>() {});
        // 检查时间对不对
        long now = new Date().getTime();
        if (now < sku.getStartTime() || now > sku.getEndTime())
            return null;

        // 检查随机码
        if (!key.equals(sku.getRandomCode()) || !killId.equals(sku.getPromotionSessionId() + "_" + sku.getSkuId()))
            return null;

        // 检查限单
        long limit = sku.getSeckillLimit().longValue();
        if (num > limit) return null;
        RLock lock = redissonClient.getLock(SECKILL_STOCK_USER_LOCK_PREFIX + user.getUserId());
        try{
            if (!lock.tryLock(300, TimeUnit.MILLISECONDS)) return null;
            String stock = redisTemplate.opsForValue().get(SECKILL_STOCK_USER_PREFIX + user.getUserId());
            // 如果买超了就退出
            Integer totalBuy = num;
            if (stock != null) totalBuy += Integer.parseInt(stock);
            if (totalBuy > limit) return null;
            // 计算活动持续时间
            long last = sku.getEndTime() - sku.getStartTime();
            redisTemplate.opsForValue().set(SECKILL_STOCK_USER_PREFIX + user.getUserId(),
                    totalBuy.toString(),
                    last + 30 * 60 * 1000,
                    TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }

        // 查看库存是否充足
        RSemaphore semaphore = redissonClient.getSemaphore(SECKILL_STOCK_SEMAPHORE_PREFIX + sku.getRandomCode());
        try {
            boolean b = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
            if (!b) return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 生成订单号发消息队列
        SeckillOrderTo order = new SeckillOrderTo();
        String orderSn = IdWorker.getTimeId();
        order.setOrderSn(orderSn);
        order.setMemberId(user.getUserId());
        order.setNum(num);
        order.setPromotionSessionId(sku.getPromotionSessionId());
        order.setSeckillPrice(sku.getSeckillPrice());
        rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", order);

        return orderSn;
    }
}
