package com.example.backend.study.api.service.comment.study;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.comment.study.StudyCommentException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.comment.StudyCommentFixture;
import com.example.backend.domain.define.study.comment.study.StudyComment;
import com.example.backend.domain.define.study.comment.study.repository.StudyCommentRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentRegisterRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class StudyCommentServiceTest extends TestConfig {
    @Autowired
    private StudyCommentRepository studyCommentRepository;

    @Autowired
    private StudyMemberRepository studyMemberRepository;

    @Autowired
    private StudyCommentService studyCommentService;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        studyCommentRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
    }

    @Test
    void StudyComment_등록_테스트() {
        // given
        User user = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(user.getId(), studyInfo.getId()));
        StudyCommentRegisterRequest studyCommentRegisterRequest
                = StudyCommentFixture.createDefaultStudyCommentRegisterRequest(user.getId());

        // when
        studyCommentService.registerStudyComment(studyCommentRegisterRequest, studyInfo.getId());

        // then
        List<StudyComment> studyComment = studyCommentRepository.findAll();
        assertAll(
                () -> assertEquals(studyComment.get(0).getStudyInfoId(), studyInfo.getId()),
                () -> assertEquals(studyComment.get(0).getUserId(), studyCommentRegisterRequest.getUserId()),
                () -> assertEquals(studyComment.get(0).getContent(), studyCommentRegisterRequest.getContent())
        );
    }

    @Test
    void StudyComment_등록_예외_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUserByPlatformId("a"));
        User otherUser = userRepository.save(UserFixture.generateAuthUserByPlatformId("b"));

        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(user.getId(), studyInfo.getId()));

        // when
        StudyCommentRegisterRequest studyCommentRegisterRequest
                = StudyCommentFixture.createDefaultStudyCommentRegisterRequest(otherUser.getId());

        // then
        assertThrows(StudyCommentException.class, () -> {
            studyCommentService.registerStudyComment(studyCommentRegisterRequest, studyInfo.getId());
        }, ExceptionMessage.USER_NOT_STUDY_MEMBER.getText());
    }
}