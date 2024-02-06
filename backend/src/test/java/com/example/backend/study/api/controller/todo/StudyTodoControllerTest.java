package com.example.backend.study.api.controller.todo;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.utils.TokenUtil;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.service.todo.StudyTodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static com.example.backend.study.api.service.todo.StudyTodoServiceTest.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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


    @Test
    public void Todo_등록_테스트() throws Exception {
        //given
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        StudyTodoRequest studyTodoRequest = new StudyTodoRequest();
        studyTodoRequest.setStudyInfoId(expectedStudyInfoId);
        studyTodoRequest.setTodoId(expectedTodoId);
        studyTodoRequest.setUserId(expectedUserId);
        studyTodoRequest.setEndTime(expectedEndTime);
        studyTodoRequest.setTitle(expectedTitle);
        studyTodoRequest.setDetail(expectedDetail);
        studyTodoRequest.setTodoLink(expectedTodoLink);
        studyTodoRequest.setStatus(expectedStatus);

        User savedUser = userRepository.save(generateAuthUser());
        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
        String accessToken = jwtService.generateAccessToken(map, savedUser);
        String refreshToken = jwtService.generateRefreshToken(map, savedUser);


        //when
        mockMvc.perform(post("/auth/studytodo/register")
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
    public void 특정_스터디아이디_조회_테스트() throws Exception{

//        //given
//        StudyTodo studyTodo = StudyTodo.builder()
//                .studyInfoId(expectedStudyInfoId)
//                .title(expectedTitle)
//                .detail(expectedDetail)
//                .todoLink(expectedTodoLink)
//                .endTime(expectedEndTime)
//                .build();
//        studyTodoRepository.save(studyTodo);
//
//        List<StudyTodo> todos = new ArrayList<>();
//        todos.add(studyTodo);
//
//        User savedUser = userRepository.save(generateAuthUser());
//        Map<String, String> map = TokenUtil.createTokenMap(savedUser);
//        String accessToken = jwtService.generateAccessToken(map, savedUser);
//        String refreshToken = jwtService.generateRefreshToken(map, savedUser);
//
//
//        //when
//        when(studyTodoService.readStudyTodo(expectedStudyInfoId)).thenReturn(todos);
//
//        mockMvc.perform(get("/auth/studytodo/" + expectedStudyInfoId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(AUTHORIZATION, createAuthorizationHeader(accessToken, refreshToken)))
//                // then
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.res_code").value(200));


    }




}
