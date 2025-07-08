package com.example.damimall.product.feign;

import com.example.common.to.auth.SimpleUserToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

public interface LoginFeignService {
    @GetMapping("/refreshToke")
    public void reFreshToken (@CookieValue("Authorization") SimpleUserToken userToken, HttpServletResponse resp);
}
