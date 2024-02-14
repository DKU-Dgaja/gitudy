package com.example.backend.study.api.service.todo;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static com.example.backend.auth.config.fixture.UserFixture.generateGoogleUser;
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
    @Autowired
    private StudyMemberRepository studyMemberRepository;

    public final static Long expectedStudyInfoId = 1L;
    public final static String expectedTitle = "백준 1234번 풀기";
    public final static String expectedDetail = "오늘 자정까지 풀고 제출한다";
    public final static String expectedTodoLink = "https://www.acmicpc.net/";
    public final static LocalDate expectedTodoDate = LocalDate.now();
    public final static StudyTodoStatus expectedStatus = TODO_INCOMPLETE;

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
        User member = userRepository.save(generateGoogleUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember studyMember1 = StudyMemberFixture.createStudyMemberLeader(leader.getId(), studyInfo.getId());
        StudyMember studyMember2 = StudyMemberFixture.createDefaultStudyMember(member.getId(), studyInfo.getId());
        studyMemberRepository.save(studyMember1);
        studyMemberRepository.save(studyMember2);


        StudyTodoRequest request = StudyTodoFixture.generateStudyTodoRequest(studyInfo.getId());

        //when
        studyTodoService.registerStudyTodo(request, studyInfo.getId(), leader);

        //then
        // StudyTodo
        List<StudyTodo> studyTodos = studyTodoRepository.findAll();
        assertNotNull(studyTodos);
        StudyTodo savedStudyTodo = studyTodos.get(0);
        assertEquals(studyInfo.getId(), savedStudyTodo.getStudyInfoId());

        List<StudyMember> members = studyMemberRepository.findByStudyInfoId(studyInfo.getId()); // 스터디 멤버 조회
        assertFalse(members.isEmpty()); // 스터디 멤버가 존재하는지 확인

        members.forEach(mb -> {
            // 각 스터디 멤버에 대한 StudyTodoMapping이 존재하는지 확인
            List<StudyTodoMapping> mappings = studyTodoMappingRepository.findByUserId(mb.getUserId());
            assertFalse(mappings.isEmpty()); // 각 스터디 멤버에 대해 해당 StudyTodo에 대한 매핑이 존재하는지 확인
            assertTrue(mappings.stream().anyMatch(mapping -> mapping.getTodoId().equals(savedStudyTodo.getId()))); // 매핑된 To do 확인
        });


    }
}