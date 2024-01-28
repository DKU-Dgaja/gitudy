package com.example.backend.auth.api.controller.auth;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.api.service.oauth.OAuthService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.domain.define.user.User;
import com.example.backend.domain.define.user.constant.UserPlatformType;
import com.example.backend.domain.define.user.constant.UserRole;
import com.example.backend.domain.define.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static com.example.backend.auth.api.service.oauth.adapter.google.GoogleAdapterTest.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class AuthControllerTest extends TestConfig {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuthService oAuthService;

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
                                .header("Authorization", "Bearer " + accessToken + refreshToken))


                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400));
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
        map.put("name", savedUser.getName());
        map.put("profileImageUrl", savedUser.getProfileImageUrl());

        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);


        // when
        mockMvc.perform(get("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer" + " " + accessToken + " " + refreshToken))
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
                        .header("Authorization", "INVALID HEADER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400))
                .andExpect(jsonPath("$.res_msg").value(ExceptionMessage.JWT_INVALID_HEADER.getText()));
    }
}