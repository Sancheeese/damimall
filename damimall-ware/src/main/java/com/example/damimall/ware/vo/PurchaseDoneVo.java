package com.example.damimall.ware.vo;

import lombok.Data;
import java.util.List;

@Data
public class PurchaseDoneVo {
    private Long id;

    private List<NotDonePurchase> items;
}
