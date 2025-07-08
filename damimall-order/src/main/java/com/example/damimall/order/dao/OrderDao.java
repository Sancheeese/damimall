package com.example.damimall.order.dao;

import com.example.damimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-08 00:21:19
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    Integer getStatusBySn(@Param("orderSn") String orderSn);

    void updateStatusBySn(@Param("orderSn") String orderSn, @Param("code") Integer code);
}
