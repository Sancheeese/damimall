package com.example.damimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.damimall.product.entity.SkuSaleAttrValueEntity;
import com.example.damimall.product.vo.itemVo.SaleAttrItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:49
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuSaleAttrValue(List<SkuSaleAttrValueEntity> skuSaleAttrValues);

    List<SaleAttrItemVo> getSaleAttrItemBySpuId(Long spuId);
}

