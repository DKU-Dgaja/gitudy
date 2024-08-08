package com.example.backend.study.api.controller.comment.commit;

import com.example.backend.MockTestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.auth.AuthException;
import com.example.backend.common.exception.member.MemberException;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.controller.comment.commit.request.AddCommitCommentRequest;
import com.example.backend.study.api.controller.comment.commit.response.CommitCommentInfoResponse;
import com.example.backend.study.api.service.comment.commit.CommitCommentService;
import com.example.backend.study.api.service.member.StudyMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
class CommitCommentControllerTest extends MockTestConfig {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommitCommentService commitCommentService;

    @MockBean
    private StudyMemberService studyMemberService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void 커밋_댓글_리스트_조회_성공_테스트() throws Exception {
        // given
        User user = generateAuthUser();
        Long commitId = 1L;

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(studyMemberService.isValidateStudyMember(any(User.class), any(Long.class))).thenReturn(UserInfoResponse.of(user));
        when(commitCommentService.getCommitCommentsList(any(Long.class), any(Long.class))).thenReturn(List.of(CommitCommentInfoResponse.builder().studyCommitId(commitId).build()));

        // when
        mockMvc.perform(get("/commits/" + commitId + "/comments").contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("studyInfoId", "1"))


                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()) 
                .andDo(print());

    }


    @Test
    void 커밋_댓글_리스트_조회_실패_테스트() throws Exception {
        // given
        User user = generateAuthUser();
        Long commitId = 1L;

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(studyMemberService.isValidateStudyMember(any(User.class), any(Long.class))).thenReturn(UserInfoResponse.of(user));
        when(commitCommentService.getCommitCommentsList(any(Long.class), any())).thenThrow(new AuthException(ExceptionMessage.AUTH_NOT_FOUND));

        // when
        mockMvc.perform(get("/commits/" + commitId + "/comments").contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("studyInfoId", "1"))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.AUTH_NOT_FOUND.getText()))
                .andDo(print());

    }

    @Test
    void 커밋_댓글_등록_요청_성공_테스트() throws Exception {
        // given
        User user = generateAuthUser();
        Long commitId = 1L;

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(studyMemberService.isValidateStudyMember(any(User.class), any(Long.class))).thenReturn(UserInfoResponse.of(user));
        doNothing().when(commitCommentService).addCommitComment(any(Long.class), any(Long.class), any(AddCommitCommentRequest.class));

        // when
        mockMvc.perform(post("/commits/" + commitId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(AddCommitCommentRequest.builder().content("test").build())))

                // then
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    void 커밋_댓글_등록_유효성_검증_실패_테스트() throws Exception {
        // given
        String inValidContent = "    ";
        String expectedError = "content: 댓글 내용은 공백일 수 없습니다.";

        User user = generateAuthUser();
        Long commitId = 1L;

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        // when
        mockMvc.perform(post("/commits/" + commitId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(AddCommitCommentRequest.builder().content(inValidContent).build())))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedError))
                .andDo(print());
    }

    @Test
    void 커밋_댓글_등록_요청_실패_테스트() throws Exception {
        // given
        User user = generateAuthUser();
        Long commitId = 1L;

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(studyMemberService.isValidateStudyMember(any(User.class), any(Long.class)))
                .thenThrow(new MemberException(ExceptionMessage.STUDY_NOT_MEMBER));
        // when
        mockMvc.perform(post("/commits/" + commitId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(AddCommitCommentRequest.builder().content("test").build())))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.STUDY_NOT_MEMBER.getText()))
                .andDo(print());

    }

}