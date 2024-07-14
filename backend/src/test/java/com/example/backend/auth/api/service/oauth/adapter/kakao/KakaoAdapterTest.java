package com.example.backend.auth.api.service.oauth.adapter.kakao;


import com.example.backend.TestConfig;
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

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.KAKAO;
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
        Long expetedId = 1L;
        String expectedName = "구영민";
        String expectedNickName = "구영민";
        String expectedProfileImageUrl = "http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_640x640.jpg";
        String expectedThumbnailImage = "http://k.kakaocdn.net/dn/1G9kp/btsAot8liOn/8CWudi3uy07rvFNUkk3ER0/img_110x110.jpg";

        KakaoAdapterTest.MockKakaoTokenClients mockKakaoTokenClients = new KakaoAdapterTest.MockKakaoTokenClients();
        KakaoAdapterTest.MockKakaoProfileClients mockKakaoProfileClients = new KakaoAdapterTest.MockKakaoProfileClients(expetedId,
                expectedName,
                new KakaoProfileResponse.Properties(expectedNickName, expectedProfileImageUrl, expectedThumbnailImage));
        KakaoAdapter kakaoAdapter = new KakaoAdapter(mockKakaoTokenClients, mockKakaoProfileClients);

        // when
        OAuthResponse profile = kakaoAdapter.getProfile("access-token");

        // then
        assertAll(
                () -> assertThat(profile.getPlatformId()).isEqualTo(expetedId.toString()),
                () -> assertThat(profile.getProfileImageUrl()).isEqualTo(expectedProfileImageUrl),
                () -> assertThat(profile.getName()).isEqualTo(expectedName),
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
        private Long id;
        private String name;
        private KakaoProfileResponse.Properties properties;
        MockKakaoProfileClients(Long id, String name, KakaoProfileResponse.Properties properties){
            this.id = id;
            this.name = name;
            this.properties = properties;
        }
        MockKakaoProfileClients(){};
        public static class Properties {
            private String nickname;
            private String profile_image;
            private String thumbnail_image;

            public Properties(String nickname, String profile_image, String thumbnail_image) {
                this.nickname = nickname;
                this.profile_image = profile_image;
                this.thumbnail_image = thumbnail_image;
            }
        }
        @Override
        public KakaoProfileResponse getProfile(String header) {
            return new KakaoProfileResponse(id,
                    name,
                    new KakaoProfileResponse.Properties(properties.getNickname(),
                            properties.getProfile_image(),
                            properties.getThumbnail_image()))
                    ;
        }
    }
}
