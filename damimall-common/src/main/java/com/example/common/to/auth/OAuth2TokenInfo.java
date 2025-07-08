package com.example.common.to.auth;

import lombok.Data;

@Data
public class OAuth2TokenInfo {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private String refreshToken;
    private String scope;
    private long createdAt;
}
