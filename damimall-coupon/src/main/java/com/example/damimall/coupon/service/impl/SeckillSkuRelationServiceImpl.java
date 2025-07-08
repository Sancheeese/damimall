package com.example.damimall.coupon.service.impl;

import com.example.common.utils.ParamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.coupon.dao.SeckillSkuRelationDao;
import com.example.damimall.coupon.entity.SeckillSkuRelationEntity;
import com.example.damimall.coupon.service.SeckillSkuRelationService;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {
    @Autowired
    SeckillSkuRelationDao seckillSkuRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SeckillSkuRelationEntity> queryWrapper = new QueryWrapper<>();
        if (!ParamUtils.isNullOrEmpty(params, "promotionSessionId")){
            queryWrapper.eq("promotion_session_id", params.get("promotionSessionId"));
        }

        if (!ParamUtils.isNullOrEmpty(params, "key")){
            queryWrapper.and(q -> q.eq("sku_id", params.get("key")));
        }

        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public Map<Long, List<SeckillSkuRelationEntity>> getSession2Relation(List<Long> sessionIds) {
        Map<Long, List<SeckillSkuRelationEntity>> map = new HashMap<>();
        if (sessionIds == null || sessionIds.isEmpty()) return map;

        List<SeckillSkuRelationEntity> relations = query().in("promotion_session_id", sessionIds).list();
        for (SeckillSkuRelationEntity relation : relations) {
            Long sessionId = relation.getPromotionSessionId();
            if (!map.containsKey(sessionId))
                map.put(sessionId, new ArrayList<>());
            map.get(sessionId).add(relation);
        }
        return map;
    }

}