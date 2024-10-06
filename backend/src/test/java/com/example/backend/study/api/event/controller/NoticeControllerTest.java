package com.example.backend.study.api.event.controller;


import com.example.backend.MockTestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.study.api.event.controller.response.UserNoticeList;
import com.example.backend.study.api.event.service.NoticeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
class NoticeControllerTest extends MockTestConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthService mockAuthService;

    @Autowired
    private NoticeService mockNoticeService;


    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @Test
    void notice_알림_목록_조회_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(savedUser));
        when(mockNoticeService.ReadNoticeList(any(UserInfoResponse.class), any(LocalDateTime.class), anyLong())).thenReturn(List.of(UserNoticeList.builder().build()));

        // when
        mockMvc.perform(get("/notice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("cursorTime", "2020-01-01T00:00:00")
                        .param("limit", "5"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());

    }

    @Test
    void notice_특정알림_삭제_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);


        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(savedUser));

        // when
        mockMvc.perform(delete("/notice/" + "stringId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isOk());

    }

    @Test
    void notice_알림전체_삭제_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(savedUser));

        // when
        mockMvc.perform(delete("/notice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isOk());
    }
}
