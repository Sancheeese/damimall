package com.example.damimall.product.feign;

import com.example.common.utils.R;
import com.example.damimall.product.feign.fallback.SeckillFeignFallBack;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(value = "damimall-seckill", fallback = SeckillFeignFallBack.class)
public interface SeckillFeign {
    @ResponseBody
    @GetMapping("/seckillInfo/{skuId}")
    public R seckillInfo(@PathVariable("skuId") Long skuId);
}
