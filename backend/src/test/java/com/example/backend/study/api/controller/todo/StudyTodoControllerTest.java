package com.example.backend.study.api.controller.todo;

import com.example.backend.auth.TestConfig;
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
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.StudyTodoPageResponse;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static com.example.backend.study.api.service.todo.StudyTodoServiceTest.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class StudyTodoControllerTest extends TestConfig {

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
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyMemberFixture.createStudyMemberLeader(savedUser.getId(), studyInfo.getId());

        StudyTodoRequest studyTodoRequest = StudyTodoFixture.generateStudyTodoRequest();

        doNothing().when(studyMemberService).isValidateStudyLeader(any(User.class), any(Long.class));
        doNothing().when(studyTodoService).registerStudyTodo(any(StudyTodoRequest.class), any(Long.class));

        //when , then
        mockMvc.perform(post("/study/" + studyInfo.getId() + "/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .content(objectMapper.writeValueAsString(studyTodoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").value("Todo register Success"))
                .andDo(print());

    }

    @Test
    public void Todo_수정_테스트() throws Exception {

        //given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

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
        doNothing().when(studyMemberService).isValidateStudyLeader(any(User.class), any(Long.class));
        doNothing().when(studyTodoService).updateStudyTodo(any(StudyTodoUpdateRequest.class), any(Long.class));

        //then
        mockMvc.perform(put("/study/" + studyInfo.getId() + "/todo/" + studyTodo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").value("Todo update Success"))
                .andDo(print());

    }

    @Test
    public void Todo_삭제_테스트() throws Exception {
        //given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodo studyTodo = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.save(studyTodo);


        //when
        doNothing().when(studyMemberService).isValidateStudyLeader(any(User.class), any(Long.class));
        doNothing().when(studyTodoService).deleteStudyTodo(any(Long.class), any(Long.class));


        //then
        mockMvc.perform(delete("/study/" + studyInfo.getId() + "/todo/" + studyTodo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").value("Todo delete Success"))
                .andDo(print());
    }

    @Test
    public void Todo_전체조회_테스트() throws Exception {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        // StudyTodoResponse 10개 생성
        List<StudyTodoResponse> studyTodoResponses = StudyTodoFixture.createStudyTodoResponses(studyInfo.getId(), 10);
        Long nextCursorIdx = studyTodoResponses.get(studyTodoResponses.size() - 1).getId();


        StudyTodoPageResponse pageResponse = StudyTodoFixture.createStudyTodoPageResponse(studyTodoResponses, nextCursorIdx);

        doNothing().when(studyMemberService).isValidateStudyLeader(any(User.class), any(Long.class));
        when(studyTodoService.readStudyTodoList(any(Long.class), isNull(), any(Long.class))).thenReturn(pageResponse);

        // when
        mockMvc.perform(get("/study/" + studyInfo.getId() + "/todo")
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .param("limit", String.valueOf(Limit)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}