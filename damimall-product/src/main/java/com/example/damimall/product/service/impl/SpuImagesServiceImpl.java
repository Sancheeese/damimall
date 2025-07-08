package com.example.damimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.product.dao.SpuImagesDao;
import com.example.damimall.product.entity.SpuImagesEntity;
import com.example.damimall.product.service.SpuImagesService;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBySpuId(Long spuId, List<String> images) {
        if (images == null || images.isEmpty()) return ;
        for (String img : images){
            SpuImagesEntity spuImage = new SpuImagesEntity();
            spuImage.setSpuId(spuId);
            spuImage.setImgUrl(img);
            save(spuImage);
        }
    }

    @Override
    public Map<Long, String> getDefaultImg(List<Long> spuIds) {
        List<SpuImagesEntity> spuImages = query().in("spu_id", spuIds).list();
        Map<Long, String> spuImgsMap = new HashMap<>();
        for (SpuImagesEntity spuImage : spuImages) {
            if (spuImage.getDefaultImg() == null) {
                if (!spuImgsMap.containsKey(spuImage.getSpuId()))
                    spuImgsMap.put(spuImage.getSpuId(), spuImage.getImgUrl());
            }else{
                if (spuImage.getDefaultImg() == 1)
                    spuImgsMap.put(spuImage.getSpuId(), spuImage.getImgUrl());
            }
        }

        return spuImgsMap;
    }


}