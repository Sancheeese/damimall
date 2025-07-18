package com.example.damimall.product.service.impl;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.example.common.to.search.SkuEsTo;
import com.example.damimall.product.dao.CategoryBrandRelationDao;
import com.example.damimall.product.dao.SkuInfoDao;
import com.example.damimall.product.entity.CategoryBrandRelationEntity;
import com.example.damimall.product.feign.SearchFeignService;
import com.example.damimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.product.dao.BrandDao;
import com.example.damimall.product.entity.BrandEntity;
import com.example.damimall.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Autowired
    CategoryBrandRelationDao categoryBrandRelationDao;

    @Autowired
    SkuInfoDao skuInfoDao;

    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                new QueryWrapper<BrandEntity>().orderByDesc("sort")
        );

        return new PageUtils(page);
    }

    @Override
    public void updateBrand(BrandEntity brand) {
        // 更新自己的表
        updateById(brand);

        // 更新相关的表
        categoryBrandRelationDao.updateBrandName(brand.getBrandId(), brand.getName());

        // 更新es
        List<Long> skuIds = skuInfoDao.getSkuIdsByBrandId(brand.getBrandId());
        List<SkuEsTo> skuEsTos = skuIds.stream().map(id -> {
            SkuEsTo skuEsTo = new SkuEsTo();
            skuEsTo.setSkuId(id);
            skuEsTo.setBrandName(brand.getName());
            skuEsTo.setBrandImg(brand.getLogo());
            return skuEsTo;
        }).collect(Collectors.toList());
        searchFeignService.updateProduct(skuEsTos);
    }

}