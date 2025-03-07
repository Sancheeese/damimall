package com.example.damimall.search.controller;

import com.example.common.to.search.SkuEsTo;
import com.example.damimall.search.service.ProductSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("elasticsearch")
public class EsController {
    @Autowired
    ProductSaveService productSaveService;

    /**
     * 商品上架
     * @param products
     * @return
     */
    @RequestMapping("/saveProduct")
    public boolean saveProduct(@RequestBody List<SkuEsTo> products){
        boolean success = productSaveService.productUp(products);
        return success;
    }

    @RequestMapping("/hello")
    public String Hello(){
        return "hello";
    }
}
