package com.example.damimall.order.service.impl;

import com.alipay.api.AlipayApiException;
import com.example.damimall.order.config.AlipayTemplate;
import com.example.damimall.order.entity.OrderEntity;
import com.example.damimall.order.service.OrderService;
import com.example.damimall.order.service.PayService;
import com.example.damimall.order.vo.PaySubmitVo;
import com.example.damimall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayServiceImpl implements PayService {
    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    @Override
    public String payOrder(PaySubmitVo paySubmitVo) {
        String orderSn = paySubmitVo.getOrderSn();
        OrderEntity entity = orderService.query().eq("order_sn", orderSn).one();

        PayVo payVo = new PayVo();
        payVo.setOrderSn(orderSn);
        payVo.setTotalAmount(entity.getPayAmount());

        try {
            return alipayTemplate.pay(payVo);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return "支付错误";
        }
    }
}
