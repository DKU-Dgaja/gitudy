package com.example.backend.study.api.service;

import com.example.backend.auth.TestConfig;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.StudyCommitFixture;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.study.api.service.commit.StudyCommitService;
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
    void 커밋_상세_조회_성공_테스트() {
        // given
        String commitSha = "123";
        StudyCommit savedCommit = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit(commitSha));

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