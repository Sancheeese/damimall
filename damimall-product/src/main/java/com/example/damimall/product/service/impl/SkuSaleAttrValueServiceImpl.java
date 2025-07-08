package com.example.damimall.product.service.impl;

import com.example.common.utils.BatchOptUtils;
import com.example.damimall.product.vo.itemVo.SaleAttrItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.product.dao.SkuSaleAttrValueDao;
import com.example.damimall.product.entity.SkuSaleAttrValueEntity;
import com.example.damimall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {
    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuSaleAttrValue(List<SkuSaleAttrValueEntity> skuSaleAttrValues) {
        new BatchOptUtils<SkuSaleAttrValueEntity>().saveBatch(this, skuSaleAttrValues, 1000);
    }

    @Override
    public List<SaleAttrItemVo> getSaleAttrItemBySpuId(Long spuId) {
        List<SaleAttrItemVo> saleAttrItemVos = skuSaleAttrValueDao.getSaleAttrItemBySpuId(spuId);
        return saleAttrItemVos;
    }

}