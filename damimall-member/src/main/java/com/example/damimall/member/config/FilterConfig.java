package com.example.damimall.member.config;

import com.example.damimall.member.filter.JWTUserFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<JWTUserFilter> jwtUserFilter(){
        FilterRegistrationBean<JWTUserFilter> registrationBean = new FilterRegistrationBean<>();
        JWTUserFilter jwtUserFilter = new JWTUserFilter();
        registrationBean.setFilter(jwtUserFilter);
        registrationBean.addUrlPatterns("/*"); // 作用于所有请求
        registrationBean.addInitParameter("exclusions", "/member/member/login");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
