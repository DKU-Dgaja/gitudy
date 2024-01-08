package com.example.backend.auth.api.controller.auth.response;

import com.example.backend.domain.define.user.constant.UserPlatformType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class AuthLoginPageResponse {
    private UserPlatformType platformType;
    private String url;

    @Builder
    public AuthLoginPageResponse(UserPlatformType platformType, String url) {
        this.platformType = platformType;
        this.url = url;
    }
}
