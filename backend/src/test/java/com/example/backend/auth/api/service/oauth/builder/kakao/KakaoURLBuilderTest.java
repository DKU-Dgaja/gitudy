package com.example.backend.auth.api.service.oauth.builder.kakao;

import com.example.backend.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static org.assertj.core.api.Assertions.assertThat;

public class KakaoURLBuilderTest extends TestConfig {
    @Autowired
    private KakaoURLBuilder urlBuilder;

    @Value("${oauth2.client.kakao.client-id}") private String clientId;
    @Value("${oauth2.client.kakao.client-secret}") private String clientSecret;
    @Value("${oauth2.client.kakao.redirect-uri}") private String redirectUri;

    @Value("${oauth2.provider.kakao.authorization-uri}") String authorizationUri;
    @Value("${oauth2.provider.kakao.token-uri}") private String tokenUri;
    @Value("${oauth2.provider.kakao.profile-uri}") private String profileUri;

    @Test
    @DisplayName("authorize(인가 코드 요청) URL을 성공적으로 생성한다.")
    void authorizeURIBuildSuccess() {
        // given
        String state = "testState";

        // when
        String authorizeURL = urlBuilder.authorize(state);

        // then
        System.out.println("authorize URL : " + authorizeURL);
        assertThat(authorizeURL).isEqualTo(authorizationUri
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&state=" + state
                + "&scope=openid");
    }


    @Test
    @DisplayName("token(Access Token 요청) URL을 성공적으로 생성한다.")
    void tokenURIBuildSuccess() {
        // given
        String code = "testCode";
        String state = "testState";

        // when
        String tokenURL = urlBuilder.token(code, state);

        // then
        System.out.println("tokenURL : " + tokenURL);
        assertThat(tokenURL).isEqualTo(tokenUri
                + "?grant_type=authorization_code"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + redirectUri
                + "&code=" + code);
    }

    @Test
    @DisplayName("profile(사용자 정보 요청) URL을 성공적으로 생성한다.")
    void profileURIBuildSuccess() {
        // given

        // when
        String profileURL = urlBuilder.profile();

        // then
        System.out.println("profileURL : " + profileURL);
        assertThat(profileURL).isEqualTo(profileUri);

    }
}
