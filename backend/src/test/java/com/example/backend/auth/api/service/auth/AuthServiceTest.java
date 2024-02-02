package com.example.backend.auth.api.service.auth;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.controller.auth.response.AuthLoginResponse;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.request.AuthServiceRegisterRequest;
import com.example.backend.auth.api.service.auth.response.AuthServiceLoginResponse;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.api.service.oauth.OAuthService;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.common.exception.auth.AuthException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.domain.define.account.user.constant.UserRole;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;
import static com.example.backend.domain.define.account.user.constant.UserPlatformType.KAKAO;
import static com.example.backend.domain.define.account.user.constant.UserRole.USER;
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
        String code = "code";
        String state = "state";

        OAuthResponse oAuthResponse = generateOauthResponse();
        UserPlatformType platformType = oAuthResponse.getPlatformType();
        String platformId = oAuthResponse.getPlatformId();

        when(oAuthService.login(any(UserPlatformType.class), any(String.class), any(String.class)))
                .thenReturn(oAuthResponse);

        // when
        authService.login(GITHUB, code, state);

        User user = userRepository.findByPlatformIdAndPlatformType(platformId, platformType).get();

        // then
        assertThat(user).isNotNull();
        assertThat(user.getRole().name()).isEqualTo(UserRole.UNAUTH.name());
    }

    @Test
    @DisplayName("OAuth 사용자 정보 변경시 DB에 업데이트되어야 한다.")
    void loginUserProfileUpdate() {
        // given
        String code = "code";
        String state = "state";

        OAuthResponse oAuthResponse = generateOauthResponse();
        UserPlatformType platformType = oAuthResponse.getPlatformType();

        String platformId = oAuthResponse.getPlatformId();
        String updateName = "test";
        String updateProfileImageUrl = "www.test.com";

        when(oAuthService.login(any(UserPlatformType.class), any(String.class), any(String.class)))
                .thenReturn(oAuthResponse);
        authService.login(GITHUB, code, state);

        oAuthResponse = OAuthResponse.builder()
                .platformId(oAuthResponse.getPlatformId())
                .platformType(GITHUB)
                .name(updateName)
                .profileImageUrl(updateProfileImageUrl)
                .build();

        when(oAuthService.login(any(UserPlatformType.class), any(String.class), any(String.class)))
                .thenReturn(oAuthResponse);

        // when
        authService.login(GITHUB, code, state);
        User findUser = userRepository.findByPlatformIdAndPlatformType(platformId, platformType).get();

        // then
        assertThat(findUser).isNotNull();
        assertThat(findUser.getName()).isEqualTo(updateName);
        assertThat(findUser.getProfileImageUrl()).isEqualTo(updateProfileImageUrl);


    }

    @Test
    @DisplayName("OAuth 로그인 인증 완료 후 JWT 토큰이 정상적으로 발급된다.")
    void loginJwtTokenGenerate() {
        // given
        String code = "code";
        String state = "state";

        OAuthResponse oAuthResponse = generateOauthResponse();
        when(oAuthService.login(any(UserPlatformType.class), any(String.class), any(String.class)))
                .thenReturn(oAuthResponse);

        // when
        AuthLoginResponse loginResponse = authService.login(GITHUB, code, state);
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
        String code = "code";
        String state = "state";

        String platformId = "platformId";
        String platformType = "platformType";



        String expectedPlatformId = "1";
        String expectedPlatformType = "GITHUB";



        OAuthResponse oAuthResponse = generateOauthResponse();
        when(oAuthService.login(any(UserPlatformType.class), any(String.class), any(String.class)))
                .thenReturn(oAuthResponse);

        // when
        AuthLoginResponse loginResponse = authService.login(GITHUB, code, state);
        String atk = loginResponse.getAccessToken();
        Claims claims = jwtService.extractAllClaims(atk);

        // then
        assertAll(
                () -> assertThat(claims.get(platformId)).isEqualTo(expectedPlatformId),
                () -> assertThat(claims.get(platformType)).isEqualTo(expectedPlatformType)
        );
    }
    @Test
    @DisplayName("UNAUTH 미가입자 회원가입 성공 테스트")
    public void registerUnauthUserSuccessTest() {
        UserPlatformType platformType = KAKAO;
        String platformId = "1234";
        String name = "testUser";
        String profileImageUrl = "https://example.com/profile.jpg";
        String githubId = "test@github.com";
        // UNAUTH 사용자 저장
        User unauthUser = User.builder()
                .role(UserRole.UNAUTH)
                .platformId(platformId)
                .platformType(platformType)
                .name(name)
                .profileImageUrl(profileImageUrl)
                .build();

        User findUser = userRepository.save(unauthUser);

        // 회원가입 요청 생성 (CENTER)
        AuthServiceRegisterRequest request = AuthServiceRegisterRequest.builder()
                .role(USER)
                .platformId(platformId)
                .platformType(platformType)
                .githubId(githubId)
                .name(name)
                .build();

        // when
        AuthServiceLoginResponse response = authService.register(request);

        User savedUser = userRepository.findByPlatformIdAndPlatformType(request.getPlatformId(), request.getPlatformType()).orElse(null);
        boolean tokenValid = jwtService.isTokenValid(response.getAccessToken(), savedUser.getUsername());   // 발행한 토큰 검증


        // then
        assertEquals(USER, response.getRole());
        assertThat(tokenValid).isTrue();
    }

    @Test
    @DisplayName("UNAUTH 미가입자 회원가입 실패 테스트")
    public void registerUnauthUserFailTest() {
        UserPlatformType platformType = KAKAO;
        String platformId = "1234";
        String name = "testUser";
        String profileImageUrl = "https://example.com/profile.jpg";
        String githubId = "test@github.com";

        User user = User.builder()
                .platformId(platformId)
                .platformType(platformType)
                .role(USER)
                .name(name)
                .profileImageUrl(profileImageUrl)
                .build();

        userRepository.save(user);

        // 회원가입 요청 생성
        AuthServiceRegisterRequest request = AuthServiceRegisterRequest.builder()
                .role(USER)
                .platformId(platformId)
                .platformType(platformType)
                .name(name)
                .githubId(githubId)
                .build();

        // then
        assertThrows(RuntimeException.class, () -> {
            authService.register(request);
        });
    }
    @Test
    @DisplayName("존재하지 않는 userName으로 계정삭제를 진행할 수 없다.")
    void isNotProcessingWhenUserNameIsNotExist() {
        // given
        String invalidUserName = "1234_KAKAO";

        // when
        assertThrows(AuthException.class,
                () -> authService.userDelete(invalidUserName));
    }
    @Test
    @DisplayName("존재하는 계정의 userName으로 계정삭제를 진행할 수 있다.")
    void successProcessingWhenUserNameIsExistInDB() {
        String platformId="1234";
        String platformType="KAKAO";
        // given
        User user = User.builder()
                .platformId(platformId)
                .platformType(UserPlatformType.valueOf(platformType))
                .name("김민수")
                .profileImageUrl("google.co.kr")
                .role(USER)
                .build();
        userRepository.save(user);

        // when
        authService.userDelete(platformId+"_"+platformType);
        User deletedUser = userRepository.findByPlatformIdAndPlatformType(user.getPlatformId(), user.getPlatformType()).orElse(null);

        // then
        assertThat(deletedUser.getRole()).isEqualTo(UserRole.WITHDRAW);
    }

    @Test
    @DisplayName("유저 정보가 가져와지는지 확인")
    void getUserByInfoTest() {
        // given
        String expectedProfileUrl = "https://google.com";
        String expectedGithubId = "jusung-c";

        User savedUser = userRepository.save(generateUser());

        // when
        UserInfoResponse expectedUser = authService.getUserByInfo(savedUser.getPlatformId(), savedUser.getPlatformType());

        // then
        assertThat(expectedUser).isNotNull();
        assertEquals(expectedUser.getRole(), USER);
        assertEquals(expectedUser.getProfileImageUrl(), expectedProfileUrl);
        assertEquals(expectedUser.getGithubId(), expectedGithubId);

    }

}