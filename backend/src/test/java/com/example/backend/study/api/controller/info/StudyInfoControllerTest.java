package com.example.backend.study.api.controller.info;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.response.StudyInfoRegisterResponse;
import com.example.backend.study.api.service.info.StudyInfoService;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;

import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static com.example.backend.auth.config.fixture.UserFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
class StudyInfoControllerTest extends TestConfig {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudyInfoService studyInfoService;
    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
    }

    @Test
    void 스터디_등록_테스트() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        // given
        User savedUser = userRepository.save(generateAuthUser());
        StudyInfoRegisterRequest request = generateStudyInfoRegisterRequest(savedUser.getId());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        // when
        when(studyInfoService.registerStudy(any(StudyInfoRegisterRequest.class)))
                .thenReturn(Mockito.mock(StudyInfoRegisterResponse.class));
        // then
        mockMvc.perform(post("/studyinfo/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").value("Study Register Success."))
                .andDo(print());
    }

    @Test
    void MaximumMember이_10명_초과할_때_예외_테스트() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        // given
        User savedUser = userRepository.save(generateAuthUser());
        // MaximumMember가 11일 때
        StudyInfoRegisterRequest request = generateStudyInfoRegisterRequestWhenMaximumMemberExceed10(savedUser.getId());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        // when
        when(studyInfoService.registerStudy(any(StudyInfoRegisterRequest.class)))
                .thenReturn(Mockito.mock(StudyInfoRegisterResponse.class));
        // then
        mockMvc.perform(post("/studyinfo/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400))
                .andExpect(jsonPath("$.res_msg").value("maximumMember: must be less than or equal to 10"))
                .andDo(print());
    }

    @Test
    void MaximumMember이_1명을_넘지_않을_때_예외_테스트() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        // given
        User savedUser = userRepository.save(generateAuthUser());
        // MaximumMember가 -1일 때
        StudyInfoRegisterRequest request = generateStudyInfoRegisterRequestWhenMaximumMemberLessThan1(savedUser.getId());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        // when
        when(studyInfoService.registerStudy(any(StudyInfoRegisterRequest.class)))
                .thenReturn(Mockito.mock(StudyInfoRegisterResponse.class));
        // then
        mockMvc.perform(post("/studyinfo/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400))
                .andExpect(jsonPath("$.res_msg").value("maximumMember: must be greater than or equal to 1"))
                .andDo(print());
    }
}