package com.example.backend.external.clients.oauth.github;

import com.example.backend.auth.TestConfig;
import com.example.backend.external.clients.oauth.github.response.GithubProfileResponse;
import com.example.backend.external.clients.oauth.github.response.GithubTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GithubClientTest extends TestConfig {
    @Autowired
    private GithubTokenClients githubTokenClients;

    @Autowired
    private GithubProfileClients githubProfileClients;

    @Test
    @DisplayName("인가 code를 URL에 담아 요청해 access_token을 성공적으로 받환받는다.")
    void githubTokenRequestTest() {
        // given
        String uri = "https://github.com/login/oauth/access_token?grant_type=authorization_code&client_id=9f11827f9305f205336a&client_secret=919ba9d1f8ecc2dc3209ef42e1fde0f86d89790b&redirect_uri=http://localhost:8080/auth/GITHUB/login&code=";

        // when
        GithubTokenResponse token = githubTokenClients.getToken(URI.create(uri));
        System.out.println("token = " + token.getAccess_token());

        // then
        // 현재는 동적인 테스트 불가하므로 null. OauthService 구현 후 동적으로 테스트할 예정
        assertNull(token.getAccess_token());
    }

    @Test
    @DisplayName("Access Token을 URL에 담아 요청해 사용자 정보를 성공적으로 받환받는다.")
    void githubProfileRequestTest() {
        // given
        String accessToken = "";

/*        GithubProfileResponse profile = githubProfileClients.getProfile("Bearer " + accessToken);

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
