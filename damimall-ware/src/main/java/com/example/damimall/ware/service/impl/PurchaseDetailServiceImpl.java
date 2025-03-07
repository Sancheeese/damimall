package com.example.damimall.ware.service.impl;

import com.example.common.utils.ParamUtils;
import com.example.common.utils.R;
import com.example.damimall.ware.feign.ProductFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.ware.dao.PurchaseDetailDao;
import com.example.damimall.ware.entity.PurchaseDetailEntity;
import com.example.damimall.ware.service.PurchaseDetailService;
import org.springframework.transaction.annotation.Transactional;

import javax.management.ObjectName;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {
    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                new QueryWrapper<PurchaseDetailEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils listByCondition(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<>();

        if (!ParamUtils.isNullOrEmpty(params, "key")){
            String key = (String) params.get("key");
            queryWrapper.and(qw -> qw.eq("purchase_id", key).or().eq("sku_id", key));
        }

        if (!ParamUtils.isNullOrEmpty(params, "status")){
            String status = (String) params.get("status");
            queryWrapper.eq("status", status);
        }

        if (!ParamUtils.isNullOrEmpty(params, "wareId")){
            String wareId = (String) params.get("wareId");
            queryWrapper.eq("ware_id", wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void savePurchaseDetail(PurchaseDetailEntity purchaseDetail) {
        R r = productFeignService.getSkuById(purchaseDetail.getSkuId());
        Map<String, Object> map = (Map<String, Object>) r.get("skuInfo");
        purchaseDetail.setSkuPrice(new BigDecimal(map.get("price").toString()));
        save(purchaseDetail);
    }

}