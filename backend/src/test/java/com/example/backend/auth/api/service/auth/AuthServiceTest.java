package com.example.backend.auth.api.service.auth;


import com.example.backend.TestConfig;
import com.example.backend.auth.api.controller.auth.request.AdminLoginRequest;
import com.example.backend.auth.api.controller.auth.request.UserNameRequest;
import com.example.backend.auth.api.controller.auth.response.AuthLoginResponse;
import com.example.backend.auth.api.controller.auth.response.UserInfoAndRankingResponse;
import com.example.backend.auth.api.service.auth.request.AuthServiceRegisterRequest;
import com.example.backend.auth.api.service.auth.request.UserUpdateServiceRequest;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.api.service.oauth.OAuthService;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.auth.api.service.token.RefreshTokenService;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.auth.AuthException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.SocialInfo;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.domain.define.account.user.constant.UserRole;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.refreshToken.RefreshToken;
import com.example.backend.domain.define.refreshToken.repository.RefreshTokenRepository;
import com.example.backend.domain.define.study.github.GithubApiToken;
import com.example.backend.domain.define.study.github.repository.GithubApiTokenRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.constant.StudyMemberStatus;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.example.backend.auth.config.fixture.UserFixture.*;
import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;
import static com.example.backend.domain.define.account.user.constant.UserPlatformType.KAKAO;
import static com.example.backend.domain.define.account.user.constant.UserRole.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


class AuthServiceTest extends TestConfig {

    @Value("${tester.token}")
    private String testerToken;
    @Value("${tester.id}")
    private String testerId;
    @Value("${tester.password}")
    private String testerPassword;


    @MockBean
    private OAuthService oAuthService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private GithubApiTokenRepository githubApiTokenRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyMemberRepository studyMemberRepository;

    @AfterEach
    void tearDown() {
        githubApiTokenRepository.deleteAll();
        userRepository.deleteAllInBatch();
        githubApiTokenRepository.deleteAll();
        studyInfoRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("신규 사용자의 경우 UNAUTH 권한으로 DB에 저장된다.")
    void registerUnauthUser() {
        // given
        String code = "code";
        String state = "state";
        String token = "githubApiToken";

        OAuthResponse oAuthResponse = generateOauthResponse();
        UserPlatformType platformType = oAuthResponse.getPlatformType();
        String platformId = oAuthResponse.getPlatformId();

        when(oAuthService.login(any(UserPlatformType.class), any(String.class), any(String.class)))
                .thenReturn(oAuthResponse);

        // when
        authService.login(GITHUB, code, state);

        User user = userRepository.findByPlatformIdAndPlatformType(platformId, platformType).get();
        GithubApiToken githubApiToken = githubApiTokenRepository.findByUserId(user.getId()).get();

        // then
        assertThat(user).isNotNull();
        assertThat(user.getRole().name()).isEqualTo(UserRole.UNAUTH.name());
        assertThat(user.getGithubId()).isEqualTo(oAuthResponse.getName());

        // githubApiToken 생성 검증
        assertThat(githubApiToken.githubApiToken()).isEqualTo(token);
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
        String platformId = "1234";
        String name = "testUser";
        String profileImageUrl = "https://example.com/profile.jpg";

        // UNAUTH 사용자 저장
        User unauthUser = userRepository.save(User.builder()
                .role(UserRole.UNAUTH)
                .platformId(platformId)
                .platformType(KAKAO)
                .name(name)
                .profileImageUrl(profileImageUrl)
                .build());

        // 회원가입 요청 생성
        AuthServiceRegisterRequest request = AuthServiceRegisterRequest.builder()
                .name(name)
                .pushAlarmYn(true)
                .fcmToken("token")
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
        String platformId = "1234";
        String name = "testUser";
        String profileImageUrl = "https://example.com/profile.jpg";

        User user = User.builder()
                .platformId(platformId)
                .platformType(KAKAO)
                .role(USER)
                .name(name)
                .profileImageUrl(profileImageUrl)
                .build();

        userRepository.save(user);

        // 회원가입 요청 생성
        AuthServiceRegisterRequest request = AuthServiceRegisterRequest.builder()
                .name(name)
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
        User notSavedUser = generateAuthUser();

        // when
        UserException e = assertThrows(UserException.class, () -> authService.userDelete(notSavedUser, "reason"));
        assertEquals(ExceptionMessage.USER_NOT_FOUND.getText(), e.getMessage());
    }

    @Test
    @DisplayName("존재하는 계정의 userName으로 계정삭제를 진행할 수 있다.")
    void successProcessingWhenUserNameIsExistInDB() {
        // given
        User user = userRepository.save(generateAuthUser());

        // when
        authService.userDelete(user, "reason");
        User deletedUser = userRepository.findById(user.getId()).get();

        // then
        assertThat(deletedUser.getRole()).isEqualTo(UserRole.WITHDRAW);
        assertEquals(deletedUser.getName(), "탈퇴한 사용자");
        assertEquals(deletedUser.getPlatformId(), "DELETED" + deletedUser.getId());
    }

    @Test
    @DisplayName("유저 정보가 가져와지는지 확인")
    void getUserByInfoTest() {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        System.out.println("savedUser = " + savedUser.getPlatformId());
        System.out.println("savedUser = " + savedUser.getPlatformType());

        // when
        UserInfoAndRankingResponse expectedUser = authService.getUserByInfo(savedUser.getPlatformId(), savedUser.getPlatformType());

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

    @Test
    void 로그아웃_토큰_삭제_테스트() {
        //given
        User savedUser = userRepository.save(generateGoogleUser());

        HashMap<String, String> map = new HashMap<>();
        map.put("role", savedUser.getRole().name());
        map.put("platformId", savedUser.getPlatformId());
        map.put("platformType", String.valueOf(savedUser.getPlatformType()));

        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .refreshToken(refreshToken)
                .subject(savedUser.getUsername())
                .build();
        refreshTokenService.saveRefreshToken(refreshTokenEntity);

        // when
        authService.logout(accessToken);

        // then
        Optional<RefreshToken> deletedRefreshToken = refreshTokenRepository.findBySubject(savedUser.getUsername());
        assertFalse(deletedRefreshToken.isPresent());
    }

    @Test
    void 회원_탈퇴_최종_테스트() {
        // given
        User userA = userRepository.save(UserFixture.generateAuthUser());
        User userB = userRepository.save(UserFixture.generateAuthJusung());

        // 탈퇴 회원의 스터디
        StudyInfo studyA = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(userA.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(userA.getId(), studyA.getId()));

        // 탈퇴 회원이 참여중인 스터디
        StudyInfo studyB = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(userB.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(userA.getId(), studyB.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(userB.getId(), studyB.getId()));

        // when
        authService.userDelete(userA, "때리칠게요.");
        List<StudyInfo> allByUserId = studyInfoRepository.findAllByUserId(userA.getId());
        StudyMember member = studyMemberRepository.findByStudyInfoIdAndUserId(studyB.getId(), userA.getId()).get();

        // then
        assertSame(allByUserId.get(0).getStatus(), StudyStatus.STUDY_INACTIVE);
        assertSame(StudyMemberStatus.STUDY_WITHDRAWAL, member.getStatus());

    }

    @Test
    @DisplayName("Admin 로그인 - 아이디 불일치로 실패 테스트")
    void loginAdminFailDueToIncorrectId() {
        // given
        AdminLoginRequest request = AdminLoginRequest.builder()
                .id("not_admin") // 잘못된 아이디
                .password(testerPassword) // 올바른 패스워드
                .build();

        // then
        UserException exception = assertThrows(UserException.class, () -> {
            authService.loginAdmin(request);
        });

        // verify
        assertEquals(ExceptionMessage.USER_NOT_ADMIN_ID.getText(), exception.getMessage());
    }

    @Test
    @DisplayName("Admin 로그인 - 패스워드 불일치로 실패 테스트")
    void loginAdminFailDueToIncorrectPassword() {
        // given
        AdminLoginRequest request = AdminLoginRequest.builder()
                .id(testerId) // 올바른 아이디
                .password("wrongPassword") // 잘못된 패스워드
                .build();

        // then
        UserException exception = assertThrows(UserException.class, () -> {
            authService.loginAdmin(request);
        });

        // verify
        assertEquals(ExceptionMessage.USER_NOT_ADMIN_PASSWORD.getText(), exception.getMessage());
    }

    @Test
    @DisplayName("Admin 로그인 성공 테스트")
    void loginAdminSuccess() {
        // given
        AdminLoginRequest request = AdminLoginRequest.builder()
                .id(testerId) // 올바른 아이디
                .password(testerPassword) // 올바른 패스워드
                .build();

        // when
        AuthLoginResponse response = authService.loginAdmin(request);

        // then
        assertNotNull(response);
        assertEquals(testerToken, response.getAccessToken());
    }

}