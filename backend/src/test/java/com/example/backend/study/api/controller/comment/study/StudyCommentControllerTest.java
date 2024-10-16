package com.example.backend.study.api.controller.comment.study;

import com.example.backend.MockTestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.auth.AuthException;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.comment.StudyCommentFixture;
import com.example.backend.domain.define.study.comment.study.StudyComment;
import com.example.backend.domain.define.study.comment.study.repository.StudyCommentRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentRegisterRequest;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentUpdateRequest;
import com.example.backend.study.api.controller.comment.study.response.StudyCommentListAndCursorIdxResponse;
import com.example.backend.study.api.service.comment.study.StudyCommentService;
import com.example.backend.study.api.service.member.StudyMemberService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
class StudyCommentControllerTest extends MockTestConfig {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private AuthService mockAuthService;

    @Autowired
    private StudyCommentService mockStudyCommentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudyCommentRepository studyCommentRepository;

    @Autowired
    private StudyMemberService mockStudyMemberService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyCommentRepository.deleteAllInBatch();
    }

    @Test
    public void Study_Comment_등록_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId()));


        StudyMemberFixture.createStudyMemberLeader(savedUser.getId(), studyInfo.getId());

        StudyCommentRegisterRequest studyCommentRegisterRequest = StudyCommentFixture.createDefaultStudyCommentRegisterRequest();

        when(mockStudyMemberService.isValidateStudyMember(any(User.class), any(Long.class))).thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(mockStudyCommentService).registerStudyComment(any(StudyCommentRegisterRequest.class), any(Long.class), any(Long.class));

        //when , then
        mockMvc.perform(post("/study/" + studyInfo.getId() + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(studyCommentRegisterRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void Study_Comment_Update_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));
        StudyComment studyComment =
                studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(savedUser.getId(), studyInfo.getId()));
        StudyCommentUpdateRequest studyCommentUpdateRequest = StudyCommentFixture.createDefaultStudyCommentUpdateRequest();

        //when
        when(mockStudyMemberService.isValidateStudyMember(any(User.class), any(Long.class))).thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(mockStudyCommentService).registerStudyComment(any(StudyCommentRegisterRequest.class), any(Long.class), any(Long.class));

        //then
        mockMvc.perform(patch("/study/" + studyInfo.getId() + "/comment/" + studyComment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(studyCommentUpdateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void Study_Comment_삭제_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));
        StudyComment studyComment =
                studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(savedUser.getId(), studyInfo.getId()));

        //when
        when(mockStudyMemberService.isValidateStudyMember(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(mockStudyCommentService).deleteStudyComment(any(User.class), any(Long.class), any(Long.class));
        //then
        mockMvc.perform(delete("/study/" + studyInfo.getId() + "/comment/" + studyComment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))
                .andExpect(status().isOk());
    }

    @Test
    void 스터디_댓글_조회_성공_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        StudyCommentListAndCursorIdxResponse response
                = StudyCommentFixture.generateStudyCommentListAndCursorIdxResponse(user.getId(), studyInfo.getId());
        when(mockStudyMemberService.isValidateStudyMember(any(User.class), any(Long.class))).thenReturn(UserFixture.createDefaultUserInfoResponse(user.getId()));
        when(mockStudyCommentService.selectStudyCommentList(any(Long.class), any(Long.class), any(Long.class), any(Long.class)))
                .thenReturn(response);

        // when
        mockMvc.perform(get("/study/" + studyInfo.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("cursorIdx", "1")
                        .param("limit", "5"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.study_comment_list").isNotEmpty());
    }

    @Test
    void cursorIdx가_null일_때_스터디_댓글_조회_성공_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        StudyCommentListAndCursorIdxResponse response
                = StudyCommentFixture.generateStudyCommentListAndCursorIdxResponse(user.getId(), studyInfo.getId());

        when(mockStudyMemberService.isValidateStudyMember(any(User.class), any(Long.class))).thenReturn(UserFixture.createDefaultUserInfoResponse(user.getId()));
        when(mockStudyCommentService.selectStudyCommentList(any(Long.class), any(), any(Long.class), any(Long.class)))
                .thenReturn(response);

        // when
        mockMvc.perform(get("/study/" + studyInfo.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("limit", "5"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.study_comment_list").isNotEmpty());

    }

    @Test
    void 스터디_댓글_조회_실패_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        doThrow(new AuthException(ExceptionMessage.UNAUTHORIZED_AUTHORITY))
                .when(mockStudyMemberService)
                .isValidateStudyMember(any(User.class), any(Long.class));

        // when
        mockMvc.perform(get("/study/" + studyInfo.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("cursorIdx", "1")
                        .param("limit", "5"))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.UNAUTHORIZED_AUTHORITY.getText()));
    }

    @Test
    void 스터디_댓글_조회_유효성_검증_실패_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        // when
        mockMvc.perform(get("/study/" + studyInfo.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("cursorIdx", "1")
                        .param("limit", "-1"))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("400 BAD_REQUEST \"Validation failure\""));

    }
}