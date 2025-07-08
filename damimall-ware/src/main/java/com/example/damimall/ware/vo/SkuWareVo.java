package com.example.damimall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class SkuWareVo {
    private Long skuId;

    private List<Long> wareId;
}
