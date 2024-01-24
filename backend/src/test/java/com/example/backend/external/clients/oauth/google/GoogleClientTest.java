package com.example.backend.external.clients.oauth.google;

import com.example.backend.auth.TestConfig;
import com.example.backend.external.clients.oauth.google.response.GoogleTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertNull;

public class GoogleClientTest extends TestConfig {
    @Autowired
    private GoogleTokenClients googleTokenClients;

    @Autowired
    private GoogleProfileClients googleProfileClients;

    @Test
    @DisplayName("인가 code를 URL에 담아 요청해 access_token을 성공적으로 받환받는다.")
    void googleTokenRequestTest() {
        // given
       // String uri = "https://oauth2.googleapis.com/token?grant_type=authorization_code&client_id=990624479058-n15n90hki5la1cf8246nmg2nchu7qofn.apps.googleusercontent.com&client_secret=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&redirect_uri=http://localhost:8080/auth/GOOGLE/login&code=";

        // when
       //   GoogleTokenResponse token = googleTokenClients.getToken(URI.create(uri));
       // System.out.println("token = " + token.getAccess_token());

        // then
        // 현재는 동적인 테스트 불가하므로 null. OauthService 구현 후 동적으로 테스트할 예정
       // assertNull(token.getAccess_token());
    }

    @Test
    @DisplayName("Access Token을 URL에 담아 요청해 사용자 정보를 성공적으로 받환받는다.")
    void googleProfileRequestTest() {
        // given


/*      GoogleProfileResponse profile = googleProfileClients.getProfile("Bearer " + accessToken);

        System.out.println("login = " + profile.getLogin());
        System.out.println("email = " + profile.getEmail());
        System.out.println("name = " + profile.getName());

        assertAll(
                () -> assertThat(profile.getLogin()).isEqualTo("j-ra1n"),
                () -> assertThat(profile.getEmail()).isEqualTo("xw21yog@dankook.ac.kr"),
                () -> assertThat(profile.getName()).isEqualTo("이정우")
        );*/

    }
}

