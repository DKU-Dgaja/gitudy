package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.study.commit.StudyCommitFixture;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;
import com.example.backend.study.api.controller.todo.response.StudyTodoWithCommitsResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.backend.study.api.service.todo.StudyTodoServiceTest.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("NonAsciiCharacters")
public class StudyTodoRepositoryTest extends TestConfig {
    @Autowired
    private StudyTodoRepository studyTodoRepository;

    @Autowired
    private StudyCommitRepository studyCommitRepository;


    private final String expectedTitle = "Title";
    private final String expectedDetail = "Detail";
    private final String expectedTodoLink = "http://example.com/todo";
    private final LocalDate expectedTodoDate = LocalDate.now();

    @AfterEach
    void tearDown() {
        studyTodoRepository.deleteAllInBatch();
        studyCommitRepository.deleteAllInBatch();
    }

    @Test
    void 커서가_null일_경우_전체_페이지_조회_테스트() {
        // given
        Long cursorIdx = null;
        Long studyInfoId = 1L;
        Long limit = 3L;

        // To do 10개 저장
        IntStream.rangeClosed(1, 10).forEach(td -> {
            StudyTodo studyTodo = studyTodoRepository.save(StudyTodoFixture.createStudyTodoList(
                    studyInfoId,
                    expectedTitle + td,
                    expectedDetail + td,
                    expectedTodoLink + td,
                    expectedTodoDate.plusDays(td)
            ));
            // 각 To do에 Commit 2개씩 저장
            IntStream.rangeClosed(1, 2).forEach(ci -> {
                studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit(
                        1L,
                        studyInfoId,
                        studyTodo.getId(),
                        "CommitSHA" + td + ci
                ));
            });
        });

        // when
        var response = studyTodoRepository.findStudyTodoListByStudyInfoId_CursorPaging(studyInfoId, cursorIdx, limit);

        // then
        response.forEach(r -> {
            System.out.println("Response StudyTodo ID: " + r.getId() + ", Title: " + r.getTitle() + ", Commit Count: " + r.getCommits().size());
        });

        assertEquals(limit, response.size());
        assertEquals(expectedTitle + "10", response.get(0).getTitle());
        assertEquals(2, response.get(0).getCommits().size()); // Commit 개수 확인
    }

    @Test
    void 커서가_null이_아닌경우_전체_페이지_조회_테스트() {

        // given
        Long cursorIdx = 5L;
        Long studyInfoId = 1L;
        Long limit = 3L;

        // To do 10개 저장
        IntStream.rangeClosed(1, 10).forEach(td -> {
            StudyTodo studyTodo = studyTodoRepository.save(StudyTodoFixture.createStudyTodoList(
                    studyInfoId,
                    expectedTitle + td,
                    expectedDetail + td,
                    expectedTodoLink + td,
                    expectedTodoDate.plusDays(td)
            ));
            // 각 To do에 Commit 2개씩 저장
            IntStream.rangeClosed(1, 2).forEach(ci -> {
                studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit(
                        1L,
                        studyInfoId,
                        studyTodo.getId(),
                        "CommitSHA" + td + ci
                ));
            });
        });

        // when
        var response = studyTodoRepository.findStudyTodoListByStudyInfoId_CursorPaging(studyInfoId, cursorIdx, limit);

        // then
        assertEquals(limit, response.size());
        assertEquals(expectedTitle + "4", response.get(0).getTitle());
        assertEquals(2, response.get(0).getCommits().size()); // 첫 번째 To do의 Commit 개수 확인
        assertEquals(expectedTitle + "3", response.get(1).getTitle());
        assertEquals(2, response.get(1).getCommits().size()); // 두 번째 To do의 Commit 개수 확인
        assertEquals(expectedTitle + "2", response.get(2).getTitle());
        assertEquals(2, response.get(2).getCommits().size()); // 세 번째 To do의 Commit 개수 확인
    }
}
