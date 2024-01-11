package com.example.backend.auth.api.service.oauth.builder.github;

import com.example.backend.auth.api.service.oauth.builder.OAuthURLBuilder;
import com.example.backend.auth.config.oauth.OAuthProperties;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.oauth.OAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GithubURLBuilder implements OAuthURLBuilder {
    private static final String PLATFORM = "github";
    private static final int PROPERTY_SIZE = 3;
    private final String authorizationUri;
    private final String clientId;
    private final String redirectUri;
    private final String tokenUri;
    private final String clientSecret;
    private final String profileUri;

    // 속성에서 읽어온 객체를 주입
    public GithubURLBuilder(OAuthProperties oAuthProperties) {
        // 플랫폼(github)의 client, provider Map 획득
        OAuthProperties.Client githubClient = oAuthProperties.getClient().get(PLATFORM);
        OAuthProperties.Provider githubProvider = oAuthProperties.getProvider().get(PLATFORM);

        // 속성값을 불러오지 못했을 경우 예외 발생
        if (githubClient == null || githubProvider == null) {
            log.error(">>>> {} <<<<", ExceptionMessage.OAUTH_CONFIG_NULL.getText());
            throw new OAuthException(ExceptionMessage.OAUTH_CONFIG_NULL);
        }

        this.authorizationUri = githubProvider.getAuthorizationUri();
        this.clientId = githubClient.getClientId();
        this.redirectUri = githubClient.getRedirectUri();
        this.tokenUri = githubProvider.getTokenUri();
        this.clientSecret = githubClient.getClientSecret();
        this.profileUri = githubProvider.getProfileUri();
    }

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
