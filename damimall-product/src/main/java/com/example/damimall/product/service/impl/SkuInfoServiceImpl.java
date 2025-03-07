package com.example.damimall.product.service.impl;

import com.example.common.utils.ParamUtils;
import com.example.damimall.product.entity.SpuInfoEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.product.dao.SkuInfoDao;
import com.example.damimall.product.entity.SkuInfoEntity;
import com.example.damimall.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfo) {
        save(skuInfo);
    }

    @Override
    public PageUtils listByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();

        if (!ParamUtils.isNullOrEmpty(params, "key")){
            String key = (String) params.get("key");
            queryWrapper.and(qw -> qw.eq("id", key).or().eq("spu_name", key));
        }

        if (ParamUtils.isAvailable(params, "brandId")){
            String brandId = (String) params.get("brandId");
            queryWrapper.eq("brand_id", brandId);
        }

        if (ParamUtils.isAvailable(params, "catelogId")){
            String catelogId = (String) params.get("catelogId");
            queryWrapper.eq("catelog_id", catelogId);
        }

        if (!ParamUtils.isNullOrEmpty(params, "min")){
            String min = (String) params.get("min");
            queryWrapper.ge("price", min);
        }

        if (ParamUtils.isAvailable(params, "max")){
            String max = (String) params.get("max");
            queryWrapper.le("price", max);
        }



        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public SkuInfoEntity queryOneById(Long skuId) {
        return getById(skuId);
    }

}