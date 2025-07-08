package com.example.damimall.ware.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("damimall-order")
public interface OrderFeign {
    @GetMapping("/order/order/status")
    public Integer getStatusBySn(@RequestParam("orderSn") String orderSn);
}
