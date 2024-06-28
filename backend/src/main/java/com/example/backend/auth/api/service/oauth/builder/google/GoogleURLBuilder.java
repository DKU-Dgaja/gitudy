package com.example.backend.auth.api.service.oauth.builder.google;

import com.example.backend.auth.api.service.oauth.builder.OAuthURLBuilder;
import com.example.backend.auth.config.oauth.OAuthProperties;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.oauth.OAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class GoogleURLBuilder implements OAuthURLBuilder {

    private static final String PLATFORM = "google";
    private final String authorizationUri;
    private final String clientId;
    private final String redirectUri;
    private final String tokenUri;
    private final String clientSecret;
    private final String profileUri;


    public GoogleURLBuilder(OAuthProperties oAuthProperties) {
        try {
            // 플랫폼(google)의 client, provider Map 획득
            OAuthProperties.Client googleClient = oAuthProperties.getClient().get(PLATFORM);
            OAuthProperties.Provider googleProvider = oAuthProperties.getProvider().get(PLATFORM);

            this.authorizationUri = googleProvider.authorizationUri();
            this.clientId = googleClient.clientId();
            this.redirectUri = googleClient.redirectUri();
            this.tokenUri = googleProvider.tokenUri();
            this.clientSecret = googleClient.clientSecret();
            this.profileUri = googleProvider.profileUri();

        } catch (NullPointerException e) {
            log.error(">>>> OAuthProperties NullPointerException 발생: {}", ExceptionMessage.OAUTH_CONFIG_NULL);
            throw new OAuthException(ExceptionMessage.OAUTH_CONFIG_NULL);
        }
    }

    // "https://accounts.google.com/o/oauth2/v2/auth?..."
    @Override   // Google OAuth 인증을 위한 URL 생성
    public String authorize(String state) {
        return authorizationUri
                + "?response_type=code"             // OAuth 인증 코드 그랜트 유형: code로 고정
                + "&client_id=" + clientId          // 클라이언트 ID
                + "&redirect_uri=" + redirectUri    // 리다이렉트 URI
                + "&state=" + state                 // CSRF 방지
                + "&scope=email+profile";           // Google의 경우 openid가 아닌 email+profile로 추가해야함
    }

    // "https://oauth2.googleapis.com/token?..."
    @Override    // access token을 요청하는 URL 생성
    public String token(String code, String state) {
        return tokenUri
                + "?grant_type=authorization_code"  // OAuth 인증 코드 그랜트 유형: code로 고정
                + "&client_id=" + clientId          // 클라이언트 ID
                + "&client_secret=" + clientSecret  // 클라이언트 Secret
                + "&redirect_uri=" + redirectUri    // 리다이렉트 URI
                + "&code=" + code;                  // authorize() 요청으로 얻은 인가 코드
    }


    // "https://www.googleapis.com/oauth2/v3/userinfo"
    @Override   // 사용자 프로필 정보 요청하는 URL반환
    public String profile() {
        return profileUri;
    }
}
