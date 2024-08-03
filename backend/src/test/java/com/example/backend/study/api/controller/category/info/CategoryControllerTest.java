package com.example.backend.study.api.controller.category.info;

import com.example.backend.MockTestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.auth.AuthException;
import com.example.backend.common.exception.category.CategoryException;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.StudyCategory.info.StudyCategoryFixture;
import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.category.info.repository.StudyCategoryRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.controller.category.info.request.CategoryRegisterRequest;
import com.example.backend.study.api.controller.category.info.request.CategoryUpdateRequest;
import com.example.backend.study.api.controller.category.info.response.CategoryListAndCursorIdxResponse;
import com.example.backend.study.api.service.category.info.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("NonAsciiCharacters")
class CategoryControllerTest extends MockTestConfig {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private CategoryService categoryService;
    @MockBean
    private AuthService authService;

    @Autowired
    private StudyCategoryRepository studyCategoryRepository;
    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @AfterEach
    void tearDown() {
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        studyCategoryRepository.deleteAllInBatch();
    }

    @Test
    void 카테고리_등록_테스트() throws Exception {
        objectMapper.registerModule(new JavaTimeModule());
        // given
        User user = userRepository.save(generateAuthUser());

        CategoryRegisterRequest request = CategoryRegisterRequest.builder()
                .name("name")
                .build();

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        // when
        when(authService.findUserInfo(any())).thenReturn(UserInfoResponse.of(user));
        doNothing().when(categoryService).registerCategory(request);

        // then
        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 카테고리_등록_공백_유효성_검증_실패_테스트() throws Exception {
        //given
        String inValidContent = "        ";
        String expectedError = "name: 카테고리 내용은 공백일 수 없습니다.";

        User user = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        CategoryRegisterRequest request = CategoryRegisterRequest.builder()
                .name(inValidContent)
                .build();

        // when
        when(authService.findUserInfo(any())).thenReturn(UserInfoResponse.of(user));
        doNothing().when(categoryService).registerCategory(request);

        //when, then
        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedError))
                .andDo(print());
    }

    @Test
    void 카테고리_등록_글자수_초과_유효성_검증_실패_테스트() throws Exception {
        //given
        String inValidContent = "1234567891011";
        String expectedError = "name: 카테고리 내용은 10자를 넘을 수 없습니다.";

        User user = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        CategoryRegisterRequest request = CategoryRegisterRequest.builder()
                .name(inValidContent)
                .build();

        // when
        when(authService.findUserInfo(any())).thenReturn(UserInfoResponse.of(user));
        doNothing().when(categoryService).registerCategory(request);

        //when, then
        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedError))
                .andDo(print());
    }

    @Test
    public void 카테고리_수정_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        StudyCategory studyCategory
                = studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("name"));
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name("updateName")
                .build();

        //when
        when(authService.authenticate(any(Long.class), any(User.class))).thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(categoryService).updateCategory(any(CategoryUpdateRequest.class), any(Long.class));

        //then
        mockMvc.perform(patch("/category/" + studyCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 카테고리_수정_글자수_초과_유효성_검증_실패_테스트() throws Exception {
        //given
        String inValidContent = "1234567891011";
        String expectedError = "name: 카테고리 내용은 10자를 넘을 수 없습니다.";

        User user = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        StudyCategory studyCategory
                = studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("name"));

        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name(inValidContent)
                .build();

        // when
        when(authService.findUserInfo(any())).thenReturn(UserInfoResponse.of(user));
        doNothing().when(categoryService).updateCategory(request, studyCategory.getId());

        //when, then
        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedError))
                .andDo(print());
    }

    @Test
    void 카테고리_수정_공백_유효성_검증_실패_테스트() throws Exception {
        //given
        String inValidContent = "        ";
        String expectedError = "name: 카테고리 내용은 공백일 수 없습니다.";

        User user = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        StudyCategory studyCategory
                = studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("name"));

        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name(inValidContent)
                .build();

        // when
        when(authService.findUserInfo(any())).thenReturn(UserInfoResponse.of(user));
        doNothing().when(categoryService).updateCategory(request, studyCategory.getId());

        //when, then
        mockMvc.perform(patch("/category/" + studyCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedError))
                .andDo(print());
    }

    @Test
    public void 카테고리_수정_권한_실패_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        StudyCategory studyCategory
                = studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("name"));
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name("updateName")
                .build();

        //when
        doThrow(new CategoryException(ExceptionMessage.CATEGORY_NOT_FOUND))
                .when(categoryService)
                .updateCategory(any(), any());

        //then
        mockMvc.perform(patch("/category/" + studyCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.CATEGORY_NOT_FOUND.getText()))
                .andDo(print());
    }

    @Test
    public void 카테고리_삭제_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        StudyCategory studyCategory
                = studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("name"));

        //when
        when(authService.findUserInfo(any(User.class))).thenReturn(UserInfoResponse.builder().build());
        doNothing().when(categoryService).deleteCategory(any(Long.class));

        //then
        mockMvc.perform(delete("/category/" + studyCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void 카테고리_삭제_권한_실패_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);


        StudyCategory studyCategory
                = studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("name"));

        //when
        doThrow(new AuthException(ExceptionMessage.UNAUTHORIZED_AUTHORITY))
                .when(authService)
                .findUserInfo(any(User.class));

        //then
        mockMvc.perform(delete("/category/" + studyCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.UNAUTHORIZED_AUTHORITY.getText()))
                .andDo(print());
    }

    @Test
    void 카테고리_조회_성공_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);

        CategoryListAndCursorIdxResponse response
                = StudyCategoryFixture.generateCategoryListAndCursorIdxResponse(3);

        when(authService.authenticate(any(Long.class), any(User.class))).thenReturn(UserInfoResponse.builder().build());
        when(categoryService.selectCategoryList(any(Long.class), any(Long.class), any(Long.class)))
                .thenReturn(response);

        // when
        mockMvc.perform(get("/category/" + studyInfo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("cursorIdx", "1")
                        .param("limit", "5"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category_response_list").isNotEmpty())
                .andDo(print());
    }

    @Test
    void 카테고리_조회_실패_테스트_권한_없음() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);


        //when
        doThrow(new AuthException(ExceptionMessage.UNAUTHORIZED_AUTHORITY))
                .when(authService)
                .findUserInfo(any(User.class));

        mockMvc.perform(get("/category/" + studyInfo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("cursorIdx", "1")
                        .param("limit", "5"))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ExceptionMessage.UNAUTHORIZED_AUTHORITY.getText()))
                .andDo(print());
    }

    @Test
    void 카테고리_조회_유효성_검증_실패_테스트() throws Exception {
        // given
        User user = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        Map<String, String> map = TokenUtil.createTokenMap(user);
        String accessToken = jwtService.generateAccessToken(map, user);


        // when
        mockMvc.perform(get("/category/" + studyInfo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("cursorIdx", "1")
                        .param("limit", "-1"))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("400 BAD_REQUEST \"Validation failure\""))
                .andDo(print());
    }
}