package com.example.backend.study.api.service.todo;

import com.example.backend.auth.TestConfig;
import com.example.backend.common.exception.todo.TodoException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

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

    public final static Long expectedStudyInfoId = 1L;
    public final static String expectedTitle = "백준 1234번 풀기";
    public final static String expectedDetail = "오늘 자정까지 풀고 제출한다";
    public final static String expectedTodoLink = "https://www.acmicpc.net/";
    public final static LocalDate expectedEndTime = LocalDate.now();
    public final static Long expectedTodoId = 2L;
    public final static Long expectedUserId = 3L;
    public final static StudyTodoStatus expectedStatus = TODO_INCOMPLETE;
    public final static Long expectedLeaderId = 3L;

    @AfterEach
    void tearDown() {
        studyTodoMappingRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
    }

    @BeforeEach
    void setUp() {
        studyTodoRepository.deleteAllInBatch();
        studyTodoMappingRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Todo 등록 테스트")
    public void registerTodo() {
        //given
        StudyInfo studyInfo = StudyInfo.builder()
                .userId(expectedLeaderId)
                .topic("깃터디1")
                .build();
        studyInfoRepository.save(studyInfo);

        StudyTodo studyTodo = StudyTodo.builder()
                .studyInfoId(expectedStudyInfoId)
                .title(expectedTitle)
                .detail(expectedDetail)
                .todoLink(expectedTodoLink)
                .endTime(expectedEndTime)
                .build();
        studyTodoRepository.save(studyTodo);

        StudyTodoMapping studyTodoMappings = StudyTodoMapping.builder()
                .todoId(expectedTodoId)
                .userId(expectedUserId)
                .status(expectedStatus)
                .build();
        studyTodoMappingRepository.save(studyTodoMappings);

        //when
        studyTodoService.registerStudyTodo(studyTodo, studyTodoMappings, expectedLeaderId);

        //then
        // StudyTodo
        StudyTodo savedStudyTodo = studyTodoRepository.findById(studyTodo.getId()).orElse(null);
        assertNotNull(savedStudyTodo);
        assertEquals(expectedStudyInfoId, savedStudyTodo.getStudyInfoId());
        assertEquals(expectedTitle, savedStudyTodo.getTitle());
        assertEquals(expectedDetail, savedStudyTodo.getDetail());
        assertEquals(expectedTodoLink, savedStudyTodo.getTodoLink());
        assertEquals(expectedEndTime, savedStudyTodo.getEndTime());

        // StudyTodoMapping
        StudyTodoMapping savedStudyTodoMapping = studyTodoMappingRepository.findById(studyTodoMappings.getId()).orElse(null);
        assertNotNull(savedStudyTodoMapping);
        assertEquals(expectedTodoId, savedStudyTodoMapping.getTodoId());
        assertEquals(expectedUserId, savedStudyTodoMapping.getUserId());
        assertEquals(expectedStatus, savedStudyTodoMapping.getStatus());
    }


    @Test
    @DisplayName("Todo 리스트 조회 테스트")
    public void readTodoAll() {
        //given
        StudyTodo studyTodo1 = StudyTodo.builder()
                .studyInfoId(expectedStudyInfoId)
                .title(expectedTitle)
                .detail(expectedDetail)
                .todoLink(expectedTodoLink)
                .endTime(expectedEndTime)
                .build();
        StudyTodo studyTodo2 = StudyTodo.builder()
                .studyInfoId(expectedStudyInfoId)
                .title(expectedTitle)
                .detail(expectedDetail)
                .todoLink(expectedTodoLink)
                .endTime(expectedEndTime)
                .build();
        StudyTodo studyTodo3 = StudyTodo.builder()
                .studyInfoId(expectedStudyInfoId)
                .title(expectedTitle)
                .detail(expectedDetail)
                .todoLink(expectedTodoLink)
                .endTime(expectedEndTime)
                .build();

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
        StudyTodo todo1 = StudyTodo.builder()
                .studyInfoId(expectedStudyInfoId)
                .title(expectedTitle)
                .detail(expectedDetail)
                .todoLink(expectedTodoLink)
                .endTime(expectedEndTime)
                .build();
        StudyTodo todo2 = StudyTodo.builder()
                .studyInfoId(expectedStudyInfoId)
                .title(expectedTitle)
                .detail(expectedDetail)
                .todoLink(expectedTodoLink)
                .endTime(expectedEndTime)
                .build();
        studyTodoRepository.saveAll(List.of(todo1, todo2));

        // when
        List<StudyTodoResponse> findTodos = studyTodoService.readStudyTodo(expectedStudyInfoId);

        // then
        assertEquals(2, findTodos.size());
        assertTrue(findTodos.stream().allMatch(todo -> todo.getStudyInfoId().equals(expectedStudyInfoId)));


    }


    @Test
    @DisplayName("Todo Id 조회 테스트")
    public void readTodoId() {

        // given
        StudyTodo studyTodoId = StudyTodo.builder()
                .studyInfoId(expectedStudyInfoId)
                .title(expectedTitle)
                .detail(expectedDetail)
                .todoLink(expectedTodoLink)
                .endTime(expectedEndTime)
                .build();
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
        StudyInfo studyInfo = StudyInfo.builder()
                .userId(expectedLeaderId)
                .topic("깃터디1")
                .build();
        studyInfoRepository.save(studyInfo);

        StudyTodo studyTodo = StudyTodo.builder()
                .studyInfoId(1L)
                .title("백준 1234번 풀기")
                .detail("오늘 자정까지 풀고 제출한다")
                .todoLink("https://www.acmicpc.net/")
                .endTime(LocalDate.now())
                .build();
        studyTodoRepository.save(studyTodo);
        StudyTodoMapping studyTodoMapping = StudyTodoMapping.builder()
                .todoId(studyTodo.getId())
                .userId(expectedUserId)
                .status(expectedStatus)
                .build();
        studyTodoMappingRepository.save(studyTodoMapping);

        String updateTitle = "프로그래머스 1234번 풀기";
        String updateDetail = "오늘 오후 3시까지 풀다";
        String updateTodoLink = "https://programmers.co.kr/";
        LocalDate updateEndTime = LocalDate.of(2024, 2, 5);
        StudyTodoStatus updateStatus = TODO_COMPLETE;


        StudyTodoUpdateRequest request = new StudyTodoUpdateRequest();
        request.setTitle(updateTitle);
        request.setDetail(updateDetail);
        request.setTodoLink(updateTodoLink);
        request.setEndTime(updateEndTime);
        request.setStatus(updateStatus);

        // when
        studyTodoService.updateStudyTodo(studyTodo.getId(), request, expectedLeaderId);

        // then
        StudyTodo updatedTodo = studyTodoRepository.findById(studyTodo.getId()).orElseThrow();
        StudyTodoMapping updatedTodoMapping = studyTodoMappingRepository.findByTodoIdAndUserId(studyTodo.getId(), expectedUserId).orElseThrow();
        assertEquals(updateTitle, updatedTodo.getTitle());
        assertEquals(updateDetail, updatedTodo.getDetail());
        assertEquals(updateTodoLink, updatedTodo.getTodoLink());
        assertEquals(updateEndTime, updatedTodo.getEndTime());
        assertEquals(updateStatus, updatedTodoMapping.getStatus());

    }


    @Test
    @DisplayName("Todo 수정 권한 없는 경우")
    void updateTodo_NotAllowedUser_ThrowsException() {
        // given
        Long todoId = 1L;
        StudyTodoUpdateRequest request = new StudyTodoUpdateRequest();
        Long wrongUserId = expectedUserId + 1; // 잘못된 사용자 ID

        // when , then
        assertThrows(TodoException.class, () -> studyTodoService.updateStudyTodo(todoId, request, wrongUserId));
    }

    @Test
    @DisplayName("Todo 삭제 테스트")
    void deleteTodo_Success() {
        // given
        StudyInfo studyInfo = StudyInfo.builder()
                .userId(expectedLeaderId)
                .topic("깃터디1")
                .build();
        studyInfoRepository.save(studyInfo);

        StudyTodo studyTodo = StudyTodo.builder()
                .studyInfoId(expectedStudyInfoId)
                .title(expectedTitle)
                .detail(expectedDetail)
                .todoLink(expectedTodoLink)
                .endTime(expectedEndTime)
                .build();
        studyTodoRepository.save(studyTodo);

        StudyTodoMapping studyTodoMapping = StudyTodoMapping.builder()
                .todoId(expectedTodoId)
                .userId(expectedUserId)
                .status(expectedStatus)
                .build();
        studyTodoMappingRepository.save(studyTodoMapping);

        // when
        studyTodoService.deleteStudyTodo(expectedTodoId, expectedLeaderId);

        // then
        assertFalse(studyTodoRepository.existsById(studyTodoMapping.getTodoId()));
        assertTrue(studyTodoMappingRepository.findByTodoId(studyTodoMapping.getTodoId()).isEmpty());
    }

}

