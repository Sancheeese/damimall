package com.example.damimall.member.feign;

import com.example.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@FeignClient("damimall-order")
public interface OrderFeign {
    @GetMapping("/order/order/listItems")
    @ResponseBody
    public R listItems (@RequestParam Map<String, Object> param);
}
