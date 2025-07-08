package com.example.damimall.auth.service;

import javax.servlet.http.HttpServletResponse;

public interface OAuth2Service {
    String socialGiteeLogin(String code, HttpServletResponse resp);
}
