package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommitFixture;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("NonAsciiCharacters")
public class StudyTodoRepositoryTest extends TestConfig {
    @Autowired
    private StudyTodoRepository studyTodoRepository;

    @Autowired
    private StudyCommitRepository studyCommitRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    private final String expectedTitle = "Title";
    private final String expectedDetail = "Detail";
    private final String expectedTodoLink = "http://example.com/todo";
    private final LocalDate expectedTodoDate = LocalDate.now();

    @AfterEach
    void tearDown() {
        studyTodoRepository.deleteAllInBatch();
        studyCommitRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
    }

    @Test
    void 커서가_null일_경우_전체_페이지_조회_테스트() {
        // given
        Long cursorIdx = null;
        Long limit = 3L;

        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));


        // To do 10개 저장
        IntStream.rangeClosed(1, 10).forEach(td -> {
            StudyTodo studyTodo = studyTodoRepository.save(StudyTodoFixture.createStudyTodoCustom(
                    study.getId(),
                    expectedTitle + td,
                    expectedDetail + td,
                    expectedTodoLink + td,
                    expectedTodoDate.plusDays(td)
            ));
            // 각 To do에 Commit 2개씩 저장
            IntStream.rangeClosed(1, 2).forEach(ci -> {
                studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit(
                        user.getId(),
                        study.getId(),
                        studyTodo.getId(),
                        "CommitSHA" + td + ci
                ));
            });
        });

        // when
        var response = studyTodoRepository.findStudyTodoListByStudyInfoId_CursorPaging(study.getId(), cursorIdx, limit);

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
        Long limit = 3L;
        Long cursorIdx = Long.MAX_VALUE;

        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        // To do 10개 저장
        studyTodoRepository.saveAll(StudyTodoFixture.createStudyTodoList(study.getId(), 5));

        var response = studyTodoRepository.findStudyTodoListByStudyInfoId_CursorPaging(study.getId(), cursorIdx, limit);

        assertEquals(limit, response.size());
    }

    @Test
    void 마감일이_가장_빠른_투두를_성공적으로_조회한다() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        // To do 5개 저장
        studyTodoRepository.saveAll(List.of(
                StudyTodoFixture.createStudyTodoCustom(study.getId(), "A","A", "A", LocalDate.now().plusDays(1)),
                StudyTodoFixture.createStudyTodoCustom(study.getId(), "B","B", "B", LocalDate.now().plusDays(2)),
                StudyTodoFixture.createStudyTodoCustom(study.getId(), "C","C", "C", LocalDate.now().plusDays(3)),
                StudyTodoFixture.createStudyTodoCustom(study.getId(), "D","D", "D", LocalDate.now().plusDays(4)),
                StudyTodoFixture.createStudyTodoCustom(study.getId(), "E","E", "E", LocalDate.now().plusDays(5))
        ));

        // when
        StudyTodo todo = studyTodoRepository.findStudyTodoByStudyInfoIdWithEarliestDueDate(study.getId()).orElse(null);

        // then
        assertEquals("A", todo.getTitle());
        assertEquals("A", todo.getDetail());
        assertEquals("A", todo.getTodoLink());
    }

    @Test
    void 마감일이_지나지_않은_활성화된_투두를_조회한다() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        int expectedSize = 3;

        // To do 5개 저장
        studyTodoRepository.saveAll(List.of(
                StudyTodoFixture.createStudyTodoCustom(study.getId(), "A","A", "A", today.plusDays(1)),
                StudyTodoFixture.createStudyTodoCustom(study.getId(), "B","B", "B", today.plusDays(2)),
                StudyTodoFixture.createStudyTodoCustom(study.getId(), "C","C", "C", today),
                StudyTodoFixture.createStudyTodoCustom(study.getId(), "D","D", "D", today.minusDays(1)),
                StudyTodoFixture.createStudyTodoCustom(study.getId(), "E","E", "E", today.minusDays(2))
        ));

        // when
        List<StudyTodo> todoList = studyTodoRepository.findByStudyInfoIdAndTodoDateGreaterThanEqual(study.getId(), today);

        // then
        assertEquals(expectedSize, todoList.size());
    }
}
