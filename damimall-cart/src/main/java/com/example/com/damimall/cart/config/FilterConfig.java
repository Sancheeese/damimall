package com.example.com.damimall.cart.config;

import com.example.com.damimall.cart.filter.CartFilter;
import com.example.com.damimall.cart.filter.JWTUserFilter;
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
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<CartFilter> cartFilter(){
        FilterRegistrationBean<CartFilter> registrationBean = new FilterRegistrationBean<>();
        CartFilter cartFilter = new CartFilter();
        registrationBean.setFilter(cartFilter);
        registrationBean.addUrlPatterns("/*"); // 作用于所有请求
        registrationBean.setOrder(3);
        return registrationBean;
    }
}
