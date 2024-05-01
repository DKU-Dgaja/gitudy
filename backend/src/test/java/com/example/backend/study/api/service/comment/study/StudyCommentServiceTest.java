package com.example.backend.study.api.service.comment.study;

import com.example.backend.TestConfig;
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
import com.example.backend.study.api.controller.comment.study.request.StudyCommentUpdateRequest;
import com.example.backend.study.api.controller.comment.study.response.StudyCommentListAndCursorIdxResponse;
import com.example.backend.study.api.controller.comment.study.response.StudyCommentResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class StudyCommentServiceTest extends TestConfig {
    private final static int DATA_SIZE = 10;
    private final static Long LIMIT = 5L;
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
        StudyCommentRegisterRequest studyCommentRegisterRequest = StudyCommentFixture.createDefaultStudyCommentRegisterRequest();

        // when
        studyCommentService.registerStudyComment(studyCommentRegisterRequest, studyInfo.getId(), user.getId());

        // then
        List<StudyComment> studyComment = studyCommentRepository.findAll();
        assertAll(
                () -> assertEquals(studyComment.get(0).getStudyInfoId(), studyInfo.getId()),
                () -> assertEquals(studyComment.get(0).getUserId(), user.getId()),
                () -> assertEquals(studyComment.get(0).getContent(), studyCommentRegisterRequest.getContent())
        );
    }

    @Test
    void StudyComment_수정_테스트() {
        // given
        User user = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        StudyComment savedStudyComment =
                studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(user.getId(), studyInfo.getId()));
        StudyCommentUpdateRequest studyCommentUpdateRequest = StudyCommentFixture.createDefaultStudyCommentUpdateRequest();

        // when
        studyCommentService.updateStudyComment(studyCommentUpdateRequest, studyInfo.getId(),savedStudyComment.getId(), user.getId());

        // then
        Optional<StudyComment> studyComment = studyCommentRepository.findById(savedStudyComment.getId());
        assertAll(
                () -> assertEquals(studyComment.get().getStudyInfoId(), studyInfo.getId()),
                () -> assertEquals(studyComment.get().getUserId(), user.getId()),
                () -> assertEquals("ChangedContent", studyCommentUpdateRequest.getContent())
        );
    }

    @Test
    void StudyComment_수정_예외_테스트_댓글이_없음() {
        // given
        Long invalidStudyCommentId = 987654L;
        User user = userRepository.save(UserFixture.generateAuthUserByPlatformId("a"));
        User otherUser = userRepository.save(UserFixture.generateAuthUserByPlatformId("b"));
        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(user.getId(), studyInfo.getId()));
        StudyCommentUpdateRequest studyCommentUpdateRequest =
                StudyCommentFixture.createDefaultStudyCommentUpdateRequest();

        // when, then
        assertThrows(StudyCommentException.class, () -> {
            studyCommentService.updateStudyComment(studyCommentUpdateRequest, studyInfo.getId(), invalidStudyCommentId, user.getId());
        }, ExceptionMessage.STUDY_COMMENT_NOT_FOUND.getText());
    }

    @Test
    void StudyComment_수정_예외_테스트_댓글_수정_권한_없음() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUserByPlatformId("a"));
        User otherUser = userRepository.save(UserFixture.generateAuthUserByPlatformId("b"));
        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        StudyComment savedStudyComment =
                studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(user.getId(), studyInfo.getId()));
        StudyCommentUpdateRequest studyCommentUpdateRequest = StudyCommentFixture.createDefaultStudyCommentUpdateRequest();

        // when, then
        assertThrows(StudyCommentException.class, () -> {
            studyCommentService.updateStudyComment(studyCommentUpdateRequest, studyInfo.getId(), savedStudyComment.getId(), otherUser.getId());
        }, ExceptionMessage.STUDY_COMMENT_NOT_AUTHORIZED.getText());
    }
    @Test
    void 자신의_댓글_삭제_성공_테스트() {
        // given
        User leader = userRepository.save(UserFixture.generateAuthUserByPlatformId("a"));
        User user = userRepository.save(UserFixture.generateAuthUserByPlatformId("b"));

        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(leader.getId()));
        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(leader.getId(), studyInfo.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), studyInfo.getId()));

        StudyComment savedStudyComment =
                studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(user.getId(), studyInfo.getId()));

        // when
        studyCommentService.deleteStudyComment(user, studyInfo.getId(), savedStudyComment.getId());

        // then
        Optional<StudyComment> deletedComment = studyCommentRepository.findById(savedStudyComment.getId());
        assertThat(deletedComment).isEmpty();
    }

    @Test
    void 스터디장이_다른_스터디원_댓글_삭제_StudyComment_삭제_성공_테스트() {
        // given
        User leader = userRepository.save(UserFixture.generateAuthUserByPlatformId("a"));
        User user = userRepository.save(UserFixture.generateAuthUserByPlatformId("b"));

        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(leader.getId()));
        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(leader.getId(), studyInfo.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), studyInfo.getId()));

        StudyComment savedStudyComment =
                studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(user.getId(), studyInfo.getId()));

        // when
        studyCommentService.deleteStudyComment(leader, studyInfo.getId(), savedStudyComment.getId());

        // then
        Optional<StudyComment> deletedComment = studyCommentRepository.findById(savedStudyComment.getId());
        assertThat(deletedComment).isEmpty();
    }

    @Test
    void 삭제_권한_없는_유저가_다른_유저의_댓글_삭제_실패_테스트() {
        // given
        User leader = userRepository.save(UserFixture.generateAuthUserByPlatformId("a"));
        User user1 = userRepository.save(UserFixture.generateAuthUserByPlatformId("b"));
        User user2 = userRepository.save(UserFixture.generateAuthUserByPlatformId("c"));

        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(leader.getId()));
        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(leader.getId(), studyInfo.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user1.getId(), studyInfo.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user2.getId(), studyInfo.getId()));

        StudyComment savedStudyComment =
                studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(user1.getId(), studyInfo.getId()));

        // when, then
        assertThrows(StudyCommentException.class, () -> {
            studyCommentService.deleteStudyComment(user2, studyInfo.getId(), savedStudyComment.getId());
        }, ExceptionMessage.STUDY_COMMENT_NOT_AUTHORIZED.getText());
    }

    @Test
    void 커서가_null이_아닌_경우_스터디_댓글_리스트_조회_테스트_1() {
        // given
        Long cursorIdx = 5L;

        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));

        List<StudyComment> studyCommentList = StudyCommentFixture.createDefaultStudyCommentList(DATA_SIZE, user.getId(), study.getId());
        studyCommentRepository.saveAll(studyCommentList);

        // when
        StudyCommentListAndCursorIdxResponse studyCommentListResponse = studyCommentService.selectStudyCommentList(study.getId(), cursorIdx, LIMIT);

        for (StudyCommentResponse comment : studyCommentListResponse.getStudyCommentList()) {
            assertTrue(comment.getId() < cursorIdx);
        }
    }

    @Test
    void 커서가_null이_아닌_경우_스터디_댓글_리스트_조회_테스트_2() {
        // given
        Long cursorIdx = 15L;

        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));

        List<StudyComment> studyCommentList = StudyCommentFixture.createDefaultStudyCommentList(DATA_SIZE, user.getId(), study.getId());
        studyCommentRepository.saveAll(studyCommentList);

        // when
        StudyCommentListAndCursorIdxResponse studyCommentListResponse = studyCommentService.selectStudyCommentList(study.getId(), cursorIdx, LIMIT);

        for (StudyCommentResponse comment : studyCommentListResponse.getStudyCommentList()) {
            assertTrue(comment.getId() < cursorIdx);
        }
    }

    @Test
    void 커서가_null인_경우_마이_스터디_댓글_리스트_조회_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));

        List<StudyComment> studyCommentList = StudyCommentFixture.createDefaultStudyCommentList(DATA_SIZE, user.getId(), study.getId());
        studyCommentRepository.saveAll(studyCommentList);

        // when
        StudyCommentListAndCursorIdxResponse studyCommentListResponse = studyCommentService.selectStudyCommentList(study.getId(), null, LIMIT);

        assertEquals(LIMIT, studyCommentListResponse.getStudyCommentList().size());
    }

    @Test
    void 스터디에_여러_사용자_댓글을_단_후_동작_테스트() {
        // given
        int expectedSize = 3;

        User userA = userRepository.save(UserFixture.generateAuthUserByPlatformId("A"));
        User userB = userRepository.save(UserFixture.generateAuthUserByPlatformId("B"));
        User userC = userRepository.save(UserFixture.generateAuthUserByPlatformId("C"));
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(userA.getId()));

        studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(userA.getId(), study.getId()));
        studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(userB.getId(), study.getId()));
        studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(userC.getId(), study.getId()));

        // when
        StudyCommentListAndCursorIdxResponse studyCommentResponse = studyCommentService.selectStudyCommentList(study.getId(),  null, LIMIT);

        // then
        assertEquals(expectedSize, studyCommentResponse.getStudyCommentList().size());
    }

    @Test
    void 특정_스터디_댓글들_조회_성공_테스트() {
        // given
        int expectedStudy1CommentSize = 4;
        int expectedStudy2CommentSize = 3;

        User userA = userRepository.save(UserFixture.generateAuthUserByPlatformId("A"));
        User userB = userRepository.save(UserFixture.generateAuthUserByPlatformId("B"));
        User userC = userRepository.save(UserFixture.generateAuthUserByPlatformId("C"));
        StudyInfo study1 = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(userA.getId()));
        StudyInfo study2 = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(userA.getId()));

        studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(userA.getId(), study1.getId()));
        studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(userB.getId(), study1.getId()));
        studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(userC.getId(), study1.getId()));
        studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(userC.getId(), study1.getId()));

        studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(userA.getId(), study2.getId()));
        studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(userB.getId(), study2.getId()));
        studyCommentRepository.save(StudyCommentFixture.createDefaultStudyComment(userC.getId(), study2.getId()));

        // when
        StudyCommentListAndCursorIdxResponse studyCommentResponse1 = studyCommentService.selectStudyCommentList(study1.getId(),  null, LIMIT);
        StudyCommentListAndCursorIdxResponse studyCommentResponse2 = studyCommentService.selectStudyCommentList(study2.getId(),  null, LIMIT);

        // then
        assertEquals(expectedStudy1CommentSize, studyCommentResponse1.getStudyCommentList().size());
        assertEquals(expectedStudy2CommentSize, studyCommentResponse2.getStudyCommentList().size());
    }
}