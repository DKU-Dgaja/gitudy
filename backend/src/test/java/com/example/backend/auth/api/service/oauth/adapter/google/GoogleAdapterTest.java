package com.example.backend.auth.api.service.oauth.adapter.google;


import com.example.backend.TestConfig;
import com.example.backend.auth.api.service.oauth.builder.google.GoogleURLBuilder;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.oauth.OAuthException;
import com.example.backend.external.clients.oauth.google.GoogleProfileClients;
import com.example.backend.external.clients.oauth.google.GoogleTokenClients;
import com.example.backend.external.clients.oauth.google.response.GoogleProfileResponse;
import com.example.backend.external.clients.oauth.google.response.GoogleTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GOOGLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GoogleAdapterTest extends TestConfig {

    @Autowired
    private GoogleAdapter googleAdapter;

    @Autowired
    private GoogleURLBuilder googleURLBuilder;

    public static String expectedPlatformId = "102514823309503386675"; // google은 sub
    public static String expectedProfileImageUrl = "https://lh3.googleusercontent.com/a/ACg8ocLrP_GLo-fUjSmnUZedPZbbL7ifImYTnelh108XkgOx=s96-c";
    public static String expectedName = "이정우";

    @Test
    @DisplayName("google 토큰 요청 API에 정상적인 요청을 보내면, access_token이 발행된다.")
    void googleAdapterGetTokenSuccess() {
        // given
        GoogleAdapterTest.MockGoogleTokenClients mockGoogleTokenClients = new GoogleAdapterTest.MockGoogleTokenClients();
        GoogleAdapterTest.MockGoogleProfileClients mockGoogleProfileClients = new GoogleAdapterTest.MockGoogleProfileClients();
        GoogleAdapter googleAdapter = new GoogleAdapter(mockGoogleTokenClients, mockGoogleProfileClients);

        // when
        String accessToken = googleAdapter.getToken("tokenUrl");

        // then
        //System.out.println("accessToken = " + accessToken);
        assertThat(accessToken).isEqualTo("access-token");

    }

    @Test
    @DisplayName("google 토큰 요청 중 예외가 발생하면, OAUTH_INVALID_TOKEN_URL 에외가 발생한다.")
    void googleAdapterGetTokenFail() {
        // given
        String tokenURL = googleURLBuilder.token("error-token", "state");

        // when
        OAuthException exception = assertThrows(OAuthException.class,
                () -> googleAdapter.getToken(tokenURL));

        // then
        assertThat(exception.getMessage()).isEqualTo(ExceptionMessage.OAUTH_INVALID_TOKEN_URL.getText());

    }

    @Test
    @DisplayName("google 프로필 요청 API에 정상적인 요청을 보내면, 사용자 프로필이 반환된다.")
    void googleAdapterGetProfileSuccess() {
        // given

        GoogleAdapterTest.MockGoogleTokenClients mockGoogleTokenClients = new GoogleAdapterTest.MockGoogleTokenClients();
        GoogleAdapterTest.MockGoogleProfileClients mockGoogleProfileClients = new GoogleAdapterTest.MockGoogleProfileClients();
        GoogleAdapter googleAdapter = new GoogleAdapter(mockGoogleTokenClients, mockGoogleProfileClients);

        // when
        OAuthResponse profile = googleAdapter.getProfile("access-token");

        // then
        assertAll(
                () -> assertThat(profile.getPlatformId()).isEqualTo(expectedPlatformId),  // google은 sub
                () -> assertThat(profile.getProfileImageUrl()).isEqualTo(expectedProfileImageUrl),
                () -> assertThat(profile.getName()).isEqualTo(expectedName),
                () -> assertThat(profile.getPlatformType()).isEqualTo(GOOGLE)
        );
    }

    @Test
    @DisplayName("google 프로필 요청 중 예외가 발생하면, OAUTH_INVALID_ACCESS_TOKEN 예외가 발생한다.")
    void googleAdapterGetProfileFail() {

        // when
        OAuthException exception = assertThrows(OAuthException.class,
                () -> googleAdapter.getProfile("error-token"));

        // then
        assertThat(exception.getMessage()).isEqualTo(ExceptionMessage.OAUTH_INVALID_ACCESS_TOKEN.getText());

    }



    static class MockGoogleTokenClients implements GoogleTokenClients {

        @Override
        public GoogleTokenResponse getToken(URI uri) {
            return new GoogleTokenResponse("access-token");
        }
    }

    static class MockGoogleProfileClients implements GoogleProfileClients {

        @Override
        public GoogleProfileResponse getProfile(String header) {
            return new GoogleProfileResponse(GoogleAdapterTest.expectedPlatformId,
                    GoogleAdapterTest.expectedName,
                    GoogleAdapterTest.expectedProfileImageUrl);
        }
    }
}
