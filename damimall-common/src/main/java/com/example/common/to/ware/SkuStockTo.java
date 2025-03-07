package com.example.common.to.ware;

import lombok.Data;

@Data
public class SkuStockTo {
    private Long skuId;

    private Integer Stock;

    private String skuName;
}
