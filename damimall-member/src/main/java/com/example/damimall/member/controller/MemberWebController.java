package com.example.damimall.member.controller;

import com.example.common.utils.R;
import com.example.damimall.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MemberWebController {
    @Autowired
    MemberService memberService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, Model model){
        R r = memberService.memberOrderPage(pageNum);
        model.addAttribute("orders", r);
        return "orderList";
    }

    @PostMapping("/memberOrder.html")
    public String memberOrderPage2(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, Model model){
        R r = memberService.memberOrderPage(pageNum);
        model.addAttribute("orders", r);
        return "orderList";
    }
}
