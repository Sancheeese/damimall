package com.example.damimall.order.controller;

import com.example.damimall.order.exception.NoStockException;
import com.example.damimall.order.service.OrderService;
import com.example.damimall.order.vo.OrderConfirmVo;
import com.example.damimall.order.vo.OrderSubmitVo;
import com.example.damimall.order.vo.SubmitOrderRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderWebController {
    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model){
        OrderConfirmVo confirmVo = orderService.orderConfirm();
        model.addAttribute("confirmOrderData", confirmVo);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes redirectAttributes){
        try {
            SubmitOrderRespVo submitOrderRespVo = orderService.submitOrder(orderSubmitVo);

            if (submitOrderRespVo.getCode() != 0) {
                String msg = "下单失败：";
                switch (submitOrderRespVo.getCode()) {
                    case 1:
                        msg += "用户获取失败";
                        break;
                    case 2:
                        msg += "重复提交";
                        break;
                    case 3:
                        msg += "验价不通过";
                        break;
                    case 4:
                        msg += "锁库存失败";
                        break;
                }
                redirectAttributes.addFlashAttribute("msg", msg);

                return "redirect:http://order.damimall.com/toTrade";
            }
            model.addAttribute("submitOrderResp", submitOrderRespVo);
            return "pay";
        }catch (Exception e){
            if (e instanceof NoStockException){
                String msg = ((NoStockException) e).getMessage();
                redirectAttributes.addFlashAttribute("msg", msg);
            }
            e.printStackTrace();
            return "redirect:http://order.damimall.com/toTrade";
        }
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        System.out.println("hello");

        return "hello";
    }

    @ResponseBody
    @RequestMapping("/payed/hello")
    public String hello1(){
        System.out.println("hello");
        return "hello";
    }
}
