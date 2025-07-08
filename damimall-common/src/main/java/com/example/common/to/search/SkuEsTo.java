package com.example.common.to.search;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuEsTo {
    private Long skuId;

    private Long spuId;

    private String skuTitle;

    private BigDecimal skuPrice;

    private String skuImg;

    private Long saleCount;

    private Long hotScore;

    private Boolean hasStock;

    private Long brandId;

    private Long catelogId;

    private String brandName;

    private String catelogName;

    private String brandImg;

    private List<Attr> attrs;

    @Data
    public static class Attr{
        private Long attrId;

        private String attrName;

//        private String attrValue;
        private List<String> attrValue;
    }
}
