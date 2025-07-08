package com.example.damimall.auth.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginUserVo {
    @NotNull
    private String loginacct;

    @NotNull
    private String password;
}
