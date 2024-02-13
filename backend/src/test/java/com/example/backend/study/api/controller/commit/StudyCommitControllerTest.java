package com.example.backend.study.api.controller.commit;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.service.commit.StudyCommitService;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
class StudyCommitControllerTest extends TestConfig {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudyCommitService studyCommitService;

    @Autowired
    private JwtService jwtService;

    @Test
    void 커밋_상세_조회_성공() throws Exception {
        // given
        User user = generateAuthUser();
        Long commitId = 1L;
        String commitSha = "123";

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);

        when(studyCommitService.getCommitDetailsById(any(Long.class))).thenReturn(CommitInfoResponse.builder().commitSHA(commitSha).build());

        // when
        mockMvc.perform(get("/commits/" + commitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj.commit_sha").value(commitSha))
                .andDo(print());
    }

    @Test
    void 커밋_상세_조회_실패() throws Exception {
        // given
        User user = generateAuthUser();
        Long commitId = 1L;

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);

        when(studyCommitService.getCommitDetailsById(any(Long.class))).thenThrow(new CommitException(ExceptionMessage.COMMIT_NOT_FOUND));

        // when
        mockMvc.perform(get("/commits/" + commitId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400))
                .andExpect(jsonPath("$.res_msg").value(ExceptionMessage.COMMIT_NOT_FOUND.getText()))
                .andDo(print());
    }
}