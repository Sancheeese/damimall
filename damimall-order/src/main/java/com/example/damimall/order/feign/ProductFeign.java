package com.example.damimall.order.feign;

import com.example.common.to.order.OrderItemTo;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient("damimall-product")
public interface ProductFeign {
    @PostMapping("/product/spuinfo/getWeight")
    public R getWeight(@RequestBody List<Long> skuId);

    @PostMapping("/product/skuinfo/getOrderSkuInfo")
    public List<OrderItemTo> getOrderSkuInfo(@RequestBody List<Long> skuIds);
}
