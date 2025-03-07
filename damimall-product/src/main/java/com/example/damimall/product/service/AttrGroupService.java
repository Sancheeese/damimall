package com.example.damimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.damimall.product.entity.AttrAttrgroupRelationEntity;
import com.example.damimall.product.entity.AttrGroupEntity;
import com.example.damimall.product.vo.AttrGroupWithAttrVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:49
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCatId(Map<String, Object> params, Long catId);

    AttrGroupEntity getInfo(Long attrGroupId);

    List<AttrGroupWithAttrVo> getAllGroupWithAttr(Long catId);
}

