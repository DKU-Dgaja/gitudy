package com.example.backend.study.api.service.todo;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus.TODO_COMPLETE;
import static com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus.TODO_INCOMPLETE;
import static org.junit.jupiter.api.Assertions.*;

public class StudyTodoServiceTest extends TestConfig {

    @Autowired
    private StudyTodoRepository studyTodoRepository;

    @Autowired
    private StudyTodoMappingRepository studyTodoMappingRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyTodoService studyTodoService;

    @Autowired
    private UserRepository userRepository;

    public final static Long expectedStudyInfoId = 1L;
    public final static String expectedTitle = "백준 1234번 풀기";
    public final static String expectedDetail = "오늘 자정까지 풀고 제출한다";
    public final static String expectedTodoLink = "https://www.acmicpc.net/";
    public final static LocalDate expectedEndTime = LocalDate.now();
    public final static Long expectedTodoId = 2L;
    public final static Long expectedUserId = 3L;
    public final static StudyTodoStatus expectedStatus = TODO_INCOMPLETE;
    public final static String updatedTitle = "프로그래머스 1234번 풀기";
    public final static String updatedDetail = "오늘 오후 3시까지 풀다";
    public final static String updatedTodoLink = "https://programmers.co.kr/";
    public final static LocalDate updatedEndTime = LocalDate.now().plusDays(1);
    public final static StudyTodoStatus updatedStatus = TODO_COMPLETE;

    @AfterEach
    void tearDown() {
        studyTodoMappingRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @BeforeEach
    void setUp() {
        studyTodoRepository.deleteAllInBatch();
        studyTodoMappingRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Todo 등록 테스트")
    public void registerTodo() {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodo studyTodo = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.save(studyTodo);

        StudyTodoMapping studyTodoMapping = StudyTodoFixture.createStudyTodoMapping(studyTodo.getId(), savedUser.getId());
        studyTodoMappingRepository.save(studyTodoMapping);

        StudyTodoRequest request = StudyTodoFixture.generateStudyTodoRequest();

        //when
        studyTodoService.registerStudyTodo(request, studyInfo.getId(), savedUser);

        //then
        // StudyTodo
        List<StudyTodo> studyTodos = studyTodoRepository.findAll();
        assertNotNull(studyTodos);
        StudyTodo savedStudyTodo = studyTodos.get(0);
        assertEquals(studyInfo.getId(), savedStudyTodo.getStudyInfoId());
        assertEquals(expectedTitle, savedStudyTodo.getTitle());
        assertEquals(expectedDetail, savedStudyTodo.getDetail());
        assertEquals(expectedTodoLink, savedStudyTodo.getTodoLink());
        assertEquals(expectedEndTime, savedStudyTodo.getEndTime());

        // StudyTodoMapping
        List<StudyTodoMapping> studyTodoMappings = studyTodoMappingRepository.findAll();
        assertNotNull(studyTodoMappings);
        StudyTodoMapping savedStudyTodoMapping = studyTodoMappings.get(0);
        assertEquals(studyTodoMapping.getTodoId(), savedStudyTodoMapping.getTodoId());
        assertEquals(studyTodoMapping.getUserId(), savedStudyTodoMapping.getUserId());
        assertEquals(expectedStatus, savedStudyTodoMapping.getStatus());
    }


    @Test
    @DisplayName("Todo 리스트 조회 테스트")
    public void readTodoAll() {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodo studyTodo1 = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        StudyTodo studyTodo2 = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        StudyTodo studyTodo3 = StudyTodoFixture.createStudyTodo(studyInfo.getId());

        studyTodoRepository.saveAll(List.of(studyTodo1, studyTodo2, studyTodo3));

        //when
        List<StudyTodo> todoAllList = studyTodoRepository.findAll();

        //then
        assertFalse(todoAllList.isEmpty());
        assertEquals(3, todoAllList.size());

    }

    @Test
    @DisplayName("Todo 스터디Id 조회 테스트")
    public void readStudyInfoIdTodo() {

        // given
        User savedUser = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodo studyTodo = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.save(studyTodo);

        // when
        List<StudyTodoResponse> studyTodoResponses = studyTodoService.readStudyTodo(studyInfo.getId());

        // then
        StudyTodoResponse response = studyTodoResponses.get(0);
        assertEquals(expectedTitle, response.getTitle());
        assertEquals(expectedDetail, response.getDetail());
        assertEquals(expectedTodoLink, response.getTodoLink());
        assertEquals(expectedEndTime, response.getEndTime());

    }


    @Test
    @DisplayName("Todo Id 조회 테스트")
    public void readTodoId() {

        // given
        User savedUser = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodo studyTodoId = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.save(studyTodoId);

        // when
        StudyTodo findTodo = studyTodoRepository.findById(studyTodoId.getId()).orElseThrow();

        // then
        assertNotNull(findTodo);
        assertEquals(expectedTitle, findTodo.getTitle());

    }

    @Test
    @DisplayName("Todo 수정 테스트")
    public void updateTodo() {

        // given
        User savedUser = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodo studyTodo = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.save(studyTodo);

        StudyTodoMapping studyTodoMapping = StudyTodoFixture.createStudyTodoMapping(studyTodo.getId(), savedUser.getId());
        studyTodoMappingRepository.save(studyTodoMapping);

        StudyTodoUpdateRequest request = StudyTodoFixture.updateStudyTodoRequest();

        // when
        studyTodoService.updateStudyTodo(studyTodo.getId(), request, savedUser);

        // then
        StudyTodo updatedTodo = studyTodoRepository.findById(studyTodo.getId()).orElseThrow();
        StudyTodoMapping updatedTodoMapping = studyTodoMappingRepository.findByTodoIdAndUserId(studyTodo.getId(), savedUser.getId()).orElseThrow();
        assertEquals(updatedTitle, updatedTodo.getTitle());
        assertEquals(updatedDetail, updatedTodo.getDetail());
        assertEquals(updatedTodoLink, updatedTodo.getTodoLink());
        assertEquals(updatedEndTime, updatedTodo.getEndTime());
        assertEquals(updatedStatus, updatedTodoMapping.getStatus());

    }


    @Test
    @DisplayName("Todo 삭제 테스트")
    void deleteTodo_Success() {
        // given
        User savedUser = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);


        StudyTodo studyTodo = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.save(studyTodo);

        StudyTodoMapping studyTodoMapping = StudyTodoFixture.createStudyTodoMapping(studyTodo.getId(), savedUser.getId());
        studyTodoMappingRepository.save(studyTodoMapping);

        // when
        studyTodoService.deleteStudyTodo(studyInfo.getId(), studyTodo.getId(), savedUser);

        // then
        assertFalse(studyTodoRepository.existsById(studyTodo.getId()));
        assertTrue(studyTodoMappingRepository.findByTodoId(studyTodo.getId()).isEmpty());
    }

}

