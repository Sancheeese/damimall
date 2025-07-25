package com.example.com.damimall.cart.filter;

import com.example.common.to.auth.SimpleUserToken;
import com.example.common.utils.JWTUserUtils;
import com.example.common.utils.ObjectMapperUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class JWTUserFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();

        String token = null;
        if (cookies != null){
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())){
                    token = cookie.getValue();
                }
            }
        }

        HttpSession session = request.getSession();
        if (token != null) {
            try {
                Claims claims = JWTUserUtils.parseToken(token);

                String id = claims.getSubject();
                String username = claims.get("username", String.class);

                // 放到session
                session.setAttribute("userId", id);
                session.setAttribute("username", username);
                SimpleUserToken userToken = new SimpleUserToken(Long.parseLong(id), username);
                session.setAttribute("loginUser", userToken);
                session.setMaxInactiveInterval((int) JWTUserUtils.TOKEN_EXPIRATION_TIME);
                // 更新token时间
                new Thread(() -> {
                    HttpPost post = new HttpPost("http://auth.damimall.com/refreshToken");
                    StringEntity body = new StringEntity(ObjectMapperUtils.writeValueAsString(userToken), ContentType.APPLICATION_JSON);
                    post.setEntity(body);

                    // 发请求
                    try(CloseableHttpClient client = HttpClients.createDefault()){
                        client.execute(post);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }).start();
            }catch (ExpiredJwtException e){
                e.printStackTrace();
            }
        }
        filterChain.doFilter(request, response);

        // 删掉session
        session.removeAttribute("loginUser");
    }
}
