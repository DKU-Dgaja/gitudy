package com.example.backend.study.api.controller.info;

import com.example.backend.MockTestConfig;
import com.example.backend.TestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.category.info.repository.StudyCategoryRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.request.StudyInfoUpdateRequest;
import com.example.backend.study.api.controller.info.response.StudyInfoCountResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoRegisterResponse;
import com.example.backend.study.api.controller.info.response.UpdateStudyInfoPageResponse;
import com.example.backend.study.api.service.info.StudyInfoService;
import com.example.backend.study.api.service.member.StudyMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static com.example.backend.domain.define.study.StudyCategory.info.StudyCategoryFixture.CATEGORY_SIZE;
import static com.example.backend.domain.define.study.StudyCategory.info.StudyCategoryFixture.createDefaultPublicStudyCategories;
import static com.example.backend.domain.define.study.info.StudyInfoFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
class StudyInfoControllerTest extends MockTestConfig {

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
    @Autowired
    private StudyCategoryRepository studyCategoryRepository;
    @MockBean
    private StudyMemberService studyMemberService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyCategoryRepository.deleteAllInBatch();
    }

    @Test
    void 스터디_등록_테스트() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        // given
        User savedUser = userRepository.save(generateAuthUser());

        List<StudyCategory> studyCategories = createDefaultPublicStudyCategories(CATEGORY_SIZE);
        StudyInfoRegisterRequest request = generateStudyInfoRegisterRequest(studyCategories);

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        // when
        when(studyInfoService.registerStudy(any(StudyInfoRegisterRequest.class), any(UserInfoResponse.class)))
                .thenReturn(Mockito.mock(StudyInfoRegisterResponse.class));
        // then
        mockMvc.perform(post("/study/")
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

        List<StudyCategory> studyCategories = createDefaultPublicStudyCategories(CATEGORY_SIZE);

        // MaximumMember가 11일 때
        StudyInfoRegisterRequest request = generateStudyInfoRegisterRequestWhenMaximumMemberExceed10(studyCategories);

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        // when
        when(studyInfoService.registerStudy(any(StudyInfoRegisterRequest.class), any(UserInfoResponse.class)))
                .thenReturn(Mockito.mock(StudyInfoRegisterResponse.class));
        // then
        mockMvc.perform(post("/study/")
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

        List<StudyCategory> studyCategories = createDefaultPublicStudyCategories(CATEGORY_SIZE);

        // MaximumMember가 -1일 때
        StudyInfoRegisterRequest request = generateStudyInfoRegisterRequestWhenMaximumMemberLessThan1(studyCategories);

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        // when
        when(studyInfoService.registerStudy(any(StudyInfoRegisterRequest.class), any(UserInfoResponse.class)))
                .thenReturn(Mockito.mock(StudyInfoRegisterResponse.class));
        // then
        mockMvc.perform(post("/study/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400))
                .andExpect(jsonPath("$.res_msg").value("maximumMember: must be greater than or equal to 1"))
                .andDo(print());
    }

    @Test
    void 스터디_삭제_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(generateStudyInfo(savedUser.getId()));
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        // when
        when(authService.findUserInfo(any())).thenReturn(UserInfoResponse.of(savedUser));
        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));

        when(studyInfoService.deleteStudy(anyLong())).thenReturn(true);

        // then
        mockMvc.perform(delete("/study/" + studyInfo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"));
    }


    @Test
    public void 스터디_정보_수정_테스트() throws Exception {

        //given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        List<StudyCategory> studyCategories = createDefaultPublicStudyCategories(CATEGORY_SIZE);

        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));

        StudyInfoUpdateRequest studyInfoUpdateRequest = generateUpdatedStudyInfoUpdateRequestWithCategory(studyCategories);


        //when
        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(studyInfoService).updateStudyInfo(studyInfoUpdateRequest, studyInfo.getId());

        //then
        mockMvc.perform(patch("/study/" + studyInfo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .content(objectMapper.writeValueAsString(studyInfoUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").value("StudyInfo update Success"))
                .andDo(print());

    }

    @Test
    public void 스터디_정보_수정_페이지_요청테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        List<StudyCategory> studyCategories = createDefaultPublicStudyCategories(CATEGORY_SIZE);

        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));

        UpdateStudyInfoPageResponse updateStudyInfoPageResponse = generateUpdateStudyInfoPageResponseWithCategory(studyInfo.getUserId(), studyCategories);
        //when
        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        when(studyInfoService.updateStudyInfoPage(any(Long.class))).thenReturn(updateStudyInfoPageResponse);

        //then
        mockMvc.perform(get("/study/" + studyInfo.getId() + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andDo(print());

    }

    @Test
    void 마이_스터디_조회_성공_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);

        when(authService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(user));
        when(authService.authenticate(any(Long.class), any(User.class))).thenReturn(UserInfoResponse.builder().build());
        when(studyInfoService.selectStudyInfoList(any(Long.class), any(Long.class), any(Long.class), any(String.class), any(Boolean.class)))
                .thenReturn(generateMyStudyInfoListAndCursorIdxResponse());

        // when
        mockMvc.perform(get("/study/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .param("limit", "10")
                        .param("cursorIdx", "1")
                        .param("sortBy", "score")
                        .param("myStudy", "true")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andDo(print());
    }

    @Test
    void 마이_스터디_조회_성공_테스트_cursorIdx가_null일_때() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);

        when(authService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(user));
        when(authService.authenticate(any(Long.class), any(User.class))).thenReturn(UserInfoResponse.builder().build());
        when(studyInfoService.selectStudyInfoList(any(Long.class), any(Long.class), any(Long.class), any(String.class), any(Boolean.class)))
                .thenReturn(generateMyStudyInfoListAndCursorIdxResponse());

        // when
        mockMvc.perform(get("/study/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .param("limit", "10")
                        .param("cursorIdx", "")
                        .param("sortBy", "score")
                        .param("myStudy", "true")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andDo(print());
    }
    @Test
    void 마이_스터디_조회_유효성_검증_실패_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);

        when(authService.authenticate(any(Long.class), any(User.class))).thenReturn(UserInfoResponse.builder().build());
        when(studyInfoService.selectStudyInfoList(any(Long.class), any(Long.class), any(Long.class), any(String.class), any(Boolean.class)))
                .thenReturn(generateMyStudyInfoListAndCursorIdxResponse());

        // when
        mockMvc.perform(get("/study/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .param("limit", "10")
                        .param("cursorIdx", "-1")
                        .param("sortBy", "score")
                        .param("myStudy", "true")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400))
                .andExpect(jsonPath("$.res_msg").value("400 BAD_REQUEST \"Validation failure\""))
                .andDo(print());
    }
    @Test
    void 전체_스터디_조회_성공_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);

        when(authService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(user));
        when(studyInfoService.selectStudyInfoList(any(Long.class), any(Long.class), any(Long.class), any(String.class), any(Boolean.class)))
                .thenReturn(generateMyStudyInfoListAndCursorIdxResponse());

        // when
        mockMvc.perform(get("/study/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .param("limit", "10")
                        .param("cursorIdx", "1")
                        .param("sortBy", "score")
                        .param("myStudy", "false")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andDo(print());
    }
    @Test
    void 전체_스터디_조회_유효성_검증_실패_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);

        when(authService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(user));
        when(studyInfoService.selectStudyInfoList(any(Long.class), any(Long.class), any(Long.class), any(String.class), any(Boolean.class)))
                .thenReturn(generateMyStudyInfoListAndCursorIdxResponse());

        // when
        mockMvc.perform(get("/study/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .param("limit", "10")
                        .param("cursorIdx", "-1")
                        .param("sortBy", "score")
                        .param("myStudy", "false")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400))
                .andExpect(jsonPath("$.res_msg").value("400 BAD_REQUEST \"Validation failure\""))
                .andDo(print());
    }
    @Test
    void 스터디_상세정보_조회_성공_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(generateStudyInfo(user.getId()));
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);

        when(authService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(user));
        when(studyInfoService.selectStudyInfoDetail(any(Long.class)))
                .thenReturn(generateStudyInfoDetailResponse(studyInfo));

        // when
        mockMvc.perform(get("/study/" + studyInfo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .param("limit", "10")
                        .param("cursorIdx", "1")
                        .param("sortBy", "score")
                        .param("myStudy", "false")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andDo(print());
    }

    @Test
    void 마이스터디_개수_조회_성공_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);

        when(authService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(user));
        when(studyInfoService.getStudyInfoCount(any(Long.class), any(Boolean.class)))
                .thenReturn(StudyInfoCountResponse.builder()
                        .count(1)
                        .build());

        // when
        mockMvc.perform(get("/study/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .param("myStudy", "true")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andDo(print());
    }

    @Test
    void 전체스터디_개수_조회_성공_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(generateStudyInfo(user.getId()));
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);

        when(authService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(user));
        when(studyInfoService.getStudyInfoCount(any(Long.class), any(Boolean.class)))
                .thenReturn(StudyInfoCountResponse.builder()
                        .count(1)
                        .build());

        // when
        mockMvc.perform(get("/study/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .param("myStudy", "false")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andDo(print());
    }
}