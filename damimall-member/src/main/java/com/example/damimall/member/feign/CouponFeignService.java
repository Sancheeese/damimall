package com.example.damimall.member.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("damimall-coupon")
public interface CouponFeignService {
    @RequestMapping("/coupon/coupon/member/test")
    public R memberCoupon();
}
