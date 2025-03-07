package com.example.damimall.product.dao;

import com.example.damimall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 * 
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:49
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {
    void updateCatName(@Param("id") Long id, @Param("name") String name);

    void updateBrandName(@Param("id") Long id, @Param("name") String name);
}
