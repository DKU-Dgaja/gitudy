package com.example.backend.auth.api.service.oauth.builder.github;

import com.example.backend.auth.api.service.oauth.builder.OAuthURLBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GithubURLBuilder implements OAuthURLBuilder {

    @Value("${oauth2.client.github.client-id}") private String clientId;
    @Value("${oauth2.client.github.client-secret}") private String clientSecret;
    @Value("${oauth2.client.github.redirect-uri}") private String redirectUri;

    @Value("${oauth2.provider.github.authorization-uri}") String authorizationUri;
    @Value("${oauth2.provider.github.token-uri}") private String tokenUri;
    @Value("${oauth2.provider.github.profile-uri}") private String profileUri;

    // "https://github.com/login/oauth/authorize?..."
    @Override
    public String authorize(String state) {
        return authorizationUri
                + "?response_type=code"             // OAuth 인증 코드 그랜트 유형: code로 고정
                + "&client_id=" + clientId          // 클라이언트 ID
                + "&redirect_uri=" + redirectUri    // 리다이렉트 URI
                + "&state=" + state                 // CSRF 방지
                + "&scope=openid";                  // 리소스 접근 범위: openid로 고정
    }

    // "https://github.com/login/oauth/access_token?..."
    @Override
    public String token(String code, String state) {
        return tokenUri
                + "?grant_type=authorization_code"  // OAuth 인증 코드 그랜트 유형: code로 고정
                + "&client_id=" + clientId          // 클라이언트 ID
                + "&client_secret=" + clientSecret  // 클라이언트 Secret
                + "&redirect_uri=" + redirectUri    // 리다이렉트 URI
                + "&code=" + code;                  // authorize() 요청으로 얻은 인가 코드
    }

    // "https://api.github.com/user"
    @Override
    public String profile() {
        return profileUri;
    }
}
