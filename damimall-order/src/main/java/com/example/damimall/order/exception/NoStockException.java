package com.example.damimall.order.exception;

public class NoStockException extends RuntimeException{
    public NoStockException(){
        super("锁定库存失败");
    }

    public NoStockException(Long skuId){
        super("商品号为" + skuId + "的商品没有库存");
    }
}
