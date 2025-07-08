package com.example.damimall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.damimall.product.entity.CategoryEntity;
import com.example.damimall.product.service.CategoryService;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.Key;
import java.util.List;

//@SpringBootTest(classes = ProductApplication.class)
public class ProductApplicationTest {
//    @Autowired
//    CategoryService categoryService;

//    @Test
//    void test1(){
//        CategoryEntity category = new CategoryEntity();
//        category.setName("衣服");
//        category.setCatLevel(100);
//        categoryService.save(category);
//
//        CategoryEntity c1 = categoryService.getById(1);
//        CategoryEntity c2 = categoryService.getOne(new QueryWrapper<CategoryEntity>().eq("cat_id", 2));
//
//        List<CategoryEntity> c_l = categoryService.query().eq("cat_id", 2).list();
//
//        System.out.println(c1.getName());
//        System.out.println(c2.getName());
//
//        for (CategoryEntity categoryEntity : c_l){
//            System.out.println(categoryEntity.getName());
//        }
//
//        System.out.println("hello");
//    }

    @Test
    public void getKey(){
        Key secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        System.out.println("随机生成的 SecretKey: " + java.util.Base64.getEncoder().encodeToString(secretKey.getEncoded()));
    }
}
