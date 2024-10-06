package com.example.backend.study.api.event.controller;

import com.example.backend.MockTestConfig;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.study.api.event.FcmMultiTokenRequest;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.controller.request.FcmTokenSaveRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
class FcmControllerTest extends MockTestConfig {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private FcmService mockFcmService;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @Test
    void FCM_token_저장_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());

        FcmTokenSaveRequest token = FcmTokenSaveRequest.builder()
                .token("token")
                .build();

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        // when
        doNothing().when(mockFcmService).saveFcmTokenRequest(any(User.class), any(FcmTokenSaveRequest.class));

        // then
        mockMvc.perform(post("/fcm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(token)))
                .andExpect(status().isOk());

    }

    @Test
    public void Fcm_single_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);


        doNothing().when(mockFcmService).sendMessageSingleDevice(any(FcmSingleTokenRequest.class));

        FcmSingleTokenRequest fcmSingleTokenRequest = FcmFixture.generateFcmSingleTokenRequest();

        //when , then
        mockMvc.perform(post("/fcm/single")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(fcmSingleTokenRequest)))
                .andExpect(status().isOk());

    }

    @Test
    public void Fcm_Multi_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);


        doNothing().when(mockFcmService).sendMessageMultiDevice(any(FcmMultiTokenRequest.class));

        FcmMultiTokenRequest fcmMultiTokenRequest = FcmFixture.generateFcmMultiTokenRequest();

        //when , then
        mockMvc.perform(post("/fcm/multi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(fcmMultiTokenRequest)))
                .andExpect(status().isOk());

    }

}