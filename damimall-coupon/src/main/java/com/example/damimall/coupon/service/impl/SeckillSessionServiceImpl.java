package com.example.damimall.coupon.service.impl;

import com.example.common.utils.ParamUtils;
import com.example.damimall.coupon.entity.SeckillSkuRelationEntity;
import com.example.damimall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.coupon.dao.SeckillSessionDao;
import com.example.damimall.coupon.entity.SeckillSessionEntity;
import com.example.damimall.coupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {
    @Autowired
    SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SeckillSessionEntity> queryWrapper = new QueryWrapper<>();
        if (!ParamUtils.isNullOrEmpty(params, "key")){
            queryWrapper.eq("id", params.get("key")).or().like("name", params.get("key"));
        }

        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> latest3DaysSeckill() {
        // 计算时间
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        formatter.format(start);

//        List<SeckillSessionEntity> sessions = query().between("start_time", formatter.format(start), formatter.format(end)).list();
        // 调试时先上架全部
        List<SeckillSessionEntity> sessions = query().list();
        List<Long> sessionIds = sessions.stream().map(SeckillSessionEntity::getId).collect(Collectors.toList());
        // 获取session到relation的映射
        Map<Long, List<SeckillSkuRelationEntity>> session2Relation = seckillSkuRelationService.getSession2Relation(sessionIds);
        for (SeckillSessionEntity session : sessions) {
            session.setRelationSkus(session2Relation.getOrDefault(session.getId(), new ArrayList<>()));
        }

        return sessions;
    }

}