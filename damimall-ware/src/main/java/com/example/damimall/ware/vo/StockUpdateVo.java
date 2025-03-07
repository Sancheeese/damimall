package com.example.damimall.ware.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StockUpdateVo {
    private Long skuId;

    private Long wareId;

    private Integer addStock;
}
