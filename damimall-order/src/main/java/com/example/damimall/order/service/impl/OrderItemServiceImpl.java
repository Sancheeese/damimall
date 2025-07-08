package com.example.damimall.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.order.dao.OrderItemDao;
import com.example.damimall.order.entity.OrderItemEntity;
import com.example.damimall.order.service.OrderItemService;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {
    @Autowired
    OrderItemDao orderItemDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public Map<String, List<OrderItemEntity>> getSn2Item(List<String> orderSns) {
        List<OrderItemEntity> items = query().in("order_sn", orderSns).list();
        Map<String, List<OrderItemEntity>> map = new HashMap<>();

        for (OrderItemEntity item : items) {
            if (!map.containsKey(item.getOrderSn())) map.put(item.getOrderSn(), new ArrayList<>());
            map.get(item.getOrderSn()).add(item);
        }

        return map;
    }

}