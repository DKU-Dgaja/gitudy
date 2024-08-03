package com.example.backend.study.api.controller.todo;

import com.example.backend.MockTestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.StudyTodoListAndCursorIdxResponse;
import com.example.backend.study.api.controller.todo.response.StudyTodoProgressResponse;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;
import com.example.backend.study.api.controller.todo.response.StudyTodoStatusResponse;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.example.backend.study.api.service.member.StudyMemberService;
import com.example.backend.study.api.service.todo.StudyTodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class StudyTodoControllerTest extends MockTestConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyTodoRepository studyTodoRepository;

    @Autowired
    private StudyTodoMappingRepository studyTodoMappingRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyMemberRepository studyMemberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudyTodoService studyTodoService;

    @MockBean
    private StudyMemberService studyMemberService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
        studyTodoMappingRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
    }


    @Test
    public void Todo_등록_테스트() throws Exception {
        //given

        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyMemberFixture.createStudyMemberLeader(savedUser.getId(), studyInfo.getId());

        StudyTodoRequest studyTodoRequest = StudyTodoFixture.generateStudyTodoRequest();

        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(studyTodoService).registerStudyTodo(any(StudyTodoRequest.class), any(Long.class));

        //when , then
        mockMvc.perform(post("/study/" + studyInfo.getId() + "/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(studyTodoRequest)))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    public void Todo_수정_테스트() throws Exception {

        //given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodo studyTodo = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.save(studyTodo);

        StudyTodoUpdateRequest updateRequest = StudyTodoUpdateRequest.builder()
                .title(studyTodo.getTitle())
                .detail(studyTodo.getDetail())
                .todoLink(studyTodo.getTodoLink())
                .todoDate(studyTodo.getTodoDate())
                .build();


        //when
        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(studyTodoService).updateStudyTodo(any(StudyTodoUpdateRequest.class), any(Long.class), any(Long.class));

        //then
        mockMvc.perform(put("/study/" + studyInfo.getId() + "/todo/" + studyTodo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    public void Todo_삭제_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodo studyTodo = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.save(studyTodo);


        //when
        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        doNothing().when(studyTodoService).deleteStudyTodo(any(Long.class), any(Long.class));


        //then
        mockMvc.perform(delete("/study/" + studyInfo.getId() + "/todo/" + studyTodo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void Todo_전체조회_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodoListAndCursorIdxResponse response = StudyTodoListAndCursorIdxResponse.builder()
                .todoList(new ArrayList<>()) // 비어 있는 Todo 리스트
                .build();
        response.setNextCursorIdx();

        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        when(studyTodoService.readStudyTodoList(any(Long.class), any(Long.class), any(Long.class))).thenReturn(response);

        // when
        mockMvc.perform(get("/study/" + studyInfo.getId() + "/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("cursorIdx", "1")
                        .param("limit", "3"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(print());
    }

    @Test
    public void cursorIdx가_null일_때_Todo_전체조회_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodoListAndCursorIdxResponse response = StudyTodoListAndCursorIdxResponse.builder()
                .todoList(new ArrayList<>()) // 비어 있는 Todo 리스트
                .build();
        response.setNextCursorIdx();

        when(studyMemberService.isValidateStudyLeader(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        when(studyTodoService.readStudyTodoList(any(Long.class), any(), any(Long.class))).thenReturn(response);

        // when
        mockMvc.perform(get("/study/" + studyInfo.getId() + "/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken))
                        .param("limit", "3"))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(print());
    }

    @Test
    public void Todo_단일_조회_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodo studyTodo = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.save(studyTodo);

        StudyTodoResponse response = StudyTodoResponse.of(studyTodo);

        when(studyMemberService.isValidateStudyMember(any(User.class), any(Long.class))).thenReturn(UserInfoResponse.of(savedUser));
        when(studyTodoService.readStudyTodo(any(Long.class), any(Long.class))).thenReturn(response);

        // when
        mockMvc.perform(get("/study/" + studyInfo.getId() + "/todo/" + studyTodo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(response.getTitle()))
                .andDo(print());
    }

    @Test
    public void Todo_스터디원들의_완료여부_조회() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);


        List<StudyTodoStatusResponse> response = Arrays.asList(
                StudyTodoStatusResponse.builder()
                        .userId(1L)
                        .status(StudyTodoStatus.TODO_COMPLETE)
                        .build(),
                StudyTodoStatusResponse.builder()
                        .userId(2L)
                        .status(StudyTodoStatus.TODO_INCOMPLETE)
                        .build()
        );

        when(studyMemberService.isValidateStudyMember(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        when(studyTodoService.readStudyTodoStatus(any(Long.class), any(Long.class))).thenReturn(response);

        // when
        mockMvc.perform(get("/study/" + studyInfo.getId() + "/todo/" + 1L + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(print());
    }

    @Test
    public void 가장_마감일이_빠른_Todo의_진행률_확인_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        var response = StudyTodoProgressResponse.builder()
                .todoId(1L)
                .totalMemberCount(10)
                .completeMemberCount(5)
                .build();


        when(studyMemberService.isValidateStudyMember(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        when(studyTodoService.readStudyTodoProgress(any(Long.class))).thenReturn(response);

        // when
        mockMvc.perform(get("/study/" + 1L + "/todo/progress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andDo(print());
    }

    @Test
    void 투두별_커밋_조회_테스트() throws Exception {
        // given
        User savedUser = generateAuthUser();
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);

        List<CommitInfoResponse> list = List.of(CommitInfoResponse.builder().commitSHA("tt").build());

        when(studyMemberService.isValidateStudyMember(any(User.class), any(Long.class)))
                .thenReturn(UserInfoResponse.of(savedUser));
        when(studyTodoService.selectTodoCommits(any(Long.class)))
                .thenReturn(list);

        // when
        mockMvc.perform(get("/study/" + 1L + "/todo/" + 1L + "/commits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken)))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commit_sha").value("tt"))
                .andDo(print());
    }
}