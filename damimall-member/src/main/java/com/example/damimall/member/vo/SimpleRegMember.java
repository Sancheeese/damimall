package com.example.damimall.member.vo;

import lombok.Data;

@Data
public class SimpleRegMember {
    private Long id;

    private String name;

    private String accessToken;

    private Long expiresIn;
}
