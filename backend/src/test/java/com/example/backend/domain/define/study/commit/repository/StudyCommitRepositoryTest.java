package com.example.backend.domain.define.study.commit.repository;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.example.backend.domain.define.study.commit.StudyCommitFixture.createDefaultStudyCommitList;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class StudyCommitRepositoryTest extends TestConfig {

    private final static int DATA_SIZE = 10;
    private final static Long LIMIT = 5L;

    @Autowired
    StudyCommitRepository studyCommitRepository;

    @AfterEach
    void tearDown() {
        studyCommitRepository.deleteAllInBatch();
    }

    @Test
    void 마이_커밋_커서_기반_페이지_조회_테스트() {
        // given
        Random random = new Random();
        Long cursorIdx = random.nextLong(LIMIT) + 1L;

        Set<Integer> usedValues = new HashSet<>();

        List<StudyCommit> commitList = createDefaultStudyCommitList(DATA_SIZE, 1L, 1L, 1L, usedValues);
        studyCommitRepository.saveAll(commitList);

        // when
        List<CommitInfoResponse> commitInfoList = studyCommitRepository.findStudyCommitListByUserId_CursorPaging(1L, null, cursorIdx, LIMIT);
//        for (CommitInfoResponse c : commitInfoList) {
//            System.out.println("c.getId() = " + c.getId());
//        }

        // then
        for (CommitInfoResponse commit : commitInfoList) {
            assertTrue(commit.getId() < cursorIdx);
        }

    }

    @Test
    void 커서가_null일_경우_마이_커밋_페이지_조회_테스트() {
        // given
        Set<Integer> usedValues = new HashSet<>();

        List<StudyCommit> commitList = createDefaultStudyCommitList(DATA_SIZE, 1L, 1L, 1L, usedValues);
        studyCommitRepository.saveAll(commitList);

        // when
        List<CommitInfoResponse> commitInfoList = studyCommitRepository.findStudyCommitListByUserId_CursorPaging(1L, null,null, LIMIT);
//        for (CommitInfoResponse c : commitInfoList) {
//            System.out.println("c.getId() = " + c.getId());
//        }

        // then
        assertEquals(LIMIT, commitInfoList.size());
    }

    @Test
    void 스터디별_커밋_리스트_조회() {
        // given
        Long userId = 1L;
        Long algoStudyId = 1L;
        Long javaStudyId = 2L;
        Long algoStudyTodoId = 1L;
        Long javaStudyTodoId = 2L;

//        Random random = new Random();
//        Long cursorIdx = random.nextLong(DATA_SIZE * 5) + 1L;
        Long cursorIdx = null;

        Set<Integer> usedValues = new HashSet<>();

        studyCommitRepository.saveAll(createDefaultStudyCommitList(DATA_SIZE, userId, algoStudyId, algoStudyTodoId, usedValues));
        studyCommitRepository.saveAll(createDefaultStudyCommitList(DATA_SIZE, userId, javaStudyId, javaStudyTodoId, usedValues));
        studyCommitRepository.saveAll(createDefaultStudyCommitList(DATA_SIZE, userId, algoStudyId, algoStudyTodoId, usedValues));
        studyCommitRepository.saveAll(createDefaultStudyCommitList(DATA_SIZE, userId, javaStudyId, javaStudyTodoId, usedValues));

        var commitList = studyCommitRepository.findStudyCommitListByUserId_CursorPaging(userId, algoStudyId, cursorIdx, LIMIT);
        var commitList2 = studyCommitRepository.findStudyCommitListByUserId_CursorPaging(userId, javaStudyId, cursorIdx, LIMIT);

//        System.out.println("cursorIdx = " + cursorIdx);
//        for (CommitInfoResponse c : commitList) {
//            System.out.println("c.getId() = " + c.getId());
//            System.out.println("c.getStudyInfoId() = " + c.getStudyInfoId());
//        }
//        for (CommitInfoResponse c2 : commitList2) {
//            System.out.println("c2.getId() = " + c2.getId());
//            System.out.println("c2.getStudyInfoId() = " + c2.getStudyInfoId());
//        }

        for (CommitInfoResponse commit : commitList) {
//            assertTrue(commit.getId() < cursorIdx);
            assertEquals(commit.getStudyInfoId(), algoStudyId);
        }

        for (CommitInfoResponse commit : commitList2) {
//            assertTrue(commit.getId() < cursorIdx);
            assertEquals(commit.getStudyInfoId(), javaStudyId);
        }
    }
}