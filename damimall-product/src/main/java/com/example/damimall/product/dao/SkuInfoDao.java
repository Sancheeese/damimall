package com.example.damimall.product.dao;

import com.example.damimall.product.entity.SkuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku信息
 * 
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:49
 */
@Mapper
public interface SkuInfoDao extends BaseMapper<SkuInfoEntity> {

    List<Long> getSkuIdsByCatId(@Param("catId") Long catId);

    List<Long> getSkuIdsByBrandId(@Param("brandId") Long brandId);
}
