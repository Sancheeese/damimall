package com.example.damimall.product.web;

import com.example.damimall.product.entity.CategoryEntity;
import com.example.damimall.product.service.CategoryService;
import com.example.damimall.product.vo.webVo.Category2LevelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @RequestMapping({"/", "/index.html"})
    public String indexPage(Model model){
        // 获取分类
        List<CategoryEntity> categoryEntityList = categoryService.getFirstLevelCategory();

        model.addAttribute("categories", categoryEntityList);
        return "index";
    }

    @ResponseBody
    @RequestMapping("/index/catalog.json")
    public Map<String, List<Category2LevelVo>> getCatalogJson(){
        return categoryService.getCatalogJson();
    }
}
