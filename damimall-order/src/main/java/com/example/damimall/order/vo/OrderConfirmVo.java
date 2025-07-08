package com.example.damimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class OrderConfirmVo {
    @Setter @Getter
    private List<MemberAddressVo> memberAddressVos;

    @Getter
    private List<OrderItemVo> items;

    @Getter
    private Integer integration = 0;

    private BigDecimal total;

    private BigDecimal payPrice;

    @Setter @Getter
    private String OrderToken;

    @Setter @Getter
    private Map<Long, Boolean> stocks;

    private Integer count;

    public void setIntegration(Integer integration){
        if (payPrice != null){
            payPrice = payPrice.add(new BigDecimal(this.integration / 1000));
            payPrice = payPrice.subtract(new BigDecimal(integration / 1000));
        }
        this.integration = integration;
    }

    public void setItems(List<OrderItemVo> items){
        this.items = items;
        if (items != null && !items.isEmpty()) {
            BigDecimal sum = new BigDecimal(0);
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount()));
                sum = sum.add(multiply);
            }
            total = sum;

            if (integration != null) payPrice = total.subtract(new BigDecimal(integration / 1000));
            else payPrice = total;

            count = 0;
            for (OrderItemVo item : items) {
                count += item.getCount();
            }
        }
    }

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal(0);
        if (items != null && !items.isEmpty()) {
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }

    public BigDecimal getPayPrice() {
        return getTotal();
    }

    public Integer getCount(){
        count = 0;
        if (items != null && !items.isEmpty()) {
            for (OrderItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }
}
