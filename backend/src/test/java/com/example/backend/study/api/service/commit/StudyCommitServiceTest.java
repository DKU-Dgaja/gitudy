package com.example.backend.study.api.service.commit;

import com.example.backend.auth.TestConfig;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.StudyCommitFixture;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
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
class StudyCommitServiceTest extends TestConfig {
    private final static int DATA_SIZE = 10;
    private final static Long LIMIT = 10L;

    @Autowired
    private StudyCommitService studyCommitService;

    @Autowired
    private StudyCommitRepository studyCommitRepository;

    @AfterEach
    void tearDown() {
        studyCommitRepository.deleteAllInBatch();
    }

    @Test
    void 커서가_null이_아닌_경우_마이_커밋_조회_테스트() {
        // given
        Random random = new Random();
        Long cursorIdx = random.nextLong(LIMIT) + 1L;

        Set<Integer> usedValues = new HashSet<>();

        List<StudyCommit> commitList = createDefaultStudyCommitList(DATA_SIZE, 1L, 1L, usedValues);
        studyCommitRepository.saveAll(commitList);

        // when
        List<CommitInfoResponse> commitInfoList = studyCommitService
                .selectUserCommitList(1L, null, cursorIdx, LIMIT);
//        System.out.println("cursorIdx = " + cursorIdx);
//        System.out.println("LIMIT = " + LIMIT);
//
//        for (CommitInfoResponse commit : commitInfoList) {
//            System.out.println("commit.getId() = " + commit.getId());
//        }

//        assertEquals(cursorIdx <= LIMIT ? cursorIdx-1 : LIMIT, commitInfoList.size());
        for (CommitInfoResponse commit : commitInfoList) {
            assertTrue(commit.getId() < cursorIdx);
        }
    }

    @Test
    void 커서가_null인_경우_마이_커밋_조회_테스트() {
        // given
        Set<Integer> usedValues = new HashSet<>();

        List<StudyCommit> commitList = createDefaultStudyCommitList(DATA_SIZE, 1L, 1L, usedValues);
        studyCommitRepository.saveAll(commitList);

        // when
        List<CommitInfoResponse> commitInfoList = studyCommitService.selectUserCommitList(1L, null, null, LIMIT);
//        List<CommitInfoResponse> content = commitInfoPage.getContent();
//        for (CommitInfoResponse c : content) {
//            System.out.println("c.getId() = " + c.getId());
//        }

        assertEquals(LIMIT, commitInfoList.size());
    }

    @Test
    void 커밋_상세_조회_성공_테스트() {
        // given
        Long studyId = 1L;
        Long userId = 1L;
        String commitSha = "123";

        StudyCommit savedCommit = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit(userId, studyId, commitSha));

        // when
        CommitInfoResponse commitInfoResponse = studyCommitService.getCommitDetailsById(savedCommit.getId());

        // then
        assertEquals(savedCommit.getId(), commitInfoResponse.getId());
        assertEquals(commitSha, commitInfoResponse.getCommitSHA());
    }

    @Test
    void 커밋_상세_조회_실패_테스트() {
        // given
        Long commitID = 1L;
//        StudyCommit savedCommit = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit(commitSha));

        // when & then
        assertThrows(CommitException.class, () -> {
            studyCommitService.getCommitDetailsById(commitID);
        });
    }
}