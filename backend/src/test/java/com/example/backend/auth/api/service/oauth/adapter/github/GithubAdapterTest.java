package com.example.backend.auth.api.service.oauth.adapter.github;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.service.oauth.builder.github.GithubURLBuilder;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.oauth.OAuthException;
import com.example.backend.external.clients.oauth.github.GithubProfileClients;
import com.example.backend.external.clients.oauth.github.GithubTokenClients;
import com.example.backend.external.clients.oauth.github.response.GithubProfileResponse;
import com.example.backend.external.clients.oauth.github.response.GithubTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.net.URI;

import static com.example.backend.domain.define.user.constant.UserPlatformType.GITHUB;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@AutoConfigureMockMvc
class GithubAdapterTest extends TestConfig {

    @Autowired
    private GithubAdapter githubAdapter;

    @Autowired
    private GithubURLBuilder githubURLBuilder;

    @Test
    @DisplayName("github 토큰 요청 API에 정상적인 요청을 보내면, access_token이 발행된다.")
    void githubAdapterGetTokenSuccess() {
        // given
        MockGithubTokenClients mockGithubTokenClients = new MockGithubTokenClients();
        MockGithubProfileClients mockGithubProfileClients = new MockGithubProfileClients();
        GithubAdapter githubAdapter = new GithubAdapter(mockGithubTokenClients, mockGithubProfileClients);

        // when
        String accessToken = githubAdapter.getToken("tokenUrl");

        // then
        System.out.println("accessToken = " + accessToken);
        assertThat(accessToken).isEqualTo("access-token");

    }

    @Test
    @DisplayName("github 토큰 요청 중 예외가 발생하면, OAUTH_INVALID_TOKEN_URL 에외가 발생한다.")
    void githubAdapterGetTokenFail() {
        // given
        String tokenURL = githubURLBuilder.token("error-token", "state");

        // when
        OAuthException exception = assertThrows(OAuthException.class,
                () -> githubAdapter.getToken(tokenURL));

        // then
        assertThat(exception.getMessage()).isEqualTo(ExceptionMessage.OAUTH_INVALID_TOKEN_URL.getText());

    }

    @Test
    @DisplayName("github 프로필 요청 API에 정상적인 요청을 보내면, 사용자 프로필이 반환된다.")
    void githubAdapterGetProfileSuccess() {
        // given
        MockGithubTokenClients mockGithubTokenClients = new MockGithubTokenClients();
        MockGithubProfileClients mockGithubProfileClients = new MockGithubProfileClients();
        GithubAdapter githubAdapter = new GithubAdapter(mockGithubTokenClients, mockGithubProfileClients);

        // when
        OAuthResponse profile = githubAdapter.getProfile("access-token");

        // then
        assertAll(
                () -> assertThat(profile.getPlatformId()).isEqualTo("1"),
                () -> assertThat(profile.getEmail()).isEqualTo("32183520@dankook.ac.kr"),
                () -> assertThat(profile.getProfileImageUrl()).isEqualTo("https://www.naver.com"),
                () -> assertThat(profile.getName()).isEqualTo("jusung-c"),
                () -> assertThat(profile.getPlatformType()).isEqualTo(GITHUB)
        );
    }

    @Test
    @DisplayName("github 프로필 요청 중 예외가 발생하면, OAUTH_INVALID_ACCESS_TOKEN 예외가 발생한다.")
    void githubAdapterGetProfileFail() {
        // when
        OAuthException exception = assertThrows(OAuthException.class,
                () -> githubAdapter.getProfile("error-token"));

        // then
        assertThat(exception.getMessage()).isEqualTo(ExceptionMessage.OAUTH_INVALID_ACCESS_TOKEN.getText());

    }

    static class MockGithubTokenClients implements GithubTokenClients {

        @Override
        public GithubTokenResponse getToken(URI uri) {
            return new GithubTokenResponse("access-token");
        }
    }

    static class MockGithubProfileClients implements GithubProfileClients {

        @Override
        public GithubProfileResponse getProfile(String header) {
            return new GithubProfileResponse(1L,
                    "jusung-c",
                    "이주성",
                    "32183520@dankook.ac.kr",
                    "https://www.naver.com",
                    "https://github.com/jusung-c");
        }
    }
}