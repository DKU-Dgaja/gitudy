package com.example.backend.auth.api.service.auth.request;


import com.example.backend.auth.api.controller.auth.request.AuthRegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthServiceRegisterRequest {
    private String name;
    private String githubId;
    private boolean pushAlarmYn;
    private String fcmToken;

    public static AuthServiceRegisterRequest of(AuthRegisterRequest request) {
        return AuthServiceRegisterRequest.builder()
                .name(request.getName())
                .githubId(request.getGithubId())
                .pushAlarmYn(request.isPushAlarmYn())
                .fcmToken(request.getFcmToken())
                .build();
    }
}