package com.example.damimall.order.feign;

import com.example.common.utils.R;
import com.example.damimall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("damimall-member")
public interface MemberFeign {
    @GetMapping("/member/memberreceiveaddress/getAddress")
    public List<MemberAddressVo> getAddressById(@RequestParam("id") Long id);

    @RequestMapping("/member/member/info/{id}")
    public R info(@PathVariable("id") Long id);
}
