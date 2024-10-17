package com.example.backend.auth.api.controller.auth;

import com.example.backend.MockTestConfig;
import com.example.backend.auth.api.controller.auth.request.AdminLoginRequest;
import com.example.backend.auth.api.controller.auth.request.AuthRegisterRequest;
import com.example.backend.auth.api.controller.auth.request.UserNameRequest;
import com.example.backend.auth.api.controller.auth.request.UserUpdateRequest;
import com.example.backend.auth.api.controller.auth.response.AuthLoginResponse;
import com.example.backend.auth.api.controller.auth.response.UserInfoAndRankingResponse;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.auth.request.AuthServiceRegisterRequest;
import com.example.backend.auth.api.service.auth.request.UserUpdateServiceRequest;
import com.example.backend.auth.api.service.auth.response.UserUpdatePageResponse;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.api.service.rank.RankingService;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.auth.AuthException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.SocialInfo;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserRole;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.study.api.controller.member.request.MessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.*;
import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
class AuthControllerTest extends MockTestConfig {

    @Value("${tester.token}")
    private String testerToken;
    @Value("${tester.id}")
    private String testerId;
    @Value("${tester.password}")
    private String testerPassword;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService mockAuthService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RankingService mockRankingService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }


    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logoutSuccessTest() throws Exception {
        // given
        User savedUser = userRepository.save(generateGoogleUser());

        HashMap<String, String> map = new HashMap<>();
        map.put("role", savedUser.getRole().name());
        map.put("platformId", savedUser.getPlatformId());
        map.put("platformType", String.valueOf(savedUser.getPlatformType()));

        String accessToken = jwtService.generateAccessToken(map, savedUser);

        // when
        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))
                // then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그아웃 실패 테스트 - 잘못된 토큰으로 요청시 예외 발생")
    void logoutTestWhenInvalidToken() throws Exception {
        String accessToken = "strangeToken";

        // when
        mockMvc.perform(
                        post("/auth/logout")
                                .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.title").value(UNAUTHORIZED.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value(ExceptionMessage.JWT_MALFORMED.getText()));
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void registerSuccessTest() throws Exception {
        // given
        User savedUser = userRepository.save(generateUNAUTHUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        String extendedAtk = "atk";
        String extendedRtk = "rtk";

        // 유효성 검사 통과하는 request
        AuthRegisterRequest request = AuthRegisterRequest.builder()
                .name("구영민")
                .pushAlarmYn(false)
                .fcmToken("token")
                .build();

        when(mockAuthService.register(any(AuthServiceRegisterRequest.class), any(User.class)))
                .thenReturn(AuthLoginResponse.builder()
                        .accessToken(extendedAtk)
                        .refreshToken(extendedRtk)
                        .build());

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                                .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(extendedAtk))
                .andExpect(jsonPath("$.refresh_token").value(extendedRtk));
    }

    @Test
    @DisplayName("올바른 사용자의 토큰으로 사용자 계정 탈퇴 요청을 하면, 계정이 삭제된다.")
    void validUserTokenRequestWithDrawThenUserDelete() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        when(mockAuthService.register(any(AuthServiceRegisterRequest.class), any(User.class))).thenReturn(AuthLoginResponse.builder()
                .accessToken(accessToken)
                //.refreshToken(refreshToken)
                .build()
        );

        MessageRequest request = MessageRequest.builder()
                .message("reason")
                .build();

        doNothing().when(mockAuthService).userDelete(any(User.class), any(String.class));

        // when
        mockMvc.perform(post("/auth/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))
                // then
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("유저정보 조회 성공 테스트")
    void userInfoSuccessTest() throws Exception {
        //given
        User user = generateAuthUser();
        UserInfoAndRankingResponse savedUser = UserInfoAndRankingResponse.of(userRepository.save(user), 1L);


        when(mockAuthService.getUserByInfo(user.getPlatformId(), GITHUB)).thenReturn(savedUser);

        HashMap<String, String> map = new HashMap<>();
        map.put("role", user.getRole().name());
        map.put("platformId", user.getPlatformId());
        map.put("platformType", String.valueOf(user.getPlatformType()));

        String accessToken = jwtService.generateAccessToken(map, user);

        // when
        mockMvc.perform(get("/auth/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value(String.valueOf(UserRole.USER)))
                .andExpect(jsonPath("$.name").value(savedUser.getName()))
                .andExpect(jsonPath("$.profile_image_url").value(savedUser.getProfileImageUrl()));

    }

    @Test
    @DisplayName("유저정보 조회 실패 테스트 - 잘못된 Token")
    void userInfoWhenInvalidToken() throws Exception {
        // given
        String accessToken = "strangeToken";

        // when
        mockMvc.perform(get("/auth/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.JWT_MALFORMED.getText()));
    }

    @Test
    @DisplayName("유저정보 조회 실패 테스트 - 잘못된 권한")
    void userInfoWhenInvalidAuthority() throws Exception {
        User user = generateUNAUTHUser();
        User savedUser = userRepository.save(user);

        HashMap<String, String> map = new HashMap<>();
        map.put("role", savedUser.getRole().name());
        map.put("platformId", savedUser.getPlatformId());
        map.put("platformType", String.valueOf(savedUser.getPlatformType()));

        String accessToken = jwtService.generateAccessToken(map, user);

        // when
        mockMvc.perform(get("/auth/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))
                // then
                .andExpect(status().isForbidden());

    }

    @Test
    void 사용자_정보_수정_페이지_성공_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        // when
        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(savedUser));
        when(mockAuthService.updateUserPage(any(Long.class))).thenReturn(UserUpdatePageResponse.builder()
                .name(savedUser.getName())
                .profileImageUrl(savedUser.getProfileImageUrl())
                .build());

        // then
        mockMvc.perform(get("/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(savedUser.getName()))
                .andExpect(jsonPath("$.profile_image_url").value(savedUser.getProfileImageUrl()));

    }

    @Test
    void 사용자_정보_수정_페이지_실패_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        // when
        when(mockAuthService.findUserInfo(any(User.class))).thenThrow(new AuthException(ExceptionMessage.UNAUTHORIZED_AUTHORITY));

        // then
        mockMvc.perform(get("/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.UNAUTHORIZED_AUTHORITY.getText()));

    }

    @Test
    void 사용자_정보_수정_성공_테스트_링크_null일_경우() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .name(savedUser.getName())
                .profileImageUrl(savedUser.getProfileImageUrl())
                .profilePublicYn(false)
                .socialInfo(SocialInfo.builder()
                        .blogLink("https://test.tistory.com/").build())
                .build();
        // when
        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.builder().build());
        doNothing().when(mockAuthService).updateUser(any(UserUpdateServiceRequest.class));

        // then
        mockMvc.perform(post("/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(updateRequest)))

                // then
                .andExpect(status().isOk());
    }

    @Test
    void 사용자_정보_수정_성공_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .name(savedUser.getName())
                .profileImageUrl(savedUser.getProfileImageUrl())
                .profilePublicYn(false)
                .socialInfo(SocialInfo.builder()
                        .githubLink("https://github.com/test")
                        .blogLink("https://test.tistory.com/")
                        .linkedInLink("https://test.tistory.com/")
                        .build())
                .build();
        // when
        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.builder().build());
        doNothing().when(mockAuthService).updateUser(any(UserUpdateServiceRequest.class));

        // then
        mockMvc.perform(post("/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(updateRequest)))

                // then
                .andExpect(status().isOk());
    }

    @Test
    void 사용자_정보_수정_실패_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .name(savedUser.getName())
                .profileImageUrl(savedUser.getProfileImageUrl())
                .profilePublicYn(false)
                .socialInfo(SocialInfo.builder()
                        .blogLink("test@naver.com").build())
                .build();

        // when
        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.builder().build());
        doThrow(new AuthException(ExceptionMessage.UNAUTHORIZED_AUTHORITY))
                .when(mockAuthService)
                .updateUser(any(UserUpdateServiceRequest.class));

        // then
        mockMvc.perform(post("/auth/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(updateRequest)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.UNAUTHORIZED_AUTHORITY.getText()));
    }


    @Test
    void 푸시_알림_여부_수정_성공_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        // when
        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.builder().build());
        doNothing().when(mockAuthService).updatePushAlarmYn(any(Long.class), any(boolean.class));

        // then
        mockMvc.perform(get("/auth/update/pushAlarmYn" + "/" + true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isOk());
    }

    @Test
    void 푸시_알림_여부_수정_실패_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        // when
        when(mockAuthService.findUserInfo(any(User.class))).thenThrow(new AuthException(ExceptionMessage.UNAUTHORIZED_AUTHORITY));
        // then
        mockMvc.perform(get("/auth/update/pushAlarmYn" + "/" + true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.UNAUTHORIZED_AUTHORITY.getText()));
    }

    @Test
    void 닉네임_중복체크_테스트() throws Exception {
        // given
        UserNameRequest request = UserFixture.generateUserNameRequest("이정우");

        doNothing().when(mockAuthService).nickNameDuplicationCheck(any(UserNameRequest.class));

        // when & then
        mockMvc.perform(post("/auth/check-nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                // then
                .andExpect(status().isOk());
    }

    @Test
    void 닉네임_중복체크_유효성_검증_실패_테스트1() throws Exception {
        // given
        String inValidName = "   ";
        String expectedError = "name: 이름은 공백일 수 없습니다.";

        UserNameRequest request = UserFixture.generateUserNameRequest(inValidName);

        doNothing().when(mockAuthService).nickNameDuplicationCheck(any(UserNameRequest.class));

        // when
        mockMvc.perform(post("/auth/check-nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedError));
    }

    @Test
    void 닉네임_중복체크_유효성_검증_실패_테스트2() throws Exception {
        // given
        String inValidName = "엄청나게긴긴긴긴닉네임";

        UserNameRequest request = UserFixture.generateUserNameRequest(inValidName);

        doNothing().when(mockAuthService).nickNameDuplicationCheck(any(UserNameRequest.class));

        // when
        mockMvc.perform(post("/auth/check-nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                // then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Admin 로그인 성공 테스트")
    void adminLoginSuccessTest() throws Exception {
        // given
        AdminLoginRequest request = AdminLoginRequest.builder()
                .id("admin")
                .password(testerPassword)
                .build();

        AuthLoginResponse response = AuthLoginResponse.builder()
                .accessToken(testerToken)
                .build();

        when(mockAuthService.loginAdmin(any(AdminLoginRequest.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/auth/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(testerToken));
    }

    @Test
    @DisplayName("Admin 로그인 실패 테스트 - 잘못된 아이디로 로그인 시도")
    void adminLoginFailDueToIncorrectId() throws Exception {
        // given
        AdminLoginRequest request = AdminLoginRequest.builder()
                .id("not_admin") // 잘못된 아이디
                .password(testerPassword) // 올바른 패스워드
                .build();

        doThrow(new UserException(ExceptionMessage.USER_NOT_ADMIN_ID))
                .when(mockAuthService)
                .loginAdmin(any(AdminLoginRequest.class));

        // when & then
        mockMvc.perform(post("/auth/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.USER_NOT_ADMIN_ID.getText()));
    }

    @Test
    @DisplayName("Admin 로그인 실패 테스트 - 잘못된 패스워드로 로그인 시도")
    void adminLoginFailDueToIncorrectPassword() throws Exception {
        // given
        AdminLoginRequest request = AdminLoginRequest.builder()
                .id(testerId) // 올바른 아이디
                .password("wrongPassword") // 잘못된 패스워드
                .build();

        doThrow(new UserException(ExceptionMessage.USER_NOT_ADMIN_PASSWORD))
                .when(mockAuthService)
                .loginAdmin(any(AdminLoginRequest.class));

        // when & then
        mockMvc.perform(post("/auth/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.USER_NOT_ADMIN_PASSWORD.getText()));
    }
}