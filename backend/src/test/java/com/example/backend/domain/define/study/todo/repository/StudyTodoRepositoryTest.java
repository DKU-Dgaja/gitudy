package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.example.backend.study.api.service.todo.StudyTodoServiceTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("NonAsciiCharacters")
public class StudyTodoRepositoryTest extends TestConfig {
    @Autowired
    private StudyTodoRepository studyTodoRepository;

    @AfterEach
    void tearDown() {
        studyTodoRepository.deleteAllInBatch();
    }

    @Test
    void 커서가_null일_경우_전체_페이지_조회_테스트() {
        // given
        Long cursorIdx = null;
        Long studyInfoId = 1L;
        Long limit = 3L;

        // To do 10개 저장
        for (int td = 1; td <= 10; td++) {
            studyTodoRepository.save(StudyTodoFixture.createStudyTodoList(studyInfoId,
                    expectedTitle + td,
                    expectedDetail + td,
                    expectedTodoLink + td,
                    expectedTodoDate.plusDays(td)
            ));
        }

        // when
        List<StudyTodoResponse> studyTodoResponses = studyTodoRepository.findStudyTodoListByStudyInfoId_CursorPaging(studyInfoId, cursorIdx, limit);

        // then
        assertEquals(limit, studyTodoResponses.size());
        assertEquals(expectedTitle + "10", studyTodoResponses.get(0).getTitle());
    }

    @Test
    void 커서가_null이_아닌경우_전체_페이지_조회_테스트() {

        // given
        Long cursorIdx = 5L;
        Long studyInfoId = 1L;
        Long limit = 3L;

        // To do 10개 저장
        for (int td = 1; td <= 10; td++) {
            studyTodoRepository.save(StudyTodoFixture.createStudyTodoList(studyInfoId,
                    expectedTitle + td,
                    expectedDetail + td,
                    expectedTodoLink + td,
                    expectedTodoDate.plusDays(td)
            ));
        }

        // when
        List<StudyTodoResponse> studyTodoResponses = studyTodoRepository.findStudyTodoListByStudyInfoId_CursorPaging(studyInfoId, cursorIdx, limit);

        // then
        assertEquals(limit, studyTodoResponses.size());
        assertEquals(expectedTitle + "4", studyTodoResponses.get(0).getTitle());
        assertEquals(expectedTitle + "2", studyTodoResponses.get(2).getTitle());
    }
}
