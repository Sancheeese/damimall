package com.example.damimall.order.controller;

import java.util.Arrays;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.damimall.order.entity.OrderEntity;
import com.example.damimall.order.service.OrderService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;



/**
 * 订单
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-08 00:21:19
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("order:order:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("order:order:info")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("order:order:save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("order:order:update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("order:order:delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @GetMapping("/status")
    public Integer getStatusBySn(@RequestParam("orderSn") String orderSn){
        return orderService.getStatusBySn(orderSn);
    }

    @GetMapping("/listItems")
    @ResponseBody
    public R listItems (@RequestParam Map<String, Object> param){
       PageUtils page = orderService.listItems(param);

       return R.ok().put("page", page);
    }



}
