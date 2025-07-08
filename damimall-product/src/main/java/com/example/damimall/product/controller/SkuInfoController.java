package com.example.damimall.product.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.example.common.to.order.OrderItemTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.damimall.product.entity.SkuInfoEntity;
import com.example.damimall.product.service.SkuInfoService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;


/**
 * sku信息
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:49
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:skuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = skuInfoService.queryPage(params);
        PageUtils page = skuInfoService.listByCondition(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    // @RequiresPermissions("product:skuinfo:info")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:skuinfo:save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:skuinfo:update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:skuinfo:delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

    @RequestMapping("/queryOne")
    // @RequiresPermissions("product:skuinfo:delete")
    public R queryOneById(@RequestParam Long skuId){
        SkuInfoEntity skuInfo = skuInfoService.queryOneById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    @PostMapping("/getOrderSkuInfo")
    public List<OrderItemTo> getOrderSkuInfo(@RequestBody List<Long> skuIds){
        return skuInfoService.getOrderSkuInfo(skuIds);
    }

    @PostMapping("/batch/info")
    public List<SkuInfoEntity> getBatchInfo(@RequestBody List<Long> ids){
        return skuInfoService.getBatchInfo(ids);
    }

}
