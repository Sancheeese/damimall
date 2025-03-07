package com.example.damimall.product.service.impl;

import com.example.damimall.product.entity.BrandEntity;
import com.example.damimall.product.entity.CategoryEntity;
import com.example.damimall.product.service.BrandService;
import com.example.damimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.product.dao.CategoryBrandRelationDao;
import com.example.damimall.product.entity.CategoryBrandRelationEntity;
import com.example.damimall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {
    @Autowired
    CategoryService categoryService;

    @Autowired
    BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryBrandRelationEntity> getRelationCategory(Long brandId) {
        List<CategoryBrandRelationEntity> dataList = this.
                list(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));

        return dataList;
    }

    @Override
    public void saveRealtion(CategoryBrandRelationEntity categoryBrandRelation) {
        // 根据id查询品牌和分类的名字
        Long brandId = categoryBrandRelation.getBrandId();
        Long catId = categoryBrandRelation.getCatelogId();

        BrandEntity brand = brandService.query().eq("brand_id", brandId).one();
        CategoryEntity cat = categoryService.query().eq("cat_id", catId).one();

        categoryBrandRelation.setBrandName(brand.getName());
        categoryBrandRelation.setCatelogName(cat.getName());

        save(categoryBrandRelation);
    }

    @Override
    public List<CategoryBrandRelationEntity> getBrandListByCatId(Long catId) {
        return query().eq("catelog_id", catId).list();
    }

}