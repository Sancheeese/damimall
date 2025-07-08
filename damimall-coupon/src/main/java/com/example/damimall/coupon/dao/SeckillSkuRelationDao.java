package com.example.damimall.coupon.dao;

import com.example.damimall.coupon.entity.SeckillSkuRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 秒杀活动商品关联
 * 
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-08 00:00:09
 */
@Mapper
public interface SeckillSkuRelationDao extends BaseMapper<SeckillSkuRelationEntity> {

}
