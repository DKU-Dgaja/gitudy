package com.example.backend.external.clients.oauth.kakao.response;


import lombok.Getter;
import lombok.NoArgsConstructor;

/*
    Kakao에 Access Token을 요청 후 반환받을 DTO
 */
@Getter
@NoArgsConstructor
public class KakaoTokenResponse {
    private String access_token;

    public KakaoTokenResponse(String access_token) {
        this.access_token = access_token;
    }
}

