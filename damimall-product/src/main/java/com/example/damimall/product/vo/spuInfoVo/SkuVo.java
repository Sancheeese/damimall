package com.example.damimall.product.vo.spuInfoVo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuVo
{
    private List<Attr> attr;

    private String skuName;

    private BigDecimal price;

    private String skuTitle;

    private String skuSubtitle;

    private List<Image> images;

    private List<String> descar;

    private int fullCount;

    private BigDecimal discount;

    private int countStatus;

    private BigDecimal fullPrice;

    private BigDecimal reducePrice;

    private int priceStatus;

    private List<MemberPrice> memberPrice;
}

