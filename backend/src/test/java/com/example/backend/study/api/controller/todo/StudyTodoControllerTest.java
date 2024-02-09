package com.example.backend.study.api.controller.todo;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;
import com.example.backend.study.api.service.todo.StudyTodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.*;
import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;
import static com.example.backend.domain.define.account.user.constant.UserRole.UNAUTH;
import static com.example.backend.study.api.service.todo.StudyTodoServiceTest.*;
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

    @MockBean
    private StudyTodoService studyTodoService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
        studyTodoMappingRepository.deleteAllInBatch();
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
        studyTodoMappingRepository.deleteAllInBatch();
    }

    @Test
    public void Todo_등록_테스트() throws Exception {
        //given
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        StudyTodo studyTodo = StudyTodo.builder()
                .title(expectedTitle)
                .detail(expectedDetail)
                .todoLink(expectedTodoLink)
                .endTime(expectedEndTime)
                .studyInfoId(expectedStudyInfoId)
                .build();
        studyTodoRepository.save(studyTodo);

        StudyTodoMapping studyTodoMapping = StudyTodoMapping.builder()
                .todoId(studyTodo.getId())
                .userId(expectedUserId)
                .status(expectedStatus)
                .build();
        studyTodoMappingRepository.save(studyTodoMapping);

        StudyTodoRequest studyTodoRequest = new StudyTodoRequest();
        studyTodoRequest.setTodoLink(expectedTitle);
        studyTodoRequest.setDetail(expectedDetail);
        studyTodoRequest.setTodoLink(expectedTodoLink);
        studyTodoRequest.setEndTime(expectedEndTime);

        User savedUser = UserFixture.generateAuthUser();
        userRepository.save(savedUser);
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);


        //when
        mockMvc.perform(post("/study/" + expectedStudyInfoId + "/todo/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .content(mapper.writeValueAsString(studyTodoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").value("Todo register Success"))
                .andDo(print());

    }

    @Test
    public void 특정_스터디아이디로_Todo_조회_테스트() throws Exception {

        //given
        StudyTodoResponse todo1 = StudyTodoResponse.of(StudyTodo.builder()
                .id(expectedTodoId)
                .studyInfoId(expectedStudyInfoId)
                .title(expectedTitle)
                .detail(expectedDetail)
                .todoLink(expectedTodoLink)
                .endTime(expectedEndTime)
                .build());
        StudyTodoResponse todo2 = StudyTodoResponse.of(StudyTodo.builder()
                .id(expectedUserId + 1L)
                .studyInfoId(expectedStudyInfoId + 1L)
                .title("프로그래머스 1234번 풀기")
                .detail("3시까지 제출")
                .todoLink("https://programmers.co.kr/")
                .endTime(expectedEndTime)
                .build());
        List<StudyTodoResponse> expectedTodos = List.of(todo1, todo2);


        User savedUser = UserFixture.generateAuthUser();
        userRepository.save(savedUser);
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);


        //when
        when(studyTodoService.readStudyTodo(expectedStudyInfoId)).thenReturn(expectedTodos);


        mockMvc.perform(get("/study/" + expectedStudyInfoId + "/todo/read")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_obj[0].title").value(expectedTitle))
                .andExpect(jsonPath("$.res_obj[1].title").value("프로그래머스 1234번 풀기"));

    }


    @Test
    public void Todo_수정_테스트() throws Exception {

        //given
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        StudyTodoUpdateRequest updateRequest = new StudyTodoUpdateRequest();
        updateRequest.setTitle("깃터디 화이팅");
        updateRequest.setDetail("하루 1커밋");
        updateRequest.setTodoLink("https://j-ra1n.tistory.com/");
        updateRequest.setEndTime(LocalDate.now().plusDays(1));
        updateRequest.setStatus(expectedStatus);

        User savedUser = UserFixture.generateAuthUser();
        userRepository.save(savedUser);
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        //when
        doNothing().when(studyTodoService).updateStudyTodo(expectedTodoId, updateRequest, savedUser.getId());

        //then
        mockMvc.perform(put("/study/" + expectedTodoId + "/todo/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken))
                        .content(mapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").value("Todo update Success"))
                .andDo(print());

    }

    @Test
    public void Todo_삭제_테스트() throws Exception {
        //given


        Long userId = 1L;

        User savedUser = UserFixture.generateAuthUser();
        userRepository.save(savedUser);
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        //when
        doNothing().when(studyTodoService).deleteStudyTodo(expectedTodoId, expectedStudyInfoId, userId);


        //then
        mockMvc.perform(delete("/study/" + expectedStudyInfoId + "/" + expectedTodoId + "/todo/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res_code").value(200))
                .andExpect(jsonPath("$.res_msg").value("OK"))
                .andExpect(jsonPath("$.res_obj").value("Todo delete Success"))
                .andDo(print());
    }


}
