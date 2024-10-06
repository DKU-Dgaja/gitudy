package com.example.backend.study.api.controller.commit;

import com.example.backend.MockTestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.auth.AuthException;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.common.exception.member.MemberException;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.commit.request.CommitRejectionRequest;
import com.example.backend.study.api.service.commit.StudyCommitService;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.example.backend.study.api.service.member.StudyMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
class StudyCommitControllerTest extends MockTestConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService mockAuthService;

    @Autowired
    private StudyCommitService mockStudyCommitService;

    @Autowired
    private StudyMemberService mockStudyMemberService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 마이_커밋_조회_성공_테스트() throws Exception {
        // given
        User user = generateAuthUser();

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.builder().build());
        when(mockStudyCommitService.selectUserCommitList(any(Long.class), any(Long.class), any(Long.class), any(Long.class)))
                .thenReturn(new ArrayList<>());

        // when
        mockMvc.perform(get("/commits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("cursorIdx", "1")
                        .param("limit", "20"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void cursorIdx가_null일_때_마이_커밋_조회_성공_테스트() throws Exception {
        // given
        User user = generateAuthUser();

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.builder().build());
        when(mockStudyCommitService.selectUserCommitList(any(Long.class), any(Long.class), any(Long.class), any(Long.class)))
                .thenReturn(new ArrayList<>());

        // when
        mockMvc.perform(get("/commits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("limit", "20"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());

    }

    @Test
    void 마이_커밋_조회_실패_테스트() throws Exception {
        // given
        User user = generateAuthUser();

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockAuthService.findUserInfo(any(User.class)))
                .thenThrow(new AuthException(ExceptionMessage.UNAUTHORIZED_AUTHORITY));

        // when
        mockMvc.perform(get("/commits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("cursorIdx", "1")
                        .param("limit", "20"))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.UNAUTHORIZED_AUTHORITY.getText()));

    }

    @Test
    void 마이_커밋_조회_유효성_검증_실패_테스트() throws Exception {
        // given
        User user = generateAuthUser();

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockAuthService.findUserInfo(any(User.class)))
                .thenThrow(new AuthException(ExceptionMessage.UNAUTHORIZED_AUTHORITY));

        // when
        mockMvc.perform(get("/commits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("cursorIdx", "-1")
                        .param("limit", "0"))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("400 BAD_REQUEST \"Validation failure\""));
    }

    @Test
    void 커밋_상세_조회_성공() throws Exception {
        // given
        User user = generateAuthUser();
        Long commitId = 1L;
        String commitSha = "123";

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockStudyMemberService.isValidateStudyMember(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(user));
        when(mockStudyCommitService.getCommitDetailsById(any(Long.class))).thenReturn(CommitInfoResponse.builder().commitSHA(commitSha).build());

        // when
        mockMvc.perform(get("/commits/" + commitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("studyInfoId", "1")
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commit_sha").value(commitSha));
    }

    @Test
    void 커밋_상세_조회_실패() throws Exception {
        // given
        User user = generateAuthUser();
        Long commitId = 1L;

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockStudyMemberService.isValidateStudyMember(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(user));
        when(mockStudyCommitService.getCommitDetailsById(any(Long.class))).thenThrow(new CommitException(ExceptionMessage.COMMIT_NOT_FOUND));

        // when
        mockMvc.perform(get("/commits/" + commitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("studyInfoId", "1")
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.COMMIT_NOT_FOUND.getText()));
    }

    @Test
    void 커밋_승인_성공() throws Exception {
        // given
        User user = generateAuthUser();
        Long commitId = 1L;

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockStudyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(user));
        doNothing().when(mockStudyCommitService).approveCommit(anyLong());

        // when
        mockMvc.perform(get("/commits/" + commitId + "/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("studyInfoId", "1")
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isOk());

    }

    @Test
    void 커밋_거절_성공() throws Exception {
        // given
        User user = generateAuthUser();
        Long commitId = 1L;

        var request = CommitRejectionRequest.builder()
                .rejectionReason("작동하는 코드가 아닙니다.")
                .build();

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockStudyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(user));
        doNothing().when(mockStudyCommitService).rejectCommit(anyLong(), anyString());

        // when
        mockMvc.perform(post("/commits/" + commitId + "/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("studyInfoId", "1")
                        .content(objectMapper.writeValueAsString(request))
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isOk());
    }

    @Test
    void 커밋_거절_실패() throws Exception {
        // given
        User user = generateAuthUser();
        Long commitId = 1L;

        var request = CommitRejectionRequest.builder()
                .rejectionReason("    ")
                .build();

        String errorMsg = "rejectionReason: 거절 이유는 공백일 수 없습니다.";

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        // when
        mockMvc.perform(post("/commits/" + commitId + "/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("studyInfoId", "1")
                        .content(objectMapper.writeValueAsString(request))
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMsg));
    }

    @Test
    void 대기중인_커밋_리스트_조회_성공() throws Exception {
        // given
        User user = generateAuthUser();
        String commitSha = "abc";
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockStudyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(user));
        when(mockStudyCommitService.selectWaitingCommit(any(Long.class))).thenReturn(List.of(CommitInfoResponse.builder().commitSHA(commitSha).build()));

        // when
        mockMvc.perform(get("/commits/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("studyInfoId", "1")
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commit_sha").value(commitSha));

    }

    @Test
    void 대기중인_커밋_리스트_조회_실패() throws Exception {
        // given
        User user = generateAuthUser();
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockStudyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenThrow(new MemberException(ExceptionMessage.STUDY_MEMBER_NOT_LEADER));

        // when
        mockMvc.perform(get("/commits/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("studyInfoId", "1")
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.STUDY_MEMBER_NOT_LEADER.getText()));

    }
}