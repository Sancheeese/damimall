package com.example.damimall.auth.service;

import com.example.common.to.auth.SimpleUserToken;
import com.example.common.to.member.LoginUserTo;
import com.example.common.utils.R;
import com.example.damimall.auth.vo.LoginUserVo;
import com.example.damimall.auth.vo.RegUserVo;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;

public interface LoginService {
    R sendCode(String phone);

    String register(RegUserVo regUserVo, BindingResult bindingResult, RedirectAttributes redirectAttributes);

    String login(LoginUserVo loginUserVo, RedirectAttributes redirectAttributes, HttpServletResponse resp);

    void reFreshToken(SimpleUserToken userToken, HttpServletResponse resp);
}
