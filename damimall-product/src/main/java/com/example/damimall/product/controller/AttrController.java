package com.example.damimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.alibaba.nacos.shaded.com.google.j2objc.annotations.AutoreleasePool;
import com.example.damimall.product.entity.ProductAttrValueEntity;
import com.example.damimall.product.service.ProductAttrValueService;
import com.example.damimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.damimall.product.entity.AttrEntity;
import com.example.damimall.product.service.AttrService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;



/**
 * 商品属性
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:49
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    // @RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
		AttrVo attr = attrService.info(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
//		attrService.removeByIds(Arrays.asList(attrIds));
        attrService.deleteAttr(attrIds);

        return R.ok();
    }

    @RequestMapping("/base/list/{catelogId}")
    // @RequiresPermissions("product:attr:delete")
    public R baseList(@RequestParam Map<String, Object> params, @PathVariable Long catelogId){
        PageUtils page = attrService.listAttr("base", params, catelogId);

        return R.ok().put("page", page);
    }

    @RequestMapping("/sale/list/{catelogId}")
    // @RequiresPermissions("product:attr:delete")
    public R saleList(@RequestParam Map<String, Object> params, @PathVariable Long catelogId){
        PageUtils page = attrService.listAttr("sale", params, catelogId);

        return R.ok().put("page", page);
    }

    @GetMapping("/base/listforspu/{spuId}")
    // @RequiresPermissions("product:attr:delete")
    public R listBaseForSpu(@PathVariable Long spuId){
        List<ProductAttrValueEntity> attrList = attrService.getBaseAttrBySpuId(spuId);

        return R.ok().put("data", attrList);
    }

    @PostMapping("/update/{spuId}")
    // @RequiresPermissions("product:attr:delete")
    public R updateSpuAttr(@RequestBody List<ProductAttrValueEntity> productAttrValues, @PathVariable Long spuId){
        productAttrValueService.updateSpuAttr(spuId, productAttrValues);

        return R.ok();
    }





}
