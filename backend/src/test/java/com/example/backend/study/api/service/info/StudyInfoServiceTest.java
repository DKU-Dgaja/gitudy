package com.example.backend.study.api.service.info;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.response.StudyInfoRegisterResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static com.example.backend.domain.define.study.info.StudyInfoFixture.generateStudyInfoRegisterRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
class StudyInfoServiceTest extends TestConfig {
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

    @Autowired
    private StudyInfoService studyInfoService;
    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("StudyInfo 등록 테스트")
    void testRegisterStudy() {
        // given
        User user = userRepository.save(generateAuthUser());
        StudyInfoRegisterRequest studyInfoRegisterRequest = generateStudyInfoRegisterRequest(user.getId());

        // when
        StudyInfoRegisterResponse registeredStudy = studyInfoService.registerStudy(studyInfoRegisterRequest);

        // then
        assertThat(studyInfoRegisterRequest.getUserId()).isEqualTo(registeredStudy.getUserId());
        assertThat(studyInfoRegisterRequest.getTopic()).isEqualTo(registeredStudy.getTopic());
        assertThat(studyInfoRegisterRequest.getEndDate()).isEqualTo(registeredStudy.getEndDate());
        assertThat(studyInfoRegisterRequest.getInfo()).isEqualTo(registeredStudy.getInfo());
        assertThat(studyInfoRegisterRequest.getStatus()).isEqualTo(registeredStudy.getStatus());
        assertThat(studyInfoRegisterRequest.getMaximumMember()).isEqualTo(registeredStudy.getMaximumMember());
        assertThat(studyInfoRegisterRequest.getProfileImageUrl()).isEqualTo(registeredStudy.getProfileImageUrl());
        assertThat(studyInfoRegisterRequest.getRepositoryInfo()).usingRecursiveComparison().isEqualTo(registeredStudy.getRepositoryInfo());
        assertThat(studyInfoRegisterRequest.getPeriodType()).isEqualTo(registeredStudy.getPeriodType());
        assertThat(studyInfoRegisterRequest.getCategories().get(0).getName()).isEqualTo(registeredStudy.getCategories().get(0).getName());
        assertThat(studyInfoRegisterRequest.getCategories().get(1).getName()).isEqualTo(registeredStudy.getCategories().get(1).getName());
    }
}