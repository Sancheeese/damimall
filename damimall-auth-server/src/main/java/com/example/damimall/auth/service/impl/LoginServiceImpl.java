package com.example.damimall.auth.service.impl;

import com.example.common.exception.BizCodeEnum;
import com.example.common.to.auth.SimpleUserToken;
import com.example.common.to.member.LoginUserTo;
import com.example.common.to.member.MemberRegisterTo;
import com.example.common.utils.JWTUserUtils;
import com.example.common.utils.R;
import com.example.damimall.auth.constant.AuthConstant;
import com.example.damimall.auth.feign.MemberFeign;
import com.example.damimall.auth.feign.ThirdPartFeign;
import com.example.damimall.auth.service.LoginService;
import com.example.damimall.auth.vo.LoginUserVo;
import com.example.damimall.auth.vo.RegUserVo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {
    @Autowired
    ThirdPartFeign thirdPartFeign;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MemberFeign memberFeign;

    @Override
    public R sendCode(String phone) {
        String codeInRedis = redisTemplate.opsForValue().get(AuthConstant.AUTH_CODE_PREFIX + phone);
        if (codeInRedis != null) {
            long codeTime = Long.parseLong(codeInRedis.split("_")[1]);
            if (System.currentTimeMillis() - codeTime < 60000){
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        Random random = new Random();
        String code = String.valueOf(100000 + random.nextInt(900000));
        String codeWithTime = code + "_" + System.currentTimeMillis();
        redisTemplate.opsForValue().set(AuthConstant.AUTH_CODE_PREFIX + phone, codeWithTime, 5, TimeUnit.MINUTES);

        thirdPartFeign.sendCode(phone, code);
        return R.ok();
    }



    @Override
    public String register(RegUserVo regUserVo, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // 检验参数格式是否正确
        if (!validParam(bindingResult, redirectAttributes))
            return "redirect:http://auth.damimall.com/reg.html";
        // 检查验证码是否正确
        if (!checkCode(regUserVo, redirectAttributes))
            return "redirect:http://auth.damimall.com/reg.html";

        // 远程调用，注册会员
        MemberRegisterTo to = new MemberRegisterTo();
        BeanUtils.copyProperties(regUserVo, to);
        try {
            R r = memberFeign.register(to);
            if (r.getCode() != 0){
                Map<String, String> errors = new HashMap<>();
                errors.put("msg", r.get("msg").toString());
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.damimall.com/reg.html";
            }
        }catch (Exception e){
            log.error("远程调用member注册服务失败");
        }

        return "redirect:http://auth.damimall.com/login.html";
    }

    public boolean validParam(BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            redirectAttributes.addFlashAttribute("errors", errors);
            return false;
        }

        return true;
    }

    public boolean checkCode(RegUserVo regUserVo, RedirectAttributes redirectAttributes){
        Map<String, String> errors = new HashMap<>();
        String codeWithTime = redisTemplate.opsForValue().get(AuthConstant.AUTH_CODE_PREFIX + regUserVo.getPhone());
        if (codeWithTime == null) {
            errors.put("code", "验证码过期");
            redirectAttributes.addFlashAttribute("errors", errors);
            return false;
        }
        if (!codeWithTime.split("_")[0].equals(regUserVo.getCode())){
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            return false;
        }
        redisTemplate.delete(AuthConstant.AUTH_CODE_PREFIX + regUserVo.getPhone());

        return true;
    }

    @Override
    public String login(LoginUserVo loginUserVo, RedirectAttributes redirectAttributes, HttpServletResponse resp) {
        LoginUserTo to = new LoginUserTo();
        BeanUtils.copyProperties(loginUserVo, to);

        R r = memberFeign.login(to);

        if (r.getCode() != 0){
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.get("msg").toString());
            redirectAttributes.addFlashAttribute("errors", errors);

            return "redirect:http://auth.damimall.com/login.html";
        }

        SimpleUserToken userToken = r.getData(new TypeReference<SimpleUserToken>() {});
        JWTUserUtils.setToken(resp, userToken);

        return "redirect:http://damimall.com";
    }

    @Override
    public void reFreshToken(SimpleUserToken userToken, HttpServletResponse resp) {
        JWTUserUtils.refreshToken(resp, userToken);
    }

}
