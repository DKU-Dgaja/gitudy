package com.example.backend.auth.api.controller.auth.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReissueAccessTokenResponse {
    private String accessToken;
    private String refreshToken;

    @Builder
    public ReissueAccessTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
