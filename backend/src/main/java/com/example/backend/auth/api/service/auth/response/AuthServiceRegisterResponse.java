package com.example.backend.auth.api.service.auth.response;

import com.example.backend.domain.define.account.user.constant.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthServiceRegisterResponse {
    private String accessToken;
    private String refreshToken;

    @Builder
    public AuthServiceRegisterResponse(String accessToken, String refreshToken, UserRole role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
