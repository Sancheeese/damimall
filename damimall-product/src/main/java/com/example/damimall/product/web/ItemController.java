package com.example.damimall.product.web;

import com.example.damimall.product.service.SkuInfoService;
import com.example.damimall.product.vo.itemVo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ItemController {
    @Autowired
    SkuInfoService skuInfoService;

    @RequestMapping("/{skuId}.html")
    public String getItemPage(@PathVariable Long skuId, Model model){
        SkuItemVo skuItemVo = skuInfoService.item(skuId);
        model.addAttribute("item", skuItemVo);
        return "item";
    }
}
