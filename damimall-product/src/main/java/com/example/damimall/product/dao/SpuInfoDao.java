package com.example.damimall.product.dao;

import com.example.common.to.product.WeightTo;
import com.example.damimall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * spu信息
 * 
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:50
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {
    @MapKey("sku_id")
    List<WeightTo> getWeight(@Param("skuId") List<Long> skuId);

    @MapKey("sku_id")
    Map<Long, Map<String, Object>> getBatchSpuBySkuId(@Param("skuId") List<Long> spuIds);
}
