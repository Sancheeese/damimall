package com.example.damimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayVo {
    private String orderSn;

    private BigDecimal totalAmount;
}
