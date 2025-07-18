package com.example.damimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.damimall.order.entity.OrderItemEntity;

import java.util.List;
import java.util.Map;

/**
 * 订单项信息
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-08 00:21:19
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Map<String, List<OrderItemEntity>> getSn2Item(List<String> orderSns);
}

