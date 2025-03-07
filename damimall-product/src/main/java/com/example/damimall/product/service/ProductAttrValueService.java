package com.example.damimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.damimall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:49
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveProductAttrValue(ProductAttrValueEntity productAttrValue);

    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> productAttrValues);

    List<ProductAttrValueEntity> searchAvailableAttrs(Long spuId);
}

