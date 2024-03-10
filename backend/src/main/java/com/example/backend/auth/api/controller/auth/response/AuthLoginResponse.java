package com.example.backend.auth.api.controller.auth.response;

import com.example.backend.domain.define.account.user.constant.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthLoginResponse {
    private String accessToken;
    private String refreshToken;
    private UserRole role;

    @Builder
    public AuthLoginResponse(String accessToken, String refreshToken, UserRole role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
    }
}
