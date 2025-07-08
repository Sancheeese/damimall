package com.example.common.utils;

import com.example.common.to.auth.SimpleUserToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.*;


public class JWTUserUtils {
    private static final String SECRET = "VZJ2Jw6QRpED+yUNv8+7lIegnqxs3MDaFWlfMCoLSVk=";
    public static final long TOKEN_EXPIRATION_TIME = 3600 * 1000 * 24;
    public static final int COOKIE_EXPIRATION_TIME = 3600 * 24;
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    private static final JwtParser PARSER = Jwts.parser().verifyWith(SECRET_KEY).build();

    public static String generateToken(Long id, String username) {
        // 创建Claims对象，存储多个字段
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);


        return Jwts.builder()
                .claims(claims)
                .subject(id.toString())
                .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims parseToken(String token) {
        return PARSER.parseSignedClaims(token)
                .getPayload();
    }

    public static void setToken(HttpServletResponse response, SimpleUserToken userToken){
        String token = JWTUserUtils.generateToken(userToken.getUserId(), userToken.getName());

        Cookie cookie = new Cookie("Authorization", token);
        cookie.setDomain("damimall.com");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(COOKIE_EXPIRATION_TIME);

        response.addCookie(cookie);
    }

    public static void refreshToken(HttpServletResponse response, SimpleUserToken userToken){
        JWTUserUtils.setToken(response, userToken);
    }

    public static void refreshToken(HttpServletResponse response, String userToken){
        try{
            Claims claims = parseToken(userToken);
            SimpleUserToken simpleUserToken = new SimpleUserToken(Long.parseLong(claims.getSubject()), claims.get("username", String.class));
            JWTUserUtils.setToken(response, simpleUserToken);
        }catch (ExpiredJwtException e){
            e.printStackTrace();
        }

    }
}
