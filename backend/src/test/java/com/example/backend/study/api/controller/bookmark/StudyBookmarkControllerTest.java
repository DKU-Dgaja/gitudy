package com.example.backend.study.api.controller.bookmark;

import com.example.backend.MockTestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.auth.AuthException;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.bookmark.StudyBookmarkFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.study.api.service.bookmark.StudyBookmarkService;
import com.example.backend.study.api.service.bookmark.response.BookmarkInfoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
class StudyBookmarkControllerTest extends MockTestConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudyBookmarkService mockStudyBookmarkService;

    @Autowired
    private AuthService mockAuthService;

    @Autowired
    private JwtService jwtService;

    @Test
    void 마이_북마크_조회_성공_테스트() throws Exception {
        // given
        User user = generateAuthUser();

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.builder().build());
        when(mockStudyBookmarkService.selectUserBookmarkList(any(Long.class), any(Long.class), any(Long.class)))
                .thenReturn(List.of(BookmarkInfoResponse.builder().build()));

        // when
        mockMvc.perform(get("/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("cursorIdx", "1")
                        .param("limit", "5"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cursor_idx").value(0));

    }

    @Test
    void cursorIdx가_null일_때_마이_북마크_조회_성공_테스트() throws Exception {
        // given
        User user = generateAuthUser();

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.of(user));
        when(mockStudyBookmarkService.selectUserBookmarkList(any(Long.class), any(Long.class), any(Long.class)))
                .thenReturn(new ArrayList<>());

        // when
        mockMvc.perform(get("/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("cursorIdx", "")
                        .param("limit", "5"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cursor_idx").value(0));

    }

    @Test
    void 마이_북마크_조회_실패_테스트() throws Exception {
        // given
        User user = generateAuthUser();

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        when(mockAuthService.findUserInfo(any(User.class)))
                .thenThrow(new AuthException(ExceptionMessage.UNAUTHORIZED_AUTHORITY));

        // when
        mockMvc.perform(get("/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("cursorIdx", "1")
                        .param("limit", "5"))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.UNAUTHORIZED_AUTHORITY.getText()));
    }

    @Test
    void 마이_북마크_조회_유효성_검증_실패_테스트() throws Exception {
        // given
        User user = generateAuthUser();

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        // when
        mockMvc.perform(get("/bookmarks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("cursorIdx", "1")
                        .param("limit", "-1"))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("400 BAD_REQUEST \"Validation failure\""));
    }

    @Test
    void 북마크_등록_삭제_요청_성공_테스트() throws Exception {
        // given
        User user = generateAuthUser();
        Long studyInfoId = 1L;

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        // when
        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.builder().build());
        doNothing().when(mockStudyBookmarkService).handleBookmark(any(Long.class), any(Long.class));

        // when
        mockMvc.perform(get("/bookmarks/study/" + studyInfoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isOk());
    }

    @Test
    void 북마크_등록_삭제_요청_실패_테스트() throws Exception {
        // given
        User user = generateAuthUser();
        Long studyInfoId = 1L;

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        // when
        when(mockAuthService.findUserInfo(any(User.class))).thenThrow(new AuthException(ExceptionMessage.AUTH_NOT_FOUND));

        // when
        mockMvc.perform(get("/bookmarks/study/" + studyInfoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("계정 정보를 찾을 수 없습니다."));
    }

    @Test
    void 북마크_인지_조회_요청_성공_테스트() throws Exception {
        // given
        User user = generateAuthUser();
        Long studyInfoId = 1L;

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        // when
        when(mockAuthService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.builder().build());
        when(mockStudyBookmarkService.getIsMyBookMark(any(Long.class), any(Long.class)))
                .thenReturn(StudyBookmarkFixture.createIsMyBookmarkResponse(user.getId(), studyInfoId));

        // when
        mockMvc.perform(get("/bookmarks/study/" + studyInfoId + "/my-bookmark")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isOk());
    }

    @Test
    void 북마크_인지_조회_요청_실패_테스트() throws Exception {
        // given
        User user = generateAuthUser();
        Long studyInfoId = 1L;

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        // when
        when(mockAuthService.findUserInfo(any(User.class))).thenThrow(new AuthException(ExceptionMessage.AUTH_NOT_FOUND));

        // when
        mockMvc.perform(get("/bookmarks/study/" + studyInfoId + "/my-bookmark")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("계정 정보를 찾을 수 없습니다."));
    }
}