package com.example.damimall.order.feign;

import com.example.common.utils.R;
import com.example.damimall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("damimall-ware")
public interface WareFeign {
    @RequestMapping("/ware/waresku/queryStock")
    public R queryStock(@RequestBody List<Long> skuIds);

    @GetMapping("/ware/wareinfo/fare")
    public R fare(@RequestParam("addrId") Long addressId);

    @GetMapping("/ware/waresku/hello")
    public String hello();

    @PostMapping("/ware/waresku/lockStock")
    public R lockStock(@RequestBody WareSkuLockVo wareSkuLockVo);
}
