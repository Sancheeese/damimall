package com.example.common.to.seckill;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillOrderTo {
    private String orderSn;

    private Long promotionSessionId;

    private Long skuId;

    private BigDecimal seckillPrice;

    private Integer num;

    private Long memberId;
}
