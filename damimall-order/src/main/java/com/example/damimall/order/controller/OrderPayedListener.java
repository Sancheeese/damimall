package com.example.damimall.order.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.damimall.order.config.AlipayTemplate;
import com.example.damimall.order.service.OrderService;
import com.example.damimall.order.vo.AliPayAsyncVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@Slf4j
public class OrderPayedListener {
    @Autowired
    OrderService orderService;

    @PostMapping("/payed/notify")
    public String handleAlipay(AliPayAsyncVo payAsyncVo, HttpServletRequest req){
        Map<String, String[]> parameterMap = req.getParameterMap();
        System.out.println(parameterMap);
        try {
            if (AlipayTemplate.checkSign(req))
                return orderService.handleAlipay(payAsyncVo);
        } catch (AlipayApiException e) {
            log.error("验证签名异常");
            e.printStackTrace();
        }

        return "fail";
    }
}
