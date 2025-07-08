package com.example.damimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.to.seckill.SeckillOrderTo;
import com.example.common.utils.PageUtils;
import com.example.damimall.order.entity.OrderEntity;
import com.example.damimall.order.vo.AliPayAsyncVo;
import com.example.damimall.order.vo.OrderConfirmVo;
import com.example.damimall.order.vo.OrderSubmitVo;
import com.example.damimall.order.vo.SubmitOrderRespVo;

import java.util.Map;

/**
 * 订单
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-08 00:21:19
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo orderConfirm();

    SubmitOrderRespVo submitOrder(OrderSubmitVo orderSubmitVo);

    Integer getStatusBySn(String orderSn);

    void colesOrder(OrderEntity order);

    PageUtils listItems(Map<String, Object> param);

    String handleAlipay(AliPayAsyncVo payAsyncVo);

    void dealSeckillOrder(SeckillOrderTo order);
}

