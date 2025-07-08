package com.example.com.damimall.cart.service;

import com.example.com.damimall.cart.vo.Cart;
import com.example.com.damimall.cart.vo.CartItem;

import java.util.List;

public interface CartService {
    void addToCart(Long skuId, Integer num);

    CartItem getCartItemBySkuId(Long skuId);

    List<CartItem> getCartItems();

    Cart getCart();

    void checkItem(Long skuId, Integer checked);

    void countItem(Long skuId, Integer num);

    void deleteItem(Long skuId);

    List<CartItem> getUserItems();
}
