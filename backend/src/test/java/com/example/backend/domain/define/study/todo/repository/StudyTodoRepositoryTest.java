package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class StudyTodoRepositoryTest extends TestConfig {

    @Autowired
    private StudyTodoRepository studyTodoRepository;

    @AfterEach
    void tearDown() {
        studyTodoRepository.deleteAllInBatch();
    }

    @Test
    void 스터디_아이디와_투두_아이디로_해당_투두를_찾는다() {
        //given
        Long studyInfoId = 1L;

        StudyTodo savedTodo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(studyInfoId));

        //when
        assertFalse(studyTodoRepository.findByIdAndStudyInfoId(savedTodo.getId(), studyInfoId).isEmpty());
    }
}
