package com.example.backend.study.api.service.todo;

import com.example.backend.auth.TestConfig;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.todo.TodoException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.StudyTodoListAndCursorIdxResponse;
import com.example.backend.study.api.service.member.StudyMemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.backend.auth.config.fixture.UserFixture.*;
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
    private StudyMemberService studyMemberService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyMemberRepository studyMemberRepository;

    public final static String expectedTitle = "백준 1234번 풀기";
    public final static String expectedDetail = "오늘 자정까지 풀고 제출한다";
    public final static String expectedTodoLink = "https://www.acmicpc.net/";
    public final static LocalDate expectedTodoDate = LocalDate.now();
    public final static StudyTodoStatus expectedStatus = TODO_INCOMPLETE;
    public final static Long CursorIdx = null;
    public final static Long Limit = 3L;

    @AfterEach
    void tearDown() {
        studyTodoMappingRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
    }


    @Test
    @DisplayName("Todo 등록 테스트")
    public void registerTodo() {
        //given
        User leader = userRepository.save(generateAuthUser());
        User activeMember = userRepository.save(generateGoogleUser());
        User withdrawalMember = userRepository.save(generateKaKaoUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);


        studyMemberRepository.saveAll(List.of(
                StudyMemberFixture.createStudyMemberLeader(leader.getId(), studyInfo.getId()),
                StudyMemberFixture.createDefaultStudyMember(activeMember.getId(), studyInfo.getId()),
                StudyMemberFixture.createStudyMemberWithdrawal(withdrawalMember.getId(), studyInfo.getId())
        ));


        StudyTodoRequest request = StudyTodoFixture.generateStudyTodoRequest();

        //when
        studyMemberService.isValidateStudyLeader(leader, studyInfo.getId());
        studyTodoService.registerStudyTodo(request, studyInfo.getId());

        //then
        // StudyTodo
        List<StudyTodo> studyTodos = studyTodoRepository.findAll();
        assertNotNull(studyTodos);
        StudyTodo savedStudyTodo = studyTodos.get(0);
        assertEquals(studyInfo.getId(), savedStudyTodo.getStudyInfoId());


        List<StudyTodoMapping> mappings = studyTodoMappingRepository.findAll();
        // 활동중인 멤버에게 할당되었는지 확인
        assertTrue(mappings.stream()
                .anyMatch(mappingMember -> mappingMember.getUserId().equals(activeMember.getId())));
        // 비활동중인 멤버에게 할당되지 않았는지 확인
        assertFalse(mappings.stream()
                .anyMatch(mappingMember -> mappingMember.getUserId().equals(withdrawalMember.getId())));

    }

    @Test
    @DisplayName("Todo 수정 테스트")
    public void updateTodo() {

        //given
        User leader = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyTodo studyTodo = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.save(studyTodo);

        String updatedTitle = "제목변경";
        String updatedDetail = "설명변경";
        String updatedTodoLink = "링크변경";
        LocalDate updatedTodoDate = LocalDate.now().plusDays(3);

        StudyTodoUpdateRequest request = StudyTodoFixture.updateStudyTodoRequest(updatedTitle, updatedDetail, updatedTodoLink, updatedTodoDate);

        // when
        studyTodoService.updateStudyTodo(request, studyTodo.getId());

        // then
        StudyTodo updatedTodo = studyTodoRepository.findById(studyTodo.getId())
                .orElseThrow(() -> new TodoException(ExceptionMessage.TODO_NOT_FOUND));
        assertEquals(updatedTitle, updatedTodo.getTitle());
        assertEquals(updatedDetail, updatedTodo.getDetail());
        assertEquals(updatedTodoLink, updatedTodo.getTodoLink());
        assertEquals(updatedTodoDate, updatedTodo.getTodoDate());
    }

    @Test
    @DisplayName("Todo 삭제 테스트")
    void deleteTodo_Success() {
        // given
        User leader = userRepository.save(generateAuthUser());
        User activeMember1 = userRepository.save(generateGoogleUser());
        User activeMember2 = userRepository.save(generateKaKaoUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        studyMemberRepository.saveAll(List.of(
                StudyMemberFixture.createStudyMemberLeader(leader.getId(), studyInfo.getId()),
                StudyMemberFixture.createDefaultStudyMember(activeMember1.getId(), studyInfo.getId()),
                StudyMemberFixture.createDefaultStudyMember(activeMember2.getId(), studyInfo.getId())

        ));
        // 스터디장 To do 생성
        StudyTodo studyTodo = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.save(studyTodo);

        // 활동중인 스터디원들에게 To do 매핑
        studyTodoMappingRepository.saveAll(List.of(
                StudyTodoFixture.createStudyTodoMapping(studyTodo.getId(), activeMember1.getId()),
                StudyTodoFixture.createStudyTodoMapping(studyTodo.getId(), activeMember2.getId())
        ));

        // when
        studyMemberService.isValidateStudyLeader(leader, studyInfo.getId());
        studyTodoService.deleteStudyTodo(studyTodo.getId(), studyInfo.getId());  //To do 삭제

        // then
        assertThrows(TodoException.class, () -> {
            studyTodoService.deleteStudyTodo(990927L, studyInfo.getId());
        }, "TODO_NOT_FOUND 예외가 발생해야 한다.");

        // 등록된 To do는 삭제, 모든 매핑제거
        assertFalse(studyTodoRepository.existsById(studyTodo.getId()));
        List<StudyTodoMapping> todoMappings = studyTodoMappingRepository.findByTodoId(studyTodo.getId());
        assertTrue(todoMappings.isEmpty());

    }

    @Test
    @DisplayName("Todo 전체조회 테스트")
    void readTodoList_Success() {
        // given
        Random random = new Random();
        Long cursorIdx = Math.abs(random.nextLong()) + Limit;  // Limit 이상 랜덤값

        User leader = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        // 스터디장 To do 생성
        StudyTodo studyTodo1 = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        StudyTodo studyTodo2 = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        StudyTodo studyTodo3 = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        StudyTodo studyTodo4 = StudyTodoFixture.createStudyTodo(studyInfo.getId());
        studyTodoRepository.saveAll(List.of(studyTodo1, studyTodo2, studyTodo3, studyTodo4));

        // when
        StudyTodoListAndCursorIdxResponse responses = studyTodoService.readStudyTodoList(studyInfo.getId(), cursorIdx, Limit);

        // then
        assertNotNull(responses);
        assertEquals(3, responses.getTodoList().size());
        assertEquals(studyTodo1.getTitle(), responses.getTodoList().get(0).getTitle());
        assertEquals(studyTodo2.getTitle(), responses.getTodoList().get(1).getTitle());
    }

    @Test
    @DisplayName("Todo 전체조회 커서 기반 페이징 로직 검증")
    void readTodoList_CursorPaging_Success() {
        // given
        User leader = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        // 7개의 스터디 To do 생성 및 저장
        List<StudyTodo> createdTodos = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            createdTodos.add(StudyTodoFixture.createStudyTodo(studyInfo.getId()));
        }
        studyTodoRepository.saveAll(createdTodos);

        // when
        StudyTodoListAndCursorIdxResponse firstPageResponse = studyTodoService.readStudyTodoList(studyInfo.getId(), CursorIdx, Limit);


        // then
        assertNotNull(firstPageResponse);
        assertEquals(3, firstPageResponse.getTodoList().size());  // 3개만 가져와야함


        // when
        // 새로운 커서 인덱스를 사용하여 다음 페이지 조회
        Long newCursorIdx = firstPageResponse.getTodoList().get(firstPageResponse.getTodoList().size() - 1).getId();
        StudyTodoListAndCursorIdxResponse secondPageResponse = studyTodoService.readStudyTodoList(studyInfo.getId(), newCursorIdx, Limit);

        // then
        // 두 번째 페이지의 데이터 검증
        assertNotNull(secondPageResponse);
        assertEquals(3, secondPageResponse.getTodoList().size());

        // when
        // 새로운 커서 인덱스를 사용하여 다음 페이지 조회
        Long newCursorIdx2 = secondPageResponse.getTodoList().get(secondPageResponse.getTodoList().size() - 1).getId();
        StudyTodoListAndCursorIdxResponse thirdPageResponse = studyTodoService.readStudyTodoList(studyInfo.getId(), newCursorIdx2, Limit);

        // then
        // 세 번째 페이지의 데이터 검증
        assertNotNull(thirdPageResponse);
        assertEquals(1, thirdPageResponse.getTodoList().size());
    }
}