package com.example.damimall.product.feign;

import com.example.common.to.SkuReductionTo;
import com.example.common.to.SpuBoundTo;
import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("damimall-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/spubounds/save")
    public R saveBound(@RequestBody SpuBoundTo spuBoundTo);

    @RequestMapping("/coupon/skufullreduction/saveForFeign")
    public R saveForFeign(@RequestBody SkuReductionTo skuReductionTo);
}
