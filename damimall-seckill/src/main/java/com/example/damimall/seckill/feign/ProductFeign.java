package com.example.damimall.seckill.feign;

import com.example.common.to.seckill.SkuInfoTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("damimall-product")
public interface ProductFeign {
    @PostMapping("/product/skuinfo/batch/info")
    public List<SkuInfoTo> getBatchInfo(@RequestBody List<Long> ids);

}
