package com.example.backend.auth.api.service.auth.request;


import com.example.backend.auth.api.controller.auth.request.AuthRegisterRequest;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.domain.define.account.user.constant.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthServiceRegisterRequest {
    private UserRole role;
    private String platformId;
    private UserPlatformType platformType;
    private String githubEmail;
    public static AuthServiceRegisterRequest of(AuthRegisterRequest request) {
        return AuthServiceRegisterRequest.builder()
                .role(request.getRole())
                .platformId(request.getPlatformId())
                .platformType(request.getPlatformType())
                .githubEmail(request.getGithubEmail())
                .build();
    }
}