package com.example.damimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.damimall.product.entity.AttrEntity;
import com.example.damimall.product.entity.ProductAttrValueEntity;
import com.example.damimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:49
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils listAttr(String type, Map<String, Object> params, Long attrId);

    void saveAttr(AttrVo attr);

    AttrVo info(Long attrId);

    void updateAttr(AttrVo attr);

    void deleteAttr(Long[] attrIds);

    PageUtils getNoRelation(Map<String, Object> params, Long attrGroupId);

    List<AttrVo> getRelation(Long groupId);

    List<ProductAttrValueEntity> getBaseAttrBySpuId(Long spuId);

    List<Long> getSearcIdsFormAllIds(List<Long> allIds);
}

