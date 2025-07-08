package com.example.damimall.order.feign;

import com.example.damimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("damimall-cart")
public interface CartFeign {
    @GetMapping("/getItems")
    public List<OrderItemVo> getUserItems();
}
