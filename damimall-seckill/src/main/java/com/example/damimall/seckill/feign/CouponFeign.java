package com.example.damimall.seckill.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("damimall-coupon")
public interface CouponFeign {
    @GetMapping("/coupon/seckillsession/lastest3Days")
    public R latest3DaysSeckill();
}
