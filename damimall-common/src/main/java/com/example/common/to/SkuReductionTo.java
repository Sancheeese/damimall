package com.example.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuReductionTo {
    private Long skuId;

    private int fullCount;

    private BigDecimal discount;

    private BigDecimal price;

    private BigDecimal fullPrice;

    private int countStatus;

    private BigDecimal reducePrice;

    private int priceStatus;

    private List<MemberPrice> memberPrice;
}
