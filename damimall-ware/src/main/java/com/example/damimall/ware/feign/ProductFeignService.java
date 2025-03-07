package com.example.damimall.ware.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("damimall-product")
public interface ProductFeignService {
    @RequestMapping("/product/skuinfo/queryOne")
    public R getSkuById(@RequestParam("skuId") Long skuId);
}
