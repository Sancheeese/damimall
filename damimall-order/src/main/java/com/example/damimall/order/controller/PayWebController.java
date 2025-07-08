package com.example.damimall.order.controller;

import com.alipay.api.AlipayApiException;
import com.example.damimall.order.config.AlipayTemplate;
import com.example.damimall.order.service.PayService;
import com.example.damimall.order.vo.PaySubmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/html")
public class PayWebController {
    @Autowired
    PayService payService;

    @Autowired
    AlipayTemplate aliPayTemplate;

    @ResponseBody
    @GetMapping("/pay")
    public String payOrder(PaySubmitVo paySubmitVo) throws AlipayApiException {
        return payService.payOrder(paySubmitVo);
//        return aliPayTemplate.pay();
    }
}
