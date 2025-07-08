package com.example.damimall.product.service.impl;

import com.example.common.to.order.OrderItemTo;
import com.example.common.utils.ParamUtils;
import com.example.common.utils.R;
import com.example.damimall.product.dao.AttrGroupDao;
import com.example.damimall.product.entity.SkuImagesEntity;
import com.example.damimall.product.entity.SpuInfoDescEntity;
import com.example.damimall.product.entity.SpuInfoEntity;
import com.example.damimall.product.feign.SeckillFeign;
import com.example.damimall.product.service.*;
import com.example.damimall.product.vo.itemVo.AttrGroupItemVo;
import com.example.damimall.product.vo.itemVo.SaleAttrItemVo;
import com.example.damimall.product.vo.itemVo.SkuItemVo;
import com.example.damimall.product.vo.seckillVo.SeckillInfoVo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.product.dao.SkuInfoDao;
import com.example.damimall.product.entity.SkuInfoEntity;


@Service("skuInfoService")
@Slf4j
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    SeckillFeign seckillFeign;

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

    @Override
    public SkuItemVo item(Long skuId) {
        long start = System.currentTimeMillis();
        SkuItemVo skuItemVo = new SkuItemVo();
        // 基本信息
        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfo = getById(skuId);
            skuItemVo.setInfo(skuInfo);
            return skuInfo;
        }, executor);

        // 图片
        CompletableFuture<Void> imgFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> skuImgs = skuImagesService.query().eq("sku_id", skuId).list();
            skuItemVo.setImages(skuImgs);
        }, executor);


        // spu描述
        CompletableFuture<Void> spuDescFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            SpuInfoDescEntity spuDesc = spuInfoDescService.query().eq("spu_id", skuInfo.getSpuId()).one();
            skuItemVo.setDesc(spuDesc);
        }, executor);


        // 销售属性
        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            List<SaleAttrItemVo> saleAttrItemVos = skuSaleAttrValueService.getSaleAttrItemBySpuId(skuInfo.getSpuId());
            skuItemVo.setSaleAttr(saleAttrItemVos);
        }, executor);


        // 规格属性
        CompletableFuture<Void> baseAttrFuture = skuInfoFuture.thenAcceptAsync(skuInfo -> {
            List<AttrGroupItemVo> attrGroupItemVos = attrGroupService.getAttrGroupWithAttr(skuInfo.getSpuId());
            skuItemVo.setGroupAttrs(attrGroupItemVos);
        }, executor);

        // 查询秒杀优惠
        CompletableFuture<Void> seckillInfoFuture = CompletableFuture.runAsync(() -> {
            R r = seckillFeign.seckillInfo(skuId);
            if (r == null || r.getCode() != 0) return;
            SeckillInfoVo seckillInfo = r.getData(new TypeReference<SeckillInfoVo>() {});
            skuItemVo.setSeckillSku(seckillInfo);
        }, executor);

        try {
            CompletableFuture.allOf(skuInfoFuture, imgFuture, spuDescFuture, saleAttrFuture, baseAttrFuture, seckillInfoFuture).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        log.info("查询详情页花费了：：：：" + (System.currentTimeMillis() - start) + "毫秒");

        return skuItemVo;
    }

    @Override
    public List<OrderItemTo> getOrderSkuInfo(List<Long> skuIds) {
        List<SkuInfoEntity> skuInfo = query().in("sku_id", skuIds).list();

        // 设置sku信息(除了购买数量和销售属性组合)
        Map<Long, String> skuId2Img = skuImagesService.getDefaultImg(skuIds);
        List<OrderItemTo> tos = skuInfo.stream()
                .map(info -> {
                    OrderItemTo orderItemTo = new OrderItemTo();
                    orderItemTo.setSkuId(info.getSkuId());
                    orderItemTo.setSkuName(info.getSkuName());
                    orderItemTo.setSkuPic(skuId2Img.getOrDefault(info.getSkuId(), ""));
                    orderItemTo.setSkuPrice(info.getPrice());
                    // 一些优惠信息，先默认0
                    orderItemTo.setGiftGrowth(0);
                    orderItemTo.setGiftIntegration(0);
                    orderItemTo.setPromotionAmount(new BigDecimal(0));
                    orderItemTo.setCouponAmount(new BigDecimal(0));
                    orderItemTo.setIntegrationAmount(new BigDecimal(0));

                    // 最终价格
//                    orderItemTo.setRealAmount(orderItemTo.getSkuPrice().multiply()
//                            .subtract(orderItemTo.getPromotionAmount()
//                                    .subtract(orderItemTo.getCouponAmount()
//                                            .subtract(orderItemTo.getIntegrationAmount()))));

                    return orderItemTo;
                })
                .collect(Collectors.toList());

        // 设置spu信息
        Set<Long> spuIdSet = new HashSet<>();
        for (SkuInfoEntity info : skuInfo) {
            spuIdSet.add(info.getSpuId());
        }
        List<Long> spuIds = new ArrayList<>(spuIdSet);
        Map<Long, SpuInfoEntity> sku2Spu = spuInfoService.getBatchSpuBySkuId(skuIds);// spu基本信息
        Map<Long, String> spuId2Img = spuImagesService.getDefaultImg(spuIds);// spu图片信息
        for (OrderItemTo orderItemTo : tos) {
            SpuInfoEntity spuInfo = sku2Spu.get(orderItemTo.getSkuId());
            orderItemTo.setSpuId(spuInfo.getId());
            orderItemTo.setSpuName(spuInfo.getSpuName());
            orderItemTo.setCategoryId(spuInfo.getCatelogId());
            orderItemTo.setSpuPic(spuId2Img.getOrDefault(spuInfo.getId(), ""));
        }

        return tos;
    }

    @Override
    public List<SkuInfoEntity> getBatchInfo(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();
        return query().in("sku_id", ids).list();
    }

}