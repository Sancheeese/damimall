package com.example.damimall.product.service.impl;

import com.example.common.utils.BatchOptUtils;
import com.example.damimall.product.entity.SpuImagesEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.product.dao.SkuImagesDao;
import com.example.damimall.product.entity.SkuImagesEntity;
import com.example.damimall.product.service.SkuImagesService;


@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveImages(List<SkuImagesEntity> skuImages) {
        new BatchOptUtils<SkuImagesEntity>().saveBatch(this, skuImages, 1000);
    }

    @Override
    public Map<Long, String> getDefaultImg(List<Long> skuIds) {
        Map<Long, String> id2Img = new HashMap<>();
        List<SkuImagesEntity> skuImgs = query().in("sku_id", skuIds).list();
        for (SkuImagesEntity skuImg : skuImgs) {
            if (skuImg.getDefaultImg() == null) {
                if (!id2Img.containsKey(skuImg.getSkuId()))
                    id2Img.put(skuImg.getSkuId(), skuImg.getImgUrl());
            }else{
                if (skuImg.getDefaultImg() == 1)
                    id2Img.put(skuImg.getSkuId(), skuImg.getImgUrl());
            }
        }

        return id2Img;
    }

}