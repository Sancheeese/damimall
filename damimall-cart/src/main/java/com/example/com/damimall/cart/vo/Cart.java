package com.example.com.damimall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

public class Cart {
    List<CartItem> items;

    private Integer countNum;
    
    private Integer countType;

    private BigDecimal totalAmount;

    private BigDecimal reduce = new BigDecimal(0);

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;

        // 计算种类
        countType = 0;
        if (items != null && !items.isEmpty()){
            for (CartItem item : items) {
                countType++;
            }
        }

        // 计算件数
        countNum = 0;
        if (items != null && !items.isEmpty()){
            for (CartItem item : items) {
                countNum += item.getCount();
            }
        }

        // 计算价格
        BigDecimal total = new BigDecimal(0);
        if (items != null && !items.isEmpty()){
            for (CartItem item : items) {
                if (item.getCheck()) {
                    total = total.add(item.getPrice().multiply(new BigDecimal(item.getCount())));
                }
            }
        }

        totalAmount = reduce == null ? total : total.subtract(reduce);
    }

    public Integer getCountNum() {
        countNum = 0;
        if (items != null && !items.isEmpty()){
            for (CartItem item : items) {
                countNum += item.getCount();
            }
        }
        return countNum;
    }

    public Integer getCountType() {
        countType = 0;
        if (items != null && !items.isEmpty()){
            for (CartItem item : items) {
                countType++;
            }
        }

        return countType;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal total = new BigDecimal(0);
        if (items != null && !items.isEmpty()){
            for (CartItem item : items) {
                if (item.getCheck()) {
                    total = total.add(item.getPrice().multiply(new BigDecimal(item.getCount())));
                }
            }
        }

        total = total.subtract(getReduce());

        return total;
    }


    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        if (totalAmount != null){
            totalAmount = totalAmount.add(this.reduce);
            totalAmount = totalAmount.subtract(reduce);
        }
        this.reduce = reduce;
    }
}
