package com.example.damimall.order.vo;

import lombok.Data;

@Data
public class PaySubmitVo {
    private String orderSn;

    private String payCode;

    private String businessCode;
}
