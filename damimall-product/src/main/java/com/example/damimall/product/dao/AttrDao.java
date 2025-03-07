package com.example.damimall.product.dao;

import com.example.damimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:49
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> getSearchIdsFormAllIds(@Param("ids") List<Long> allIds);
}
