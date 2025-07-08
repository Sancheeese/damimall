package com.example.damimall.auth.controller;

import com.example.common.to.auth.SimpleUserToken;
import com.example.common.to.member.LoginUserTo;
import com.example.common.utils.R;
import com.example.damimall.auth.service.LoginService;
import com.example.damimall.auth.vo.LoginUserVo;
import com.example.damimall.auth.vo.RegUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {
    @Autowired
    LoginService loginService;

    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {
        return loginService.sendCode(phone);
    }

    @ResponseBody
    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }

    @PostMapping("/register")
    public String register(@RequestBody @Validated RegUserVo regUserVo, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        return loginService.register(regUserVo, bindingResult, redirectAttributes);
    }

    @PostMapping("/login")
    public String login(@Validated LoginUserVo loginUserVo, RedirectAttributes redirectAttributes, HttpServletResponse resp) {
        return loginService.login(loginUserVo, redirectAttributes, resp);
    }

    @PostMapping("/refreshToke")
    public void reFreshToken (@RequestBody SimpleUserToken userToken, HttpServletResponse resp) {
        loginService.reFreshToken(userToken, resp);
    }

}