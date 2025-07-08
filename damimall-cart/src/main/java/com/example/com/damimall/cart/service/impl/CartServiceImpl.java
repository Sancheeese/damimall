package com.example.com.damimall.cart.service.impl;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.C;
import com.example.com.damimall.cart.config.ThreadPoolConfig;
import com.example.com.damimall.cart.constant.CartConstant;
import com.example.com.damimall.cart.feign.ProductFeignService;
import com.example.com.damimall.cart.service.CartService;
import com.example.com.damimall.cart.utils.UserInfoUtils;
import com.example.com.damimall.cart.vo.Cart;
import com.example.com.damimall.cart.vo.CartItem;
import com.example.com.damimall.cart.vo.UserInfoVo;
import com.example.common.to.cart.SkuInfoTo;
import com.example.common.utils.ObjectMapperUtils;
import com.example.common.utils.R;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public void addToCart(Long skuId, Integer num) {
        // 构造key
        String cartKey = CartConstant.CART_PREFIX;
        UserInfoVo user = UserInfoUtils.getUser();
        if (user == null) return ;
        if (user.getUserId() != null) cartKey += user.getUserId();
        else if (user.getUserKey() != null) cartKey += user.getUserKey();
        else return ;

        // 如果商品已经在购物车则数量加一
        Object ret = redisTemplate.opsForHash().get(cartKey, skuId.toString());
        if (ret != null){
            String itemJson = ret.toString();
            CartItem cartItem = ObjectMapperUtils.readValue(itemJson, new TypeReference<CartItem>() {});
            cartItem.setCount(cartItem.getCount() + num);
            redisTemplate.opsForHash().put(cartKey, skuId.toString(), ObjectMapperUtils.writeValueAsString(cartItem));

            return;
        }

        // 如果不在则添加一个
        CartItem cartItem = new CartItem();
        // 得到skuInfo信息
        CompletableFuture<Void> infoFuture = CompletableFuture.runAsync(() -> {
            SkuInfoTo skuInfo = getSkuInfo(skuId);
            if (skuInfo == null) return;
            cartItem.setSkuId(skuInfo.getSkuId());
            cartItem.setImage(skuInfo.getSkuDefaultImg());
            cartItem.setTitle(skuInfo.getSkuTitle());
            cartItem.setPrice(skuInfo.getPrice());
            cartItem.setCount(num);
        }, executor);

        // 得到销售属性信息

        CompletableFuture<Void> attrFuture = CompletableFuture.runAsync(() -> {
            List<String> attrs = productFeignService.getNameAndValue(skuId);
            cartItem.setSkuAttrValues(attrs);
        }, executor);

        try {
            CompletableFuture.allOf(infoFuture, attrFuture).get();

            // 存进redis
            String cartJson = ObjectMapperUtils.writeValueAsString(cartItem);
            redisTemplate.opsForHash().put(cartKey, skuId.toString(), cartJson);
            redisTemplate.expire(cartKey, CartConstant.TEMP_USER_KEY_EXPIRE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CartItem getCartItemBySkuId(Long skuId) {
        // 构造key
        String cartKey = CartConstant.CART_PREFIX;
        UserInfoVo user = UserInfoUtils.getUser();
        if (user == null) return null;
        if (user.getUserId() != null) cartKey += user.getUserId();
        else if (user.getUserKey() != null) cartKey += user.getUserKey();
        else return null;

        String itemJson = redisTemplate.opsForHash().get(cartKey, skuId.toString()).toString();
        return ObjectMapperUtils.readValue(itemJson, new TypeReference<CartItem>() {});
    }

    public void putItem(CartItem item){
        // 构造key
        String cartKey = getCartKey();

        redisTemplate.opsForHash().put(cartKey, item.getSkuId().toString(), ObjectMapperUtils.writeValueAsString(item));
    }

    private SkuInfoTo getSkuInfo(Long skuId) {
        R r = productFeignService.info(skuId);
        if (r != null){
            SkuInfoTo infoTo = r.getWithKey("skuInfo", new TypeReference<SkuInfoTo>() {});
            return infoTo;
        }
        return null;
    }

    @Override
    public List<CartItem> getCartItems() {
        List<CartItem> items = new ArrayList<>();
        UserInfoVo user = UserInfoUtils.getUser();
        if (user == null) return items;

        if (user.getUserKey() != null) {
            List<Object> tmpUserCart = redisTemplate.opsForHash()
                    .values(CartConstant.CART_PREFIX + user.getUserKey());
            for (Object o : tmpUserCart) {
                String itemJson = o.toString();
                CartItem cartItem = ObjectMapperUtils.readValue(itemJson, new TypeReference<CartItem>() {});
                items.add(cartItem);
            }
        }

        Long userId = user.getUserId();
        if (userId != null){
            List<Object> userCart = redisTemplate.opsForHash()
                    .values(CartConstant.CART_PREFIX + userId);

            List<CartItem> tmpUserCart = new ArrayList<>(items);
            for (Object o : userCart) {
                String itemJson = o.toString();
                CartItem cartItem = ObjectMapperUtils.readValue(itemJson, new TypeReference<CartItem>() {});
                items.add(cartItem);
            }

            // 合并临时购物车
            for (CartItem item : tmpUserCart) {
                String itemJson = ObjectMapperUtils.writeValueAsString(item);
                redisTemplate.opsForHash()
                        .put(CartConstant.CART_PREFIX + user.getUserKey(), item.getSkuId().toString(), itemJson);
                // 删除临时购物车
                redisTemplate.delete(CartConstant.CART_PREFIX + user.getUserKey());
            }
        }

        return items;
    }

    @Override
    public Cart getCart() {
        Cart cart = new Cart();
        cart.setItems(getCartItems());
        cart.setReduce(new BigDecimal(1));

        return cart;
    }

    @Override
    public void checkItem(Long skuId, Integer checked) {
        CartItem item = getCartItemBySkuId(skuId);
        item.setCheck(checked == 1);
        putItem(item);
    }

    @Override
    public void countItem(Long skuId, Integer num) {
        CartItem item = getCartItemBySkuId(skuId);
        item.setCount(num);
        putItem(item);
    }

    @Override
    public void deleteItem(Long skuId) {
        String cartKey = getCartKey();
        redisTemplate.opsForHash().delete(cartKey, skuId.toString());
    }

    @Override
    public List<CartItem> getUserItems() {
        UserInfoVo user = UserInfoUtils.getUser();
        if (user == null || user.getUserId() == null) return new ArrayList<>();
        List<CartItem> items = redisTemplate.opsForHash().values(CartConstant.CART_PREFIX + user.getUserId())
                .stream()
                .map(item -> {
                    return ObjectMapperUtils.readValue(item.toString(), new TypeReference<CartItem>() {
                    });
                })
                .filter(CartItem::getCheck)
                .collect(Collectors.toList());
        return items;
    }

    public String getCartKey(){
        // 构造key
        String cartKey = CartConstant.CART_PREFIX;
        UserInfoVo user = UserInfoUtils.getUser();
        if (user == null) return null;
        if (user.getUserId() != null) cartKey += user.getUserId();
        else if (user.getUserKey() != null) cartKey += user.getUserKey();
        else return null;

        return cartKey;
    }

}
