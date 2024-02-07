package com.example.backend.study.api.controller.bookmark;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.auth.AuthException;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.service.bookmark.StudyBookmarkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
class StudyBookmarkControllerTest extends TestConfig {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudyBookmarkService studyBookmarkService;

    @MockBean
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Test
    void 마이_북마크_조회_성공_테스트() throws Exception {
        // given
        User user = generateAuthUser();
        Long userId = 1L;

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);

        when(authService.authenticate(any(Long.class), any(User.class))).thenReturn(UserInfoResponse.builder().build());
        when(studyBookmarkService.selectUserBookmarkList(any(Long.class), any(Long.class), any(Long.class)))
                .thenReturn(new ArrayList<>());

        // when
        mockMvc.perform(get("/bookmarks/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .param("cursorIdx", "1")
                        .param("limit", "5"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").isNotEmpty())
                .andDo(print());

    }

    @Test
    void 마이_북마크_조회_실패_테스트() throws Exception {
        // given
        User user = generateAuthUser();
        Long userId = 1L;

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);

        when(authService.authenticate(any(Long.class), any(User.class)))
                .thenThrow(new AuthException(ExceptionMessage.UNAUTHORIZED_AUTHORITY));

        // when
        mockMvc.perform(get("/bookmarks/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .param("cursorIdx", "1")
                        .param("limit", "5"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400))
                .andExpect(jsonPath("$.res_msg").value(ExceptionMessage.UNAUTHORIZED_AUTHORITY.getText()))
                .andDo(print());
    }

    @Test
    void 마이_북마크_조회_유효성_검증_실패_테스트() throws Exception {
        // given
        User user = generateAuthUser();
        Long userId = 1L;

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);
        String refreshToken = jwtService.generateRefreshToken(map, user);

        // when
        mockMvc.perform(get("/bookmarks/user/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .param("cursorIdx", "1")
                        .param("limit", "-1"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(400))
                .andExpect(jsonPath("$.res_msg").value("400 BAD_REQUEST \"Validation failure\""))
                .andDo(print());
    }

}