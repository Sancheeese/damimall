package com.example.damimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.damimall.product.entity.CategoryEntity;
import com.example.damimall.product.vo.webVo.Category2LevelVo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:49
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> treeList();

    void removeMenuByIds(List<Long> asList);

    void saveOne(CategoryEntity category);

    void updateCat(CategoryEntity category);

    List<CategoryEntity> getFirstLevelCategory();

    Map<String, List<Category2LevelVo>> getCatalogJson();
}

