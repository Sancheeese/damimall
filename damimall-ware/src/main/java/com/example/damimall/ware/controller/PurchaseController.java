package com.example.damimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.example.damimall.ware.vo.MergeVo;
import com.example.damimall.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.damimall.ware.entity.PurchaseEntity;
import com.example.damimall.ware.service.PurchaseService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;



/**
 * 采购信息
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-08 00:29:08
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("ware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("ware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("ware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.savePurchase(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("ware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updatePurchaseById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("ware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @GetMapping("/unreceive/list")
    public R listUnerceive(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.listUnreceive(params);

        return R.ok().put("page", page);
    }

    @PostMapping("/merge")
    public R merge(@RequestBody MergeVo mergeVo){
        purchaseService.mergeRequire(mergeVo);

        return R.ok();
    }

    @PostMapping("/received")
    public R received(@RequestBody List<Long> ids){
        purchaseService.recevied(ids);

        return R.ok();
    }

    @PostMapping("/done")
    public R done(@RequestBody PurchaseDoneVo purchaseDoneVo){
        purchaseService.donePurchase(purchaseDoneVo);

        return R.ok();
    }



}
