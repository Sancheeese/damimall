package com.example.damimall.member.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfig {
    // 给Feign添加
    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String cookies = request.getHeader("Cookie");
                requestTemplate.header("Cookie", cookies);
//                Cookie[] cookies = request.getCookies();
//
//                String token = null;
//                if (cookies != null){
//                    for (Cookie cookie : cookies) {
//                        if ("Authorization".equals(cookie.getName())){
//                            token = cookie.getValue();
//                        }
//                    }
//                }
//
//                if (token != null){
//                    String cookie = "Authorization=" + token + "; Path=/; Max-Age=" + 3600 * 24 +"; Secure; HttpOnly";
//                    requestTemplate.header("Cookie", cookie);
//                }
            }
        };
    }
}
