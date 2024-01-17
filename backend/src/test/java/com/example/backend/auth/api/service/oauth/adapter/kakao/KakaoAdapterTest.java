package com.example.backend.auth.api.service.oauth.adapter.kakao;


import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.service.oauth.builder.kakao.KakaoURLBuilder;

import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.oauth.OAuthException;
import com.example.backend.external.clients.oauth.kakao.KakaoProfileClients;
import com.example.backend.external.clients.oauth.kakao.KakaoTokenClients;
import com.example.backend.external.clients.oauth.kakao.response.KakaoProfileResponse;
import com.example.backend.external.clients.oauth.kakao.response.KakaoTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;

import static com.example.backend.domain.define.user.constant.UserPlatformType.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class KakaoAdapterTest extends TestConfig {
    @Autowired
    private KakaoAdapter kakaoAdapter;

    @Autowired
    private KakaoURLBuilder kakaoURLBuilder;
    @Test
    @DisplayName("kakao 토큰 요청 API에 정상적인 요청을 보내면, access_token이 발행된다.")
    void kakaoAdapterGetTokenSuccess() {
        // given
        KakaoAdapterTest.MockKakaoTokenClients mockKakaoTokenClients = new KakaoAdapterTest.MockKakaoTokenClients();
        KakaoAdapterTest.MockKakaoProfileClients mockKakaoProfileClients = new KakaoAdapterTest.MockKakaoProfileClients();
        KakaoAdapter kakaoAdapter = new KakaoAdapter(mockKakaoTokenClients, mockKakaoProfileClients);

        // when
        String accessToken = kakaoAdapter.getToken("tokenUrl");

        // then
        System.out.println("accessToken = " + accessToken);
        assertThat(accessToken).isEqualTo("access-token");

    }


    @Test
    @DisplayName("kakao 토큰 요청 중 예외가 발생하면, OAUTH_INVALID_TOKEN_URL 에외가 발생한다.")
    void kakaoAdapterGetTokenFail() {
        // given
        String tokenURL = kakaoURLBuilder.token("error-token", "state");

        // when
        OAuthException exception = assertThrows(OAuthException.class,
                () -> kakaoAdapter.getToken(tokenURL));

        // then
        assertThat(exception.getMessage()).isEqualTo(ExceptionMessage.OAUTH_INVALID_TOKEN_URL.getText());

    }




    @Test
    @DisplayName("kakao 프로필 요청 API에 정상적인 요청을 보내면, 사용자 프로필이 반환된다.")
    void kakaoAdapterGetProfileSuccess() {
        // given
        KakaoAdapterTest.MockKakaoTokenClients mockKakaoTokenClients = new KakaoAdapterTest.MockKakaoTokenClients();
        KakaoAdapterTest.MockKakaoProfileClients mockKakaoProfileClients = new KakaoAdapterTest.MockKakaoProfileClients();
        KakaoAdapter kakaoAdapter = new KakaoAdapter(mockKakaoTokenClients, mockKakaoProfileClients);

        // when
        OAuthResponse profile = kakaoAdapter.getProfile("access-token");

        // then
        assertAll(
                () -> assertThat(profile.getPlatformId()).isEqualTo("1"),
                () -> assertThat(profile.getProfileImageUrl()).isEqualTo("http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_640x640.jpg"),
                () -> assertThat(profile.getName()).isEqualTo("구영민"),
                () -> assertThat(profile.getPlatformType()).isEqualTo(KAKAO)
        );
    }


    @Test // X
    @DisplayName("kakao 프로필 요청 중 예외가 발생하면, OAUTH_INVALID_ACCESS_TOKEN 예외가 발생한다.")
    void kakaoAdapterGetProfileFail() {
        // when
        OAuthException exception = assertThrows(OAuthException.class,
                () -> kakaoAdapter.getProfile("error-token"));

        // then
        assertThat(exception.getMessage()).isEqualTo(ExceptionMessage.OAUTH_INVALID_ACCESS_TOKEN.getText());

    }





    static class MockKakaoTokenClients implements KakaoTokenClients {

        @Override
        public KakaoTokenResponse getToken(URI uri) {
            return new KakaoTokenResponse("access-token");
        }
    }
    static class MockKakaoProfileClients implements KakaoProfileClients {

        @Override
        public KakaoProfileResponse getProfile(String header) {
            return new KakaoProfileResponse(1L,
                    "구영민",
                    new KakaoProfileResponse.Properties("구영민",
                            "http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_640x640.jpg",
                            "http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_110x110.jpg"))
                    ;
        }
    }
}
