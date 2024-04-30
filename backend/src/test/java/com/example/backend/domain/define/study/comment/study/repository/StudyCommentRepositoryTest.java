package com.example.backend.domain.define.study.comment.study.repository;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.comment.StudyCommentFixture;
import com.example.backend.domain.define.study.comment.study.StudyComment;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.controller.comment.study.response.StudyCommentResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.backend.domain.define.account.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class StudyCommentRepositoryTest extends TestConfig {
    private final static int DATA_SIZE = 20;
    private final static Long LIMIT = 10L;

    @Autowired
    private StudyCommentRepository studyCommentRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        studyCommentRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void 스터디_댓글_리스트_조회_쿼리_테스트() {
        // given
        Long cursorIdx = 25L;

        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));

        List<StudyComment> StudyCommentList = StudyCommentFixture.createDefaultStudyCommentList(DATA_SIZE, user.getId(), study.getId());
        studyCommentRepository.saveAll(StudyCommentList);

        // when
        List<StudyCommentResponse> StudyCommentListResponse = studyCommentRepository.findStudyCommentListByStudyInfoIdJoinUser(study.getId(), cursorIdx, LIMIT);

        // then
        for (StudyCommentResponse b : StudyCommentListResponse) {
            assertTrue(b.getId() < cursorIdx);
            assertEquals(user.getId(), b.getUserInfoResponse().getUserId());
            assertEquals(study.getId(), b.getStudyInfoId());
        }
    }


    @Test
    void cursor가_null인_경우_스터디_댓글_리스트_조회_쿼리_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));

        List<StudyComment> StudyCommentList = StudyCommentFixture.createDefaultStudyCommentList(DATA_SIZE, user.getId(), study.getId());
        studyCommentRepository.saveAll(StudyCommentList);

        // when
        List<StudyCommentResponse> StudyCommentListResponse = studyCommentRepository.findStudyCommentListByStudyInfoIdJoinUser(study.getId(), null, LIMIT);

        // then
        assertEquals(LIMIT, StudyCommentListResponse.size());
        for (StudyCommentResponse b : StudyCommentListResponse) {
            assertEquals(user.getId(), b.getUserInfoResponse().getUserId());
            assertEquals(study.getId(), b.getStudyInfoId());
        }
    }
}