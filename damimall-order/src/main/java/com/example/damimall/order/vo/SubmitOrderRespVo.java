package com.example.damimall.order.vo;

import com.example.damimall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderRespVo {
    private OrderEntity order;

    private Integer code;
}
