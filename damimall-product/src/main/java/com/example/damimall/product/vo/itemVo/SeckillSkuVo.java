package com.example.damimall.product.vo.itemVo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
public class SeckillSkuVo {
    private Long startTime = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();

    private BigDecimal seckillPrice;

    private Long endTime = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli() + 1000L;
}
