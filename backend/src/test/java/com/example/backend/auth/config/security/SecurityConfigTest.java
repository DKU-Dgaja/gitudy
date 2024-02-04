package com.example.backend.auth.config.security;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.domain.define.account.user.constant.UserRole;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityConfigTest extends TestConfig {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
//        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 허용되지 않은 엔드포인트에 접근할 수 없다.")
    void unAuthUserTest() throws Exception {
        // given
        String uri = "/test";

        // when
        mockMvc.perform(
                        get(uri)
                )
                // then
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("모든 사용자는 인증 없이 \"/auth/**\" URI에 접근 가능하다.")
    void authUriPermitAllTest() throws Exception {
        // given
        String uri = "/auth/test";

        // when
        mockMvc.perform(
                        get(uri)
                )
                // then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("인증 받은 사용자(USER, ADMIN)은 모든 엔드포인트에 접근할 수 있다.")
    void authUserTest() throws Exception {
        // given
        String uri = "/test";

        // USER 권한 사용자 저장
        User user = User.builder()
                .name("김민수")
                .role(UserRole.USER)
                .platformType(UserPlatformType.GITHUB)
                .profileImageUrl("https://google.com")
                .build();
        User savedUser = userRepository.save(user);

        // JWT의 Claims로 넣어줄 map 생성
        HashMap<String, String> claimsMap = new HashMap<>();
        claimsMap.put("role", savedUser.getRole().name());
        claimsMap.put("name", savedUser.getName());
        claimsMap.put("profileImageUrl", savedUser.getProfileImageUrl());

        // JWT Access Token 생성
        String atk = jwtService.generateAccessToken(claimsMap, savedUser);

        // when
        mockMvc.perform(
                        get(uri)
                                .header("Authorization", "Bearer " + atk)
                )
                .andExpect(status().isOk());
    }
}