package com.example.common.to.member;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class MemberRegisterTo {
    @NotBlank(message = "用户名不能为空")
    @Length(min = 1, max = 18, message = "用户名长度为1到18")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 18, message = "密码的长度为6到18")
    private String password;

    @NotBlank(message = "电话号码不能为空")
    @Pattern(regexp = "/^[1][0-9]{10}$/", message = "电话号码格式不对")
    private String phone;
}
