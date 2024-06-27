package com.example.backend.auth.api.service.oauth.builder.kakao;

import com.example.backend.auth.api.service.oauth.builder.OAuthURLBuilder;
import com.example.backend.auth.config.oauth.OAuthProperties;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.oauth.OAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KakaoURLBuilder implements OAuthURLBuilder {
    private static final String PLATFORM = "kakao";
    private final String authorizationUri;
    private final String clientId;
    private final String redirectUri;
    private final String tokenUri;
    private final String clientSecret;
    private final String profileUri;


    // 속성에서 읽어온 객체를 주입
    public KakaoURLBuilder(OAuthProperties oAuthProperties) {
        try {
            // 플랫폼(kakao)의 client, provider Map 획득
            OAuthProperties.Client kakaoClient = oAuthProperties.getClient().get(PLATFORM);
            OAuthProperties.Provider kakaoProvider = oAuthProperties.getProvider().get(PLATFORM);

            this.authorizationUri = kakaoProvider.authorizationUri();
            this.clientId = kakaoClient.clientId();
            this.redirectUri = kakaoClient.redirectUri();
            this.tokenUri = kakaoProvider.tokenUri();
            this.clientSecret = kakaoClient.clientSecret();
            this.profileUri = kakaoProvider.profileUri();

        } catch (NullPointerException e) {
            log.error(">>>> OAuthProperties NullPointerException 발생: {}", ExceptionMessage.OAUTH_CONFIG_NULL);
            throw new OAuthException(ExceptionMessage.OAUTH_CONFIG_NULL);
        }
    }

    // "https://kauth.kakao.com/oauth/authorize?..."
    @Override
    public String authorize(String state) {
        return authorizationUri
                + "?response_type=code"             // OAuth 인증 코드 그랜트 유형: code로 고정
                + "&client_id=" + clientId          // 클라이언트 ID
                + "&redirect_uri=" + redirectUri    // 리다이렉트 URI
                + "&state=" + state                 // CSRF 방지
                + "&scope=openid";                  // 리소스 접근 범위: openid로 고정
    }

    // "https://kauth.kakao.com/oauth/token?..."
    @Override
    public String token(String code, String state) {
        return tokenUri
                + "?grant_type=authorization_code"  // OAuth 인증 코드 그랜트 유형: code로 고정
                + "&client_id=" + clientId          // 클라이언트 ID
                + "&client_secret=" + clientSecret  // 클라이언트 Secret
                + "&redirect_uri=" + redirectUri    // 리다이렉트 URI
                + "&code=" + code;                  // authorize() 요청으로 얻은 인가 코드

    }

    // "https://kapi.kakao.com/v2/user/me"
    @Override
    public String profile() {
        return profileUri;
    }
}
