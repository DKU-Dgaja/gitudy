package com.example.backend.study.api.service;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static com.example.backend.domain.define.study.commit.StudyCommitFixture.createDefaultStudyCommitList;
import static com.example.backend.domain.define.study.commit.StudyCommitFixture.expectedUserId;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class StudyCommitServiceTest extends TestConfig {

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
        int pageSize = 10;
        int dataSize = 20;
        Long cursorIdx = 20L;

        PageRequest pageable = PageRequest.of(0, pageSize);

        List<StudyCommit> commitList = createDefaultStudyCommitList(dataSize);
        studyCommitRepository.saveAll(commitList);

        // when
        Page<CommitInfoResponse> commitInfoPage = studyCommitService.selectUserCommitList(expectedUserId, pageable, cursorIdx);
//        List<CommitInfoResponse> content = commitInfoPage.getContent();
//        for (CommitInfoResponse c : content) {
//            System.out.println("c.getId() = " + c.getId());
//        }

        assertEquals(dataSize, commitInfoPage.getTotalElements());
        assertEquals(pageSize, commitInfoPage.getContent().size());

        for (CommitInfoResponse commit : commitInfoPage.getContent()) {
            assertTrue(commit.getId() < cursorIdx);
            assertEquals(expectedUserId, commit.getUserId());
        }
    }

    @Test
    void 커서가_null인_경우_마이_커밋_조회_테스트() {
        // given
        int pageSize = 10;
        int dataSize = 20;
        PageRequest pageable = PageRequest.of(0, pageSize);

        List<StudyCommit> commitList = createDefaultStudyCommitList(dataSize);
        studyCommitRepository.saveAll(commitList);

        // when
        Page<CommitInfoResponse> commitInfoPage = studyCommitService.selectUserCommitList(expectedUserId, pageable, null);
//        List<CommitInfoResponse> content = commitInfoPage.getContent();
//        for (CommitInfoResponse c : content) {
//            System.out.println("c.getId() = " + c.getId());
//        }

        assertEquals(dataSize, commitInfoPage.getTotalElements());
        assertEquals(pageSize, commitInfoPage.getContent().size());
    }

}