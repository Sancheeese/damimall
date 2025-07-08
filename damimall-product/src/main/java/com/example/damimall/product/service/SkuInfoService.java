package com.example.damimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.to.order.OrderItemTo;
import com.example.common.utils.PageUtils;
import com.example.damimall.product.entity.SkuInfoEntity;
import com.example.damimall.product.vo.itemVo.SkuItemVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:49
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoList);

    PageUtils listByCondition(Map<String, Object> params);

    SkuInfoEntity queryOneById(Long skuId);

    SkuItemVo item(Long skuId);

    List<OrderItemTo> getOrderSkuInfo(List<Long> skuIds);

    List<SkuInfoEntity> getBatchInfo(List<Long> ids);
}

