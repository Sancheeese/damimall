package com.example.common.to.mq;

import lombok.Data;

import java.util.List;

@Data
public class LockStockDetailTo {
    private String orderSn;

    private List<LockDetailTo> details;
}
