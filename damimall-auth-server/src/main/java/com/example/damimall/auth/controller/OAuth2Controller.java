package com.example.damimall.auth.controller;

import com.example.damimall.auth.service.OAuth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
public class OAuth2Controller {
    @Autowired
    OAuth2Service oAuth2Service;

    @RequestMapping("/oauth2/gitee/success")
    public String socialGiteeLogin(String code, HttpServletResponse resp){
        return oAuth2Service.socialGiteeLogin(code, resp);
    }

}
