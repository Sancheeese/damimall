package com.example.damimall.thirdpart.controller;

import com.example.common.utils.R;
import com.example.damimall.thirdpart.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sms")
public class SmsController {
    @Autowired
    SmsService smsService;

    @ResponseBody
    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code){
        smsService.sendCode(phone, code);
        return R.ok();
    }
}
