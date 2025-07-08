package com.example.common.to.member;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginUserTo {
    @NotNull
    private String loginacct;

    @NotNull
    private String password;
}
