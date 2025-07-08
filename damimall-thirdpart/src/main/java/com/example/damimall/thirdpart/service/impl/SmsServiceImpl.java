package com.example.damimall.thirdpart.service.impl;

import com.example.damimall.thirdpart.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsServiceImpl implements SmsService {
    @Override
    public void sendCode(String phone, String code) {
        log.info("给" + phone + "发送验证码 ==> " + code);
    }
}
