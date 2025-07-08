package com.example.damimall.product.vo.itemVo;

import com.example.damimall.product.entity.SkuImagesEntity;
import com.example.damimall.product.entity.SkuInfoEntity;
import com.example.damimall.product.entity.SpuInfoDescEntity;
import com.example.damimall.product.vo.seckillVo.SeckillInfoVo;
import lombok.Data;

import java.util.List;

@Data
// 商品详情页数据
public class SkuItemVo {
    private SkuInfoEntity info;

    private List<SkuImagesEntity> images;

    private SpuInfoDescEntity desc;

    // 销售属性
    private List<SaleAttrItemVo> saleAttr;

    // 规格属性
    private List<AttrGroupItemVo> groupAttrs;

//    private SeckillSkuVo seckillSku = new SeckillSkuVo();

    private boolean hasStock = true;

    private SeckillInfoVo seckillSku;
}
