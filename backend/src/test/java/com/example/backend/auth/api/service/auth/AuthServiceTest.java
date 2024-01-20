package com.example.backend.auth.api.service.auth;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.controller.auth.response.AuthLoginResponse;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.api.service.oauth.OAuthService;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.domain.define.user.User;
import com.example.backend.domain.define.user.constant.UserPlatformType;
import com.example.backend.domain.define.user.constant.UserRole;
import com.example.backend.domain.define.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.example.backend.domain.define.user.constant.UserPlatformType.GITHUB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthServiceTest extends TestConfig {

    @MockBean
    private OAuthService oAuthService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("신규 사용자의 경우 UNAUTH 권한으로 DB에 저장된다.")
    void registerUnauthUser() {
        // given
        OAuthResponse oAuthResponse = generateOauthResponse();
        when(oAuthService.login(any(UserPlatformType.class), any(String.class), any(String.class)))
                .thenReturn(oAuthResponse);

        // when
        authService.login(GITHUB, "code", "state");

        User user = userRepository.findByEmail(oAuthResponse.getEmail()).get();

        // then
        assertThat(user.getRole().name()).isEqualTo(UserRole.UNAUTH.name());
    }

    @Test
    @DisplayName("OAuth 사용자 정보 변경시 DB에 업데이트되어야 한다.")
    void loginUserProfileUpdate() {
        // given
        OAuthResponse oAuthResponse = generateOauthResponse();
        when(oAuthService.login(any(UserPlatformType.class), any(String.class), any(String.class)))
                .thenReturn(oAuthResponse);
        authService.login(GITHUB, "code", "state");

        oAuthResponse = OAuthResponse.builder()
                .platformId("1")
                .platformType(GITHUB)
                .email("32183520@dankook.ac.kr")
                .name("testName")
                .profileImageUrl("www.test.com")
                .build();

        when(oAuthService.login(any(UserPlatformType.class), any(String.class), any(String.class)))
                .thenReturn(oAuthResponse);

        // when
        authService.login(GITHUB, "code", "state");
        User findUser = userRepository.findByEmail(oAuthResponse.getEmail()).get();

        // then
        assertThat(findUser.getName()).isEqualTo("testName");
        assertThat(findUser.getProfileImageUrl()).isEqualTo("www.test.com");


    }

    @Test
    @DisplayName("OAuth 로그인 인증 완료 후 JWT 토큰이 정상적으로 발급된다.")
    void loginJwtTokenGenerate() {
        // given
        OAuthResponse oAuthResponse = generateOauthResponse();
        when(oAuthService.login(any(UserPlatformType.class), any(String.class), any(String.class)))
                .thenReturn(oAuthResponse);

        // when
        AuthLoginResponse loginResponse = authService.login(GITHUB, "code", "state");
//        System.out.println("Access Token: " + loginResponse.getAccessToken());
//        System.out.println("Refresh Token: " + loginResponse.getRefreshToken());

        // then
        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getAccessToken()).isNotBlank();
        assertThat(loginResponse.getRefreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("OAuth 로그인 인증이 완료된 사용자의 JWT 토큰은 알맞은 Claims가 들어있어야 한다.")
    void loginJwtTokenValidClaims() {
        // given
        String role = "role";
        String name = "name";
        String profileImageUrl = "profileImageUrl";

        OAuthResponse oAuthResponse = generateOauthResponse();
        when(oAuthService.login(any(UserPlatformType.class), any(String.class), any(String.class)))
                .thenReturn(oAuthResponse);

        // when
        AuthLoginResponse loginResponse = authService.login(GITHUB, "code", "state");
        String atk = loginResponse.getAccessToken();
        Claims claims = jwtService.extractAllClaims(atk);

        // then
        assertAll(
                () -> assertThat(claims.get(role)).isEqualTo("UNAUTH"),
                () -> assertThat(claims.get(name)).isEqualTo("jusung-c"),
                () -> assertThat(claims.get(profileImageUrl)).isEqualTo("http://www.naver.com")
        );
    }
}