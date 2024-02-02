package com.example.backend.auth.api.controller.auth;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.controller.auth.request.AuthRegisterRequest;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.auth.request.AuthServiceRegisterRequest;
import com.example.backend.auth.api.service.auth.response.AuthServiceLoginResponse;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.domain.define.account.user.constant.UserRole;
import com.example.backend.domain.define.account.user.repository.UserRepository;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static com.example.backend.auth.api.service.oauth.adapter.google.GoogleAdapterTest.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

class AuthControllerTest extends TestConfig {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }


    @Test
    @DisplayName("로그아웃 실패 테스트 - 잘못된 토큰으로 요청시 예외 발생")
    void logoutTestWhenInvalidToken() throws Exception {
        String accessToken = "strangeToken";
        String refreshToken = "strangeToken";

        // when
        mockMvc.perform(
                        get("/auth/logout")
                                .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))


                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400))
                .andExpect(jsonPath("$.res_msg").value(ExceptionMessage.JWT_MALFORMED.getText()));
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logoutSuccessTest() throws Exception {
        // given
        User user = User.builder()
                .name(expectedName)
                .role(UserRole.USER)
                .platformType(UserPlatformType.GOOGLE)
                .platformId(expectedPlatformId)
                .profileImageUrl(expectedProfileImageUrl)
                .build();
        User savedUser = userRepository.save(user);

        HashMap<String, String> map = new HashMap<>();
        map.put("role", savedUser.getRole().name());
        map.put("platformId", savedUser.getPlatformId());
        map.put("platformType", String.valueOf(savedUser.getPlatformType()));

        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);


        // when
        mockMvc.perform(get("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_obj").value("로그아웃 되었습니다."));
    }


    @Test
    @DisplayName("로그아웃 실패 테스트 - 잘못된 Header로 요청시 에러 발생")
    void logoutWhenInvalidHeader() throws Exception {
        mockMvc.perform(get("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "INVALID HEADER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400))
                .andExpect(jsonPath("$.res_msg").value(ExceptionMessage.JWT_INVALID_HEADER.getText()));
    }
    @Test
    @DisplayName("회원가입 성공 테스트")
    void registerSuccessTest() throws Exception {
        // given
        // 유효성 검사 통과하는 request
        AuthServiceRegisterRequest request = AuthServiceRegisterRequest.builder()
                .role(UserRole.USER)
                .platformId("1234")
                .platformType(UserPlatformType.KAKAO)
                .name("구영민")
                .githubId("test@1234")
                .build();

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonMapper.builder().build().writeValueAsString(request)))
                .andDo(result -> {
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"));
    }

    @Test
    @DisplayName("잘못된 형식의 깃허브 이메일이면 회원가입 실패")
    void registerFailTest() throws Exception {
        // given
        // 유효성 검사 실패하는 request
        AuthRegisterRequest request = AuthRegisterRequest.builder()
                .role(UserRole.USER) // userRole null
                .platformId("1234")
                .platformType(UserPlatformType.KAKAO)
                .name("구영민")
                .githubId("test1234") // 잘못된 형식의 email
                .build();

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonMapper.builder().build().writeValueAsString(request)))
                // .andExpect(status().isBadRequest());
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400))
                .andExpect(jsonPath("$.res_msg").value("githubId: must be a well-formed email address"));
    }
    @Test
    @DisplayName("올바른 사용자의 토큰으로 사용자 계정 탈퇴 요청을 하면, 계정이 삭제된다.")
    void validUserTokenRequestWithDrawThenUserDelete() throws Exception {
        String platformId="12345";
        UserRole role = UserRole.UNAUTH;
        UserPlatformType userPlatformType=UserPlatformType.KAKAO;
        String name = "구영민";
        String profileImageURL = "google.co.kr";
        String githubId="1234@github.com";

        // given
        User user = User.builder()
                .platformId(platformId)
                .platformType(UserPlatformType.KAKAO)
                .name(name)
                .profileImageUrl(profileImageURL)
                .role(role)
                .build();
        User savedUser = userRepository.saveAndFlush(user);
        HashMap<String, String> map = new HashMap<>();

        map.put("role", String.valueOf(role));
        map.put("platformId", platformId);
        map.put("platformType", String.valueOf(userPlatformType));
        AuthServiceRegisterRequest request = AuthServiceRegisterRequest.builder()
                .role(UserRole.USER)
                .platformId(user.getPlatformId())
                .platformType(user.getPlatformType())
                .githubId(githubId)
                .build();

        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        when(authService.register(request)).thenReturn(AuthServiceLoginResponse.builder()
                .accessToken(accessToken )
                .refreshToken(refreshToken)
                .role(UserRole.USER)
                .build()
        );

        Mockito.doNothing().when(authService).userDelete(any(String.class));
        // when
        mockMvc.perform(post("/auth/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken,refreshToken)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200));

    }
    @Test
    @DisplayName("유저정보 조회 성공 테스트")
    void userInfoSuccessTest() throws Exception {
        //given
        User user = User.builder()
                .name(expectedName)
                .role(UserRole.USER)
                .platformId(expectedPlatformId)
                .platformType(UserPlatformType.GOOGLE)
                .githubId("j-ra1n")
                .profileImageUrl(expectedProfileImageUrl)
                .pushAlarmYn(true)
                .score(0)
                .point(0)
                .build();
        UserInfoResponse savedUser = UserInfoResponse.of(userRepository.save(user));

        when(authService.getUserByInfo(expectedPlatformId, UserPlatformType.GOOGLE)).thenReturn(savedUser);

        HashMap<String, String> map = new HashMap<>();
        map.put("role", user.getRole().name());
        map.put("platformId", user.getPlatformId());
        map.put("platformType", String.valueOf(user.getPlatformType()));

        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);

        // when
        mockMvc.perform(get("/auth/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_obj.role").value(String.valueOf(UserRole.USER)))
                .andExpect(jsonPath("$.res_obj.name").value(expectedName))
                .andExpect(jsonPath("$.res_obj.profile_image_url").value(expectedProfileImageUrl))
                .andExpect(jsonPath("$.res_obj.github_id").value("j-ra1n"))
                .andExpect(jsonPath("$.res_obj.push_alarm_yn").value(true))
                .andExpect(jsonPath("$.res_obj.score").value(0))
                .andExpect(jsonPath("$.res_obj.point").value(0));

    }


    @Test
    @DisplayName("유저정보 조회 실패 테스트 - 잘못된 Token")
    void userInfoWhenInvalidToken() throws Exception {
        // given
        String accessToken = "strangeToken";
        String refreshToken = "strangeToken";

        // when
        mockMvc.perform(get("/auth/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400))
                .andExpect(jsonPath("$.res_msg").value(ExceptionMessage.JWT_MALFORMED.getText()));
    }

    @Test
    @DisplayName("유저정보 조회 실패 테스트 - 잘못된 권한")
    void userInfoWhenInvalidAuthority() throws Exception {
        User user = User.builder()
                .name(expectedName)
                .role(UserRole.UNAUTH)       // 잘못된 권한(미인증)
                .platformId(expectedPlatformId)
                .platformType(UserPlatformType.GOOGLE)
                .githubId("j-ra1n")
                .profileImageUrl(expectedProfileImageUrl)
                .pushAlarmYn(true)
                .score(0)
                .point(0)
                .build();
        User savedUser = userRepository.save(user);

        HashMap<String, String> map = new HashMap<>();
        map.put("role", savedUser.getRole().name());
        map.put("platformId", savedUser.getPlatformId());
        map.put("platformType", String.valueOf(savedUser.getPlatformType()));

        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);

        // when
        mockMvc.perform(get("/auth/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400));

    }

}