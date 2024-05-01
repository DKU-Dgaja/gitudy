package com.example.backend.auth.api.service.auth;

import com.example.backend.MockTestConfig;
import com.example.backend.TestConfig;
import com.example.backend.auth.api.controller.auth.request.UserNameRequest;
import com.example.backend.auth.api.controller.auth.response.AuthLoginResponse;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.request.AuthServiceRegisterRequest;
import com.example.backend.auth.api.service.auth.request.UserUpdateServiceRequest;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.api.service.oauth.OAuthService;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.auth.AuthException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.SocialInfo;
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

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static com.example.backend.auth.config.fixture.UserFixture.generateOauthResponse;
import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;
import static com.example.backend.domain.define.account.user.constant.UserPlatformType.KAKAO;
import static com.example.backend.domain.define.account.user.constant.UserRole.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthServiceTest extends MockTestConfig {

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

        OAuthResponse oAuthResponse = generateOauthResponse();
        when(oAuthService.login(any(UserPlatformType.class), any(String.class), any(String.class)))
                .thenReturn(oAuthResponse);

        // when
        AuthLoginResponse loginResponse = authService.login(GITHUB, code, state);
        String atk = loginResponse.getAccessToken();
        Claims claims = jwtService.extractAllClaims(atk);

        // then
        assertAll(
                () -> assertThat(claims.get(platformId)).isEqualTo(oAuthResponse.getPlatformId()),
                () -> assertThat(claims.get(platformType)).isEqualTo(GITHUB.name())
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
        User unauthUser = userRepository.save(User.builder()
                .role(UserRole.UNAUTH)
                .platformId(platformId)
                .platformType(platformType)
                .name(name)
                .profileImageUrl(profileImageUrl)
                .build());

        // 회원가입 요청 생성
        AuthServiceRegisterRequest request = AuthServiceRegisterRequest.builder()
                .githubId(githubId)
                .name(name)
                .pushAlarmYn(true)
                .build();

        // when
        AuthLoginResponse response = authService.register(request, unauthUser);

        User savedUser = userRepository.findByPlatformIdAndPlatformType(unauthUser.getPlatformId(), unauthUser.getPlatformType()).orElse(null);
        boolean tokenValid = jwtService.isTokenValid(response.getAccessToken(), savedUser.getUsername());   // 발행한 토큰 검증


        // then
        assertThat(tokenValid).isTrue();
        assertTrue(savedUser.isPushAlarmYn());
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
                .name(name)
                .githubId(githubId)
                .build();

        // then
        assertThrows(RuntimeException.class, () -> {
            authService.register(request, user);
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
        String platformId = "1234";
        String platformType = "KAKAO";
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
        authService.userDelete(platformId + "_" + platformType);
        User deletedUser = userRepository.findByPlatformIdAndPlatformType(user.getPlatformId(), user.getPlatformType()).orElse(null);

        // then
        assertThat(deletedUser.getRole()).isEqualTo(UserRole.WITHDRAW);
    }

    @Test
    @DisplayName("유저 정보가 가져와지는지 확인")
    void getUserByInfoTest() {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        System.out.println("savedUser = " + savedUser.getPlatformId());
        System.out.println("savedUser = " + savedUser.getPlatformType());

        // when
        UserInfoResponse expectedUser = authService.getUserByInfo(savedUser.getPlatformId(), savedUser.getPlatformType());

        // then
        assertThat(expectedUser).isNotNull();
        assertEquals(expectedUser.getRole(), USER);
        assertEquals(expectedUser.getProfileImageUrl(), savedUser.getProfileImageUrl());
        assertEquals(expectedUser.getGithubId(), savedUser.getGithubId());

    }

    @Test
    void 로그인한_사용자가_권한이_있는지_확인_성공_테스트() {
        User user = userRepository.save(generateAuthUser());

        User loginUser = User.builder()
                .role(user.getRole())
                .platformId(user.getPlatformId())
                .platformType(user.getPlatformType())
                .build();

        assertDoesNotThrow(() -> authService.authenticate(user.getId(), loginUser));
    }

    @Test
    void 로그인한_사용자가_권한이_있는지_확인_실패_테스트() {
        Long userId = -1L;

        User user = userRepository.save(generateAuthUser());

        User loginUser = User.builder()
                .role(user.getRole())
                .platformId(user.getPlatformId())
                .platformType(user.getPlatformType())
                .build();

        assertThrows(AuthException.class, () -> {
            authService.authenticate(userId, loginUser);
        });
    }

    @Test
    void 회원_정보_수정_성공_테스트() {
        // given
        String updateName = "updateName";
        String updateProfileImageUrl = "updateProfileImageUrl";
        boolean updateProfilePublicYn = false;
        SocialInfo updateSocialInfo = SocialInfo.builder().blogLink("test@naver.com").build();

        User user = userRepository.save(generateAuthUser());
        UserUpdateServiceRequest request = UserUpdateServiceRequest.builder()
                .userId(user.getId())
                .name(updateName)
                .profilePublicYn(updateProfilePublicYn)
                .profileImageUrl(updateProfileImageUrl)
                .socialInfo(updateSocialInfo)
                .build();

        // when
        authService.updateUser(request);
        User updateUser = userRepository.findById(request.getUserId()).get();

        // then
        assertAll(
                () -> assertThat(updateUser.getName()).isEqualTo(updateName),
                () -> assertThat(updateUser.getProfileImageUrl()).isEqualTo(updateProfileImageUrl),
                () -> assertThat(updateUser.isProfilePublicYn()).isEqualTo(updateProfilePublicYn),
                () -> assertThat(updateUser.getSocialInfo().getBlogLink()).isEqualTo(updateSocialInfo.getBlogLink()));
    }

    @Test
    void 회원_정보_수정_실패_테스트() {
        // given
        String updateName = "updateName";
        String updateProfileImageUrl = "updateProfileImageUrl";
        boolean updateProfilePublicYn = false;
        SocialInfo updateSocialInfo = SocialInfo.builder().blogLink("test@naver.com").build();

//        User user = userRepository.save(generateAuthUser());
        UserUpdateServiceRequest request = UserUpdateServiceRequest.builder()
                .userId(1L)
                .name(updateName)
                .profilePublicYn(updateProfilePublicYn)
                .profileImageUrl(updateProfileImageUrl)
                .socialInfo(updateSocialInfo)
                .build();

        // when
        assertThrows(UserException.class, () -> {
            authService.updateUser(request);
        });

    }

    @Test
    void 사용자_푸시_알림_여부_수정_테스트() {
        // given
        User user = userRepository.save(generateAuthUser());

        // when
        authService.updatePushAlarmYn(user.getId(), true);
        User updateUser = userRepository.findById(user.getId()).get();

        // then
        assertTrue(updateUser.isPushAlarmYn());

        // when
        authService.updatePushAlarmYn(user.getId(), false);
        User updateUser2 = userRepository.findById(user.getId()).get();

        // then
        assertFalse(updateUser2.isPushAlarmYn());
    }

    @Test
    void 닉네임_중복체크_테스트_중복인경우() {
        // given
        userRepository.save(generateAuthUser());

        UserNameRequest request = UserFixture.generateUserNameRequest("이름");

        // then
        UserException em = assertThrows(UserException.class, () -> {
            authService.nickNameDuplicationCheck(request);
        });

        assertEquals(ExceptionMessage.USER_NAME_DUPLICATION.getText(), em.getMessage());
    }

    @Test
    void 닉네임_중복체크_테스트_중복이아닌경우() {
        // given
        userRepository.save(generateAuthUser());

        UserNameRequest request = UserFixture.generateUserNameRequest("이정우");

        // when
        authService.nickNameDuplicationCheck(request);

        // then
        assertDoesNotThrow(() -> authService.nickNameDuplicationCheck(request));
    }

}