package com.example.backend.study.api.controller.convention;

import com.example.backend.MockTestConfig;
import com.example.backend.TestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.convention.StudyConvention;
import com.example.backend.domain.define.study.convention.StudyConventionFixture;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.controller.convention.request.StudyConventionRequest;
import com.example.backend.study.api.controller.convention.request.StudyConventionUpdateRequest;
import com.example.backend.study.api.controller.convention.response.StudyConventionListAndCursorIdxResponse;
import com.example.backend.study.api.controller.convention.response.StudyConventionResponse;
import com.example.backend.study.api.service.convention.StudyConventionService;
import com.example.backend.study.api.service.member.StudyMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
public class StudyConventionControllerTest extends MockTestConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyConventionRepository studyConventionRepository;

    @MockBean
    private StudyMemberService studyMemberService;

    @MockBean
    private StudyConventionService studyConventionService;

    @Autowired
    private ObjectMapper objectMapper;


    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyConventionRepository.deleteAllInBatch();
    }

    @Test
    public void Convetion_등록_테스트() throws Exception {
        //given

        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyConventionRequest request = StudyConventionFixture.generateStudyConventionRequest();

        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class))).thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(studyConventionService).registerStudyConvention(any(StudyConventionRequest.class), any(Long.class));

        //when , then
        mockMvc.perform(post("/study/" + studyInfo.getId() + "/convention")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").value("StudyConvention register Success"))
                .andDo(print());

    }

    @Test
    public void Convention_등록_유효성_검증_실패_테스트() throws Exception {
        //given
        String inValidName = "   ";
        String expectedError = "name: 컨벤션 이름은 공백일 수 없습니다.";

        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        //when , then
        mockMvc.perform(post("/study/" + studyInfo.getId() + "/convention")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .content(objectMapper.writeValueAsString(StudyConventionRequest.builder()
                                .name(inValidName)
                                .content("정규식")
                                .build())))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400))
                .andExpect(jsonPath("$.res_msg").value(expectedError))
                .andDo(print());
    }

    @Test
    public void Convention_수정_테스트() throws Exception {

        //given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyConvention studyConvention = StudyConventionFixture.createStudyDefaultConvention(studyInfo.getId());
        studyConventionRepository.save(studyConvention);

        StudyConventionUpdateRequest updateRequest = StudyConventionFixture.generateStudyConventionUpdateRequest();

        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class))).thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(studyConventionService).updateStudyConvention(any(StudyConventionUpdateRequest.class), any(Long.class));


        //when, then
        mockMvc.perform(put("/study/" + studyInfo.getId() + "/convention/" + studyConvention.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .content(objectMapper.writeValueAsString(updateRequest)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").value("StudyConvention update Success"))
                .andDo(print());

    }

    @Test
    public void Convention_수정_유효성_검즘_실패_테스트() throws Exception {

        //given
        String inValidContent = "   ";
        String expectedError = "content: 컨벤션 내용은 공백일 수 없습니다.";

        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyConvention studyConvention = StudyConventionFixture.createStudyDefaultConvention(studyInfo.getId());
        studyConventionRepository.save(studyConvention);


        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class))).thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(studyConventionService).updateStudyConvention(any(StudyConventionUpdateRequest.class), any(Long.class));


        //when, then
        mockMvc.perform(put("/study/" + studyInfo.getId() + "/convention/" + studyConvention.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .content(objectMapper.writeValueAsString(StudyConventionUpdateRequest.builder()
                                .name("컨벤션 수정")
                                .description("설명 수정")
                                .content(inValidContent)
                                .build())))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400))
                .andExpect(jsonPath("$.res_msg").value(expectedError))
                .andDo(print());
    }

    @Test
    public void Convention_삭제_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyConvention studyConvention = StudyConventionFixture.createStudyDefaultConvention(studyInfo.getId());
        studyConventionRepository.save(studyConvention);

        //when
        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(studyConventionService).deleteStudyConvention(any(Long.class));


        //then
        mockMvc.perform(delete("/study/" + studyInfo.getId() + "/convention/" + studyConvention.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").value("StudyConvention delete Success"))
                .andDo(print());
    }

    @Test
    public void Convention_단일_조회_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyConvention studyConvention = StudyConventionFixture.createStudyDefaultConvention(studyInfo.getId());
        studyConventionRepository.save(studyConvention);

        StudyConventionResponse response = StudyConventionResponse.of(studyConvention);

        when(studyMemberService.isValidateStudyMember(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        when(studyConventionService.readStudyConvention(any(Long.class))).thenReturn(response);

        // when, then
        mockMvc.perform(get("/study/" + studyInfo.getId() + "/convention/" + studyConvention.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj.name").value(response.getName()))
                .andExpect(jsonPath("$.res_obj").isNotEmpty())
                .andDo(print());
    }

    @Test
    public void Convention_전체_조회_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyConvention studyConvention = StudyConventionFixture.createStudyDefaultConvention(studyInfo.getId());
        studyConventionRepository.save(studyConvention);

        StudyConventionListAndCursorIdxResponse response = StudyConventionListAndCursorIdxResponse.builder()
                .studyConventionList(new ArrayList<>())  // 비어 있는 convention 리스트
                .build();
        response.setNextCursorIdx();

        when(studyMemberService.isValidateStudyMember(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        when(studyConventionService.readStudyConventionList(any(Long.class), any(Long.class), any(Long.class))).thenReturn(response);

        // when, then
        mockMvc.perform(get("/study/" + studyInfo.getId() + "/convention")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("cursorIdx","1")
                        .param("limit","1")
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").isNotEmpty())
                .andDo(print());
    }

    @Test
    public void cursorIdx가_null일_때_Convention_전체_조회_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyConvention studyConvention = StudyConventionFixture.createStudyDefaultConvention(studyInfo.getId());
        studyConventionRepository.save(studyConvention);

        StudyConventionListAndCursorIdxResponse response = StudyConventionListAndCursorIdxResponse.builder()
                .studyConventionList(new ArrayList<>())  // 비어 있는 convention 리스트
                .build();
        response.setNextCursorIdx();

        when(studyMemberService.isValidateStudyMember(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        when(studyConventionService.readStudyConventionList(any(Long.class), any(Long.class), any(Long.class))).thenReturn(response);

        // when, then
        mockMvc.perform(get("/study/" + studyInfo.getId() + "/convention")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("cursorIdx","")
                        .param("limit","1")
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").isEmpty())
                .andDo(print());
    }
}
