package com.example.backend.study.api.controller.info;

import com.example.backend.MockTestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.api.service.rank.RankingService;
import com.example.backend.auth.api.service.rank.response.StudyRankingResponse;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.category.info.repository.StudyCategoryRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.controller.info.request.RepoNameCheckRequest;
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
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
class StudyInfoControllerTest extends MockTestConfig {

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
    private StudyInfoService mockStudyInfoService;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyCategoryRepository studyCategoryRepository;

    @Autowired
    private StudyMemberService mockStudyMemberService;

    @Autowired
    private RankingService mockRankingService;

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

        // when
        when(mockStudyInfoService.registerStudy(any(StudyInfoRegisterRequest.class), any(UserInfoResponse.class)))
                .thenReturn(Mockito.mock(StudyInfoRegisterResponse.class));
        // then
        mockMvc.perform(post("/study/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

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

        // when
        when(mockStudyInfoService.registerStudy(any(StudyInfoRegisterRequest.class), any(UserInfoResponse.class)))
                .thenReturn(Mockito.mock(StudyInfoRegisterResponse.class));
        // then
        mockMvc.perform(post("/study/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("maximumMember: must be less than or equal to 10"));

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

        // when
        when(mockStudyInfoService.registerStudy(any(StudyInfoRegisterRequest.class), any(UserInfoResponse.class)))
                .thenReturn(Mockito.mock(StudyInfoRegisterResponse.class));
        // then
        mockMvc.perform(post("/study/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("maximumMember: must be greater than or equal to 1"));

    }

    @Test
    void 스터디_삭제_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(generateStudyInfo(savedUser.getId()));
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        // when
        when(mockAuthService.findUserInfo(any())).thenReturn(UserInfoResponse.of(savedUser));
        when(mockStudyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));

        when(mockStudyInfoService.deleteStudy(anyLong())).thenReturn(true);

        // then
        mockMvc.perform(delete("/study/" + studyInfo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                .andExpect(status().isOk());
    }


    @Test
    public void 스터디_정보_수정_테스트() throws Exception {

        //given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        List<StudyCategory> studyCategories = createDefaultPublicStudyCategories(CATEGORY_SIZE);

        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));

        StudyInfoUpdateRequest studyInfoUpdateRequest = generateUpdatedStudyInfoUpdateRequestWithCategory(studyCategories);


        //when
        when(mockStudyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(mockStudyInfoService).updateStudyInfo(studyInfoUpdateRequest, studyInfo.getId());

        //then
        mockMvc.perform(patch("/study/" + studyInfo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(studyInfoUpdateRequest)))
                .andExpect(status().isOk());


    }

    @Test
    public void 스터디_정보_수정_페이지_요청테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        List<StudyCategory> studyCategories = createDefaultPublicStudyCategories(CATEGORY_SIZE);

        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));

        UpdateStudyInfoPageResponse updateStudyInfoPageResponse = generateUpdateStudyInfoPageResponseWithCategory(studyInfo.getUserId(), studyCategories);
        //when
        when(mockStudyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        when(mockStudyInfoService.updateStudyInfoPage(any(Long.class))).thenReturn(updateStudyInfoPageResponse);

        //then
        mockMvc.perform(get("/study/" + studyInfo.getId() + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.user_id").value(savedUser.getId()));


    }

    @Test
    void 마이_스터디_조회_성공_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(user));
        when(mockAuthService.authenticate(any(Long.class), any(User.class))).thenReturn(UserInfoResponse.builder().build());
        when(mockStudyInfoService.selectStudyInfoList(any(Long.class), any(Long.class), any(Long.class), any(String.class), any(Boolean.class)))
                .thenReturn(generateMyStudyInfoListAndCursorIdxResponse());

        // when
        mockMvc.perform(get("/study/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("limit", "10")
                        .param("cursorIdx", "1")
                        .param("sortBy", "score")
                        .param("myStudy", "true")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());

    }

    @Test
    void 마이_스터디_조회_성공_테스트_cursorIdx가_null일_때() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(user));
        when(mockAuthService.authenticate(any(Long.class), any(User.class))).thenReturn(UserInfoResponse.builder().build());
        when(mockStudyInfoService.selectStudyInfoList(any(Long.class), any(), any(Long.class), any(String.class), any(Boolean.class)))
                .thenReturn(generateMyStudyInfoListAndCursorIdxResponse());

        // when
        mockMvc.perform(get("/study/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("limit", "10")
                        .param("cursorIdx", "")
                        .param("sortBy", "score")
                        .param("myStudy", "true")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());

    }

    @Test
    void 마이_스터디_조회_유효성_검증_실패_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockAuthService.authenticate(any(Long.class), any(User.class))).thenReturn(UserInfoResponse.builder().build());
        when(mockStudyInfoService.selectStudyInfoList(any(Long.class), any(Long.class), any(Long.class), any(String.class), any(Boolean.class)))
                .thenReturn(generateMyStudyInfoListAndCursorIdxResponse());

        // when
        mockMvc.perform(get("/study/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("limit", "10")
                        .param("cursorIdx", "-1")
                        .param("sortBy", "score")
                        .param("myStudy", "true")
                )
                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("400 BAD_REQUEST \"Validation failure\""));

    }

    @Test
    void 전체_스터디_조회_성공_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(user));
        when(mockStudyInfoService.selectStudyInfoList(any(Long.class), any(Long.class), any(Long.class), any(String.class), any(Boolean.class)))
                .thenReturn(generateMyStudyInfoListAndCursorIdxResponse());

        // when
        mockMvc.perform(get("/study/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("limit", "10")
                        .param("cursorIdx", "1")
                        .param("sortBy", "score")
                        .param("myStudy", "false")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());

    }

    @Test
    void 전체_스터디_조회_유효성_검증_실패_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(user));
        when(mockStudyInfoService.selectStudyInfoList(any(Long.class), any(Long.class), any(Long.class), any(String.class), any(Boolean.class)))
                .thenReturn(generateMyStudyInfoListAndCursorIdxResponse());

        // when
        mockMvc.perform(get("/study/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("limit", "10")
                        .param("cursorIdx", "-1")
                        .param("sortBy", "score")
                        .param("myStudy", "false")
                )
                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("400 BAD_REQUEST \"Validation failure\""));

    }

    @Test
    void 스터디_상세정보_조회_성공_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(generateStudyInfo(user.getId()));
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(user));
        when(mockStudyInfoService.selectStudyInfoDetail(any(Long.class), any(Long.class)))
                .thenReturn(generateStudyInfoDetailResponse(studyInfo));

        // when
        mockMvc.perform(get("/study/" + studyInfo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("limit", "10")
                        .param("cursorIdx", "1")
                        .param("sortBy", "score")
                        .param("myStudy", "false")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());

    }

    @Test
    void 마이스터디_개수_조회_성공_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(user));
        when(mockStudyInfoService.getStudyInfoCount(any(Long.class), any(Boolean.class)))
                .thenReturn(StudyInfoCountResponse.builder()
                        .count(1)
                        .build());

        // when
        mockMvc.perform(get("/study/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("myStudy", "true")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));

    }

    @Test
    void 전체스터디_개수_조회_성공_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(user));
        when(mockStudyInfoService.getStudyInfoCount(any(Long.class), any(Boolean.class)))
                .thenReturn(StudyInfoCountResponse.builder()
                        .count(1)
                        .build());

        // when
        mockMvc.perform(get("/study/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("myStudy", "false")
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));

    }

    @Test
    void 레포지토리_이름_검증_성공_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        String valid = "test-repo";

        RepoNameCheckRequest request = new RepoNameCheckRequest(valid);

        // when
        when(mockAuthService.findUserInfo(any())).thenReturn(UserInfoResponse.of(user));
        doNothing().when(mockStudyInfoService).checkDuplicateRepoName(any(UserInfoResponse.class), any(String.class));

        mockMvc.perform(post("/study/check-name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))

                // then
                .andExpect(status().isOk());

    }

    @Test
    void 레포지토리_이름이_공백이면_유효성_검증에_실패한다() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        String valid = "";

        RepoNameCheckRequest request = new RepoNameCheckRequest(valid);

        // when
        when(mockAuthService.findUserInfo(any())).thenReturn(UserInfoResponse.of(user));
        doNothing().when(mockStudyInfoService).checkDuplicateRepoName(any(UserInfoResponse.class), any(String.class));

        mockMvc.perform(post("/study/check-name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("name: " + ExceptionMessage.STUDY_REPOSITORY_NAME_INVALID_CHARS.getText())))
                .andExpect(jsonPath("$.message").value(containsString("name: " + ExceptionMessage.STUDY_REPOSITORY_NAME_EMPTY.getText())));

    }

    @Test
    void 레포지토리_이름에_허용되지_않은_문자를_사용하면_유효성_검증에_실패한다() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        String valid = "test!repo";

        RepoNameCheckRequest request = new RepoNameCheckRequest(valid);

        // when
        when(mockAuthService.findUserInfo(any())).thenReturn(UserInfoResponse.of(user));
        doNothing().when(mockStudyInfoService).checkDuplicateRepoName(any(UserInfoResponse.class), any(String.class));

        mockMvc.perform(post("/study/check-name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("name: " + ExceptionMessage.STUDY_REPOSITORY_NAME_INVALID_CHARS.getText())));

    }

    @Test
    void 레포지토리_이름에_특수문자를_연속으로_사용하면_유효성_검증에_실패한다() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        String valid = "test--repo";

        RepoNameCheckRequest request = new RepoNameCheckRequest(valid);

        // when
        when(mockAuthService.findUserInfo(any())).thenReturn(UserInfoResponse.of(user));
        doNothing().when(mockStudyInfoService).checkDuplicateRepoName(any(UserInfoResponse.class), any(String.class));

        mockMvc.perform(post("/study/check-name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("name: " + ExceptionMessage.STUDY_REPOSITORY_NAME_CONSECUTIVE_SPECIAL_CHARS.getText())));

    }

    @Test
    void 레포지토리_이름의_끝에_특수문자를_사용하면_유효성_검증에_실패한다() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        String valid = "test-repo.";

        RepoNameCheckRequest request = new RepoNameCheckRequest(valid);

        // when
        when(mockAuthService.findUserInfo(any())).thenReturn(UserInfoResponse.of(user));
        doNothing().when(mockStudyInfoService).checkDuplicateRepoName(any(UserInfoResponse.class), any(String.class));

        mockMvc.perform(post("/study/check-name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("name: " + ExceptionMessage.STUDY_REPOSITORY_NAME_ENDS_WITH_SPECIAL_CHAR.getText())));

    }

    @Test
    void 레포지토리_이름이_동시에_여러가지_조건을_지키지_못하면_해당하는_예외_메세지가_전부_반환된다() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        String valid = "test--repo@-";

        RepoNameCheckRequest request = new RepoNameCheckRequest(valid);

        // when
        when(mockAuthService.findUserInfo(any())).thenReturn(UserInfoResponse.of(user));
        doNothing().when(mockStudyInfoService).checkDuplicateRepoName(any(UserInfoResponse.class), any(String.class));

        mockMvc.perform(post("/study/check-name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("name: " + ExceptionMessage.STUDY_REPOSITORY_NAME_INVALID_CHARS.getText())))
                .andExpect(jsonPath("$.message").value(containsString("name: " + ExceptionMessage.STUDY_REPOSITORY_NAME_CONSECUTIVE_SPECIAL_CHARS.getText())))
                .andExpect(jsonPath("$.message").value(containsString("name: " + ExceptionMessage.STUDY_REPOSITORY_NAME_ENDS_WITH_SPECIAL_CHAR.getText())));

    }


    @Test
    void 특정_스터디_활동점수_랭킹_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = studyInfoRepository.save(createPublicStudyInfoScore(savedUser.getId(), 10));

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        when(mockRankingService.getStudyRankings(studyInfo)).thenReturn(any(StudyRankingResponse.class));

        // when
        mockMvc.perform(get("/study/rank/" + studyInfo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                .andExpect(status().isOk());

    }

    @Test
    void 스터디_종료_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(generateStudyInfo(savedUser.getId()));
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        // when
        when(mockAuthService.findUserInfo(any())).thenReturn(UserInfoResponse.of(savedUser));
        when(mockStudyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));

        when(mockStudyInfoService.closeStudy(anyLong())).thenReturn(true);

        // then
        mockMvc.perform(delete("/study/" + studyInfo.getId() + "/close")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                .andExpect(status().isOk());
    }
}