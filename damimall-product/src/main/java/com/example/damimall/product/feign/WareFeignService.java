package com.example.damimall.product.feign;

import com.example.common.to.ware.SkuStockTo;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("damimall-ware")
public interface WareFeignService {
    @RequestMapping("/ware/waresku/queryStock")
    public R queryStock(@RequestBody List<Long> skuIds);
}
