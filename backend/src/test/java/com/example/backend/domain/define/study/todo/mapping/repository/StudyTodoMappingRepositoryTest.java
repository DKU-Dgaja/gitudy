package com.example.backend.domain.define.study.todo.mapping.repository;

import com.example.backend.TestConfig;
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
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus.TODO_COMPLETE;
import static com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus.TODO_OVERDUE;
import static org.junit.jupiter.api.Assertions.*;

class StudyTodoMappingRepositoryTest extends TestConfig {

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyInfoRepository studyInfoRepository;

    @Autowired
    StudyTodoRepository studyTodoRepository;

    @Autowired
    StudyTodoMappingRepository todoMappingRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
        todoMappingRepository.deleteAllInBatch();
    }

    @Test
    void 투두_상태_완료_업데이트_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthJusung());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));
        todoMappingRepository.save(StudyTodoFixture.createStudyTodoMapping(todo.getId(), user.getId()));

        // when
        boolean result = todoMappingRepository.updateByUserIdAndTodoId(user.getId(), todo.getId(), TODO_COMPLETE);

        // then
        assertTrue(result); // 업데이트 성공

        StudyTodoMapping todoMapping = todoMappingRepository.findByTodoIdAndUserId(todo.getId(), user.getId()).get();
        assertEquals(StudyTodoStatus.TODO_COMPLETE, todoMapping.getStatus());
    }

    @Test
    void 투두_상태_지각_업데이트_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthJusung());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));
        todoMappingRepository.save(StudyTodoFixture.createStudyTodoMapping(todo.getId(), user.getId()));

        // when
        boolean result = todoMappingRepository.updateByUserIdAndTodoId(user.getId(), todo.getId(), TODO_OVERDUE);

        // then
        assertTrue(result); // 업데이트 성공

        StudyTodoMapping todoMapping = todoMappingRepository.findByTodoIdAndUserId(todo.getId(), user.getId()).get();
        assertEquals(TODO_OVERDUE, todoMapping.getStatus());
    }

    @Test
    void 같은_투두에_두번_커밋_시_투두_매핑은_수정하지_않는다() {
        // given
        User user = userRepository.save(UserFixture.generateAuthJusung());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));
        todoMappingRepository.save(StudyTodoFixture.createCompleteStudyTodoMapping(todo.getId(), user.getId()));

        // when
        boolean result = todoMappingRepository.updateByUserIdAndTodoId(user.getId(), todo.getId(), TODO_OVERDUE);

        // then
        assertTrue(result);

        StudyTodoMapping todoMapping = todoMappingRepository.findByTodoIdAndUserId(todo.getId(), user.getId()).get();

        // 두번째 커밋은 마감일을 넘겼지만 이전 첫번째 커밋은 마감 기한 내에 작성했으므로 COMPLETE 상태를 유지
        assertEquals(StudyTodoStatus.TODO_COMPLETE, todoMapping.getStatus());
    }

    @Test
    void 투두매핑이_조회되지_않는_경우_업데이트되지_않는다() {
        // given
        User user = userRepository.save(UserFixture.generateAuthJusung());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));
//        todoMappingRepository.save(StudyTodoFixture.createCompleteStudyTodoMapping(todo.getId(), user.getId()));

        // when
        boolean result = todoMappingRepository.updateByUserIdAndTodoId(user.getId(), todo.getId(), TODO_OVERDUE);

        // then
        assertFalse(result);
    }

}