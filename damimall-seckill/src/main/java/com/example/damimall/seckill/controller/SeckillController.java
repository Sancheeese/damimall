package com.example.damimall.seckill.controller;

import com.example.common.to.seckill.SeckillSkuRedisTo;
import com.example.common.utils.R;
import com.example.damimall.seckill.service.SeckillService;
import feign.Param;
import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SeckillController {
    @Autowired
    SeckillService seckillService;

    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R currentSeckillSkus(){
        List<SeckillSkuRedisTo> tos = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(tos);
    }

    @ResponseBody
    @GetMapping("/seckillInfo/{skuId}")
    public R seckillInfo(@PathVariable("skuId") Long skuId){
        SeckillSkuRedisTo to = seckillService.getSeckillInfo(skuId);
        return R.ok().setData(to);
    }

    @GetMapping("/kill")
    public String seckill(@RequestParam("killId") String killId,
                          @RequestParam("key") String key,
                          @RequestParam("num") Integer num,
                          Model model){
        String orderSn = seckillService.seckill(killId, key, num);
        model.addAttribute("orderSn", orderSn);
        return "success";
    }
}
