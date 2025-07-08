package com.example.com.damimall.cart.filter;

import com.example.com.damimall.cart.constant.CartConstant;
import com.example.com.damimall.cart.utils.UserInfoUtils;
import com.example.common.to.auth.SimpleUserToken;
import com.example.com.damimall.cart.vo.UserInfoVo;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

public class CartFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession();

        UserInfoVo userInfoVo = new UserInfoVo();
        SimpleUserToken loginUser = (SimpleUserToken) session.getAttribute("loginUser");
        if (loginUser != null) {
            userInfoVo.setUserId(loginUser.getUserId());
        }

        String token = null;
        for (Cookie cookie : req.getCookies()) {
            if (CartConstant.TEMP_USER_KEY.equals(cookie.getName())){
                token = cookie.getValue();
            }
        }

        if (token == null) {
            // 如果临时token不在就创建一个
            String tokenId = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_KEY, tokenId);
            cookie.setMaxAge(CartConstant.TEMP_USER_KEY_EXPIRE);
            resp.addCookie(cookie);
        }

        userInfoVo.setUserKey(token);
        UserInfoUtils.setUser(userInfoVo);

        // 执行其他过滤链
        filterChain.doFilter(servletRequest, servletResponse);

        UserInfoUtils.delUser();
    }
}
