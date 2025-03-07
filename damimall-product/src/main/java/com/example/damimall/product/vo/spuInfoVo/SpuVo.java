package com.example.damimall.product.vo.spuInfoVo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuVo
{
    private String spuName;

    private String spuDescription;

    private Long catalogId;

    private Long brandId;

    private BigDecimal weight;

    private int publishStatus;

    private List<String> decript;

    private List<String> images;

    private Bounds bounds;

    private List<BaseAttr> baseAttrs;

    private List<SkuVo> skus;
}
