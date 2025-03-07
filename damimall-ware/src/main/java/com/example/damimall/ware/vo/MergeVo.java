package com.example.damimall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVo {
    Long purchaseId;

    List<Long> items;
}
