package com.example.damimall.product.service.impl;

import com.example.common.constant.ProductConstant;
import com.example.common.to.SkuReductionTo;
import com.example.common.to.SpuBoundTo;
import com.example.common.to.search.SkuEsTo;
import com.example.common.to.ware.SkuStockTo;
import com.example.common.utils.ParamUtils;
import com.example.common.utils.R;
import com.example.damimall.product.entity.*;
import com.example.damimall.product.feign.CouponFeignService;
import com.example.damimall.product.feign.SearchFeignService;
import com.example.damimall.product.feign.WareFeignService;
import com.example.damimall.product.service.*;
import com.example.damimall.product.vo.spuInfoVo.*;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    SpuImagesService spuImagesService;

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuVo spuVo) {
        // 保存基本信息
        SpuInfoEntity spuInfo = new SpuInfoEntity();
        BeanUtils.copyProperties(spuVo, spuInfo);
        spuInfo.setCreateTime(new Date());
        spuInfo.setUpdateTime(new Date());
        save(spuInfo);
        Long spuId = spuInfo.getId();// 保存后会将自增的id回填到spuInfo

        // 描述
        SpuInfoDescEntity spuInfoDesc = new SpuInfoDescEntity();
        spuInfoDesc.setSpuId(spuId);
        spuInfoDesc.setDecript(spuVo.getSpuDescription());
        spuInfoDescService.saveSpuInfoDesc(spuInfoDesc);

        // 图片集
        spuImagesService.saveBySpuId(spuId, spuVo.getImages());

        // 规格参数
        for (BaseAttr baseAttr : spuVo.getBaseAttrs()) {
            ProductAttrValueEntity productAttrValue = new ProductAttrValueEntity();
            productAttrValue.setSpuId(spuId);
            productAttrValue.setAttrId(baseAttr.getAttrId());
            productAttrValue.setAttrValue(baseAttr.getAttrValues());
            productAttrValue.setAttrSort(0);
            productAttrValue.setQuickShow(baseAttr.getShowDesc());
            AttrEntity attr = attrService.getById(baseAttr.getAttrId());
            if (attr != null) productAttrValue.setAttrName(attr.getAttrName());

            productAttrValueService.saveProductAttrValue(productAttrValue);
        }

        // spu的积分信息
        Bounds bounds = spuVo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuId);
        R r = couponFeignService.saveBound(spuBoundTo);
        if (r.getCode() != 0) log.error("保存spu积分信息出错");

        // 对应sku
        // sku基本信息
        for (SkuVo skuVo : spuVo.getSkus()) {
            String defaultImage = null;
            for (Image img : skuVo.getImages()) {
                if (img.getDefaultImg() == 1) defaultImage = img.getImgUrl();
            }

            SkuInfoEntity skuInfo = new SkuInfoEntity();
            skuInfo.setSpuId(spuId);
            skuInfo.setSkuName(skuVo.getSkuName());
            skuInfo.setCatelogId(spuVo.getCatalogId());
            skuInfo.setBrandId(spuVo.getBrandId());
            skuInfo.setSkuDefaultImg(defaultImage);
            skuInfo.setSkuTitle(skuVo.getSkuTitle());
            skuInfo.setSkuSubtitle(skuVo.getSkuSubtitle());
            skuInfo.setPrice(skuVo.getPrice());
            skuInfo.setSaleCount(0L);
            skuInfoService.saveSkuInfo(skuInfo);
            Long skuId = skuInfo.getSkuId();

            // sku图片信息
            List<SkuImagesEntity> skuImages = new ArrayList<>();
            for (Image image : skuVo.getImages()) {
                if (image.getImgUrl() != null && image.getImgUrl() != "") {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(image.getImgUrl());
                    skuImagesEntity.setDefaultImg(image.getDefaultImg());
                    skuImagesEntity.setImgSort(0);
                    skuImages.add(skuImagesEntity);
                }
            }
            skuImagesService.saveImages(skuImages);

            // sku销售属性
            List<SkuSaleAttrValueEntity> skuSaleAttrValues = new ArrayList<>();
            for (Attr attr : skuVo.getAttr()) {
                SkuSaleAttrValueEntity skuSaleAttrValue = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(attr, skuSaleAttrValue);
                skuSaleAttrValue.setSkuId(skuId);
                skuSaleAttrValue.setAttrSort(0);
                skuSaleAttrValues.add(skuSaleAttrValue);
            }
            skuSaleAttrValueService.saveSkuSaleAttrValue(skuSaleAttrValues);

            // sku优惠满减信息
            SkuReductionTo skuReductionTo = new SkuReductionTo();
            BeanUtils.copyProperties(skuVo, skuReductionTo);
            skuReductionTo.setSkuId(skuId);
            skuReductionTo.setMemberPrice(member2memberTo(skuVo.getMemberPrice()));
            R r2 = couponFeignService.saveForFeign(skuReductionTo);
            if (r2.getCode() != 0) log.error("保存sku优惠信息错误");
        }
    }

    public List<com.example.common.to.MemberPrice> member2memberTo(List<MemberPrice> memberPrices){
        List<com.example.common.to.MemberPrice> ret = new ArrayList<>();
        for (MemberPrice memberPrice : memberPrices) {
            com.example.common.to.MemberPrice mem = new com.example.common.to.MemberPrice();
            BeanUtils.copyProperties(memberPrice, mem);
            ret.add(mem);
        }
        return ret;
    }

    @Override
    public PageUtils listByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

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

        if (!ParamUtils.isNullOrEmpty(params, "status")){
            String status = (String) params.get("status");
            queryWrapper.eq("publish_status", status);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        
        return new PageUtils(page);
    }

    @Override
    public void spuUp(Long spuId) {
        // 查出skuInfo
        List<SkuInfoEntity> skuInfoList = skuInfoService.query().eq("spu_id", spuId).list();

        // 查出所有可被检索的属性
        List<ProductAttrValueEntity> attrs = productAttrValueService.searchAvailableAttrs(spuId);
        List<SkuEsTo.Attr> searchAttrList = attrs.stream()
                .map(attr -> {
                    SkuEsTo.Attr searchAttr = new SkuEsTo.Attr();
                    BeanUtils.copyProperties(attr, searchAttr);
                    return searchAttr;
                }).collect(Collectors.toList());

        // 查sku库存
        Map<Long, Integer> stockMap = new HashMap<>();
        try {
            List<Long> skuIds = skuInfoList.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
            R r = wareFeignService.queryStock(skuIds);
            List<SkuStockTo> skuStockTos = r.getData(new TypeReference<List<SkuStockTo>>() {});
            for (SkuStockTo skuStockTo : skuStockTos) {
                stockMap.put(skuStockTo.getSkuId(), skuStockTo.getStock());
            }
        }catch (Exception e){
            log.error("远程调用ware查库存失败，将所有上架商品库存设为0");
        }

        List<SkuEsTo> skuEsToList = skuInfoList.stream()
                .map(sku -> {
                    SkuEsTo skuEsTo = new SkuEsTo();
                    BeanUtils.copyProperties(sku, skuEsTo);
                    skuEsTo.setSkuImg(sku.getSkuDefaultImg());
                    skuEsTo.setSkuPrice(sku.getPrice());

                    // hotScore, hasStock
                    skuEsTo.setHotScore(0L);// 先默认是0
                    if (stockMap.containsKey(sku.getSkuId())) {
                        skuEsTo.setHasStock(stockMap.get(sku.getSkuId()) > 0 ? true : false);
                    }else{
                        skuEsTo.setHasStock(false);
                    }

                    // brandName, catelogName, brandImg
                    BrandEntity brand  = brandService.getById(sku.getBrandId());
                    if (brand != null){
                        skuEsTo.setBrandName(brand.getName());
                        skuEsTo.setBrandImg(brand.getLogo());
                    }
                    CategoryEntity category = categoryService.getById(sku.getCatelogId());
                    if (category != null) skuEsTo.setCatelogName(category.getName());

                    // attrs
                    skuEsTo.setAttrs(searchAttrList);

                    return skuEsTo;
                })
                .collect(Collectors.toList());


        boolean success = false;
        try {
            success = searchFeignService.saveProduct(skuEsToList);
        }catch (Exception e){
            log.error("远程调用elasticsearch失败");
        }
        if (success) {
            // 修改商品状态
            SpuInfoEntity spuInfo = new SpuInfoEntity();
            spuInfo.setId(spuId);
            spuInfo.setUpdateTime(new Date());
            spuInfo.setPublishStatus(ProductConstant.ProductStatus.PRODUCT_UP.getCode());

            spuInfoService.updateById(spuInfo);
        }else{
            log.error("商品上架保存至elasticsearch出错");
        }

        Deque q;
    }
}
