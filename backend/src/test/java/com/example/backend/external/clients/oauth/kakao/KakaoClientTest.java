package com.example.backend.external.clients.oauth.kakao;

import com.example.backend.auth.TestConfig;
import com.example.backend.external.clients.oauth.kakao.response.KakaoProfileResponse;
import com.example.backend.external.clients.oauth.kakao.response.KakaoTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertNull;

public class KakaoClientTest extends TestConfig {
    @Autowired
    private KakaoTokenClients kakaoTokenClients;

    @Autowired
    private KakaoProfileClients kakaoProfileClients;

    @Test
    @DisplayName("인가 code를 URL에 담아 요청해 access_token을 성공적으로 받환받는다.")
    void kakaoTokenRequestTest() {
        // given
        //String uri = "https://kauth.kakao.com/oauth/token?grant_type=authorization_code&client_id=1d8513aae332ebe7462f429d67f3cacc&client_secret=T2dW4O4bFTPYz7PKflnIlqqfYXNbr2U6&redirect_uri=http://localhost:8080/auth/KAKAO/login&code=";


        // when
        //KakaoTokenResponse token = kakaoTokenClients.getToken(URI.create(uri));
        //System.out.println("token = " + token.getAccess_token());

        // then
        // 현재는 동적인 테스트 불가하므로 null. OauthService 구현 후 동적으로 테스트할 예정
        //assertNull(token.getAccess_token());
    }

    @Test
    @DisplayName("Access Token을 URL에 담아 요청해 사용자 정보를 성공적으로 받환받는다.")
    void githubProfileRequestTest() {
        // given
        String accessToken = "";

        /*KakaoProfileResponse profile = kakaoProfileClients.getProfile("Bearer " + accessToken);

        System.out.println("login = " + profile.getLogin());
        System.out.println("email = " + profile.getEmail());
        System.out.println("name = " + profile.getName());

        assertAll(
                () -> assertThat(profile.getLogin()).isEqualTo("jusung-c"),
                () -> assertThat(profile.getEmail()).isEqualTo("anaooauc1236@naver.com"),
                () -> assertThat(profile.getName()).isEqualTo("이주성")
        );*/
    }
}
