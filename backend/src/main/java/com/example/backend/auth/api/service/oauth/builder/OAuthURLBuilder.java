package com.example.backend.auth.api.service.oauth.builder;

public interface OAuthURLBuilder {

    // 로그인 인증 요청(인가 코드 요청) URL 생성
    String authorize(String state);

    // 토큰 발급 요청(Access Token 요청) URL 생성
    String token(String code, String state);

    // 프로필 요청 URL 생성
    String profile();
}
