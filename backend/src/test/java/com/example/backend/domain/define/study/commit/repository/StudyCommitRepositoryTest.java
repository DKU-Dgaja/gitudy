package com.example.backend.domain.define.study.commit.repository;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;

import static com.example.backend.domain.define.study.commit.StudyCommitFixture.createDefaultStudyCommitList;
import static com.example.backend.domain.define.study.commit.StudyCommitFixture.expectedUserId;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class StudyCommitRepositoryTest extends TestConfig {

    private final static int DATA_SIZE = 10;
    private final static Long LIMIT = 10L;

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

        List<StudyCommit> commitList = createDefaultStudyCommitList(DATA_SIZE);
        studyCommitRepository.saveAll(commitList);

        // when
        List<CommitInfoResponse> commitInfoList = studyCommitRepository.findStudyCommitListByUserId_CursorPaging(expectedUserId, cursorIdx, LIMIT);
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
        List<StudyCommit> commitList = createDefaultStudyCommitList(DATA_SIZE);
        studyCommitRepository.saveAll(commitList);

        // when
        List<CommitInfoResponse> commitInfoList = studyCommitRepository.findStudyCommitListByUserId_CursorPaging(expectedUserId, null, LIMIT);
//        for (CommitInfoResponse c : commitInfoList) {
//            System.out.println("c.getId() = " + c.getId());
//        }

        // then
        assertEquals(LIMIT, commitInfoList.size());
    }

}