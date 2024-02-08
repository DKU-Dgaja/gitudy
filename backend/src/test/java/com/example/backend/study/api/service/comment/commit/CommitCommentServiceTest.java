package com.example.backend.study.api.service.comment.commit;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.common.exception.member.StudyMemberException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.comment.commit.CommitComment;
import com.example.backend.domain.define.study.comment.commit.CommitCommentFixture;
import com.example.backend.domain.define.study.comment.commit.repository.CommitCommentRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.StudyCommitFixture;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.comment.commit.request.AddCommitCommentRequest;
import com.example.backend.study.api.controller.comment.commit.response.CommitCommentInfoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class CommitCommentServiceTest extends TestConfig {

    @Autowired
    private CommitCommentService commitCommentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommitCommentRepository commitCommentRepository;

    @Autowired
    private StudyCommitRepository studyCommitRepository;

    @Autowired
    private StudyMemberRepository studyMemberRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        commitCommentRepository.deleteAllInBatch();
        studyCommitRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
    }

    @Test
    void 커밋_댓글_리스트_조회_성공_테스트() {
        // given
        User userA = userRepository.save(User.builder().platformId("A").profileImageUrl("testA").build());
        User userB = userRepository.save(User.builder().platformId("B").profileImageUrl("testB").build());
        User userC = userRepository.save(User.builder().platformId("C").profileImageUrl("testC").build());

        StudyCommit commitA = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit("1"));
        StudyCommit commitB = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit("2"));

        commitCommentRepository.saveAll(CommitCommentFixture.createDefaultCommitCommentList(5, userA.getId(), commitA.getId()));
        commitCommentRepository.saveAll(CommitCommentFixture.createDefaultCommitCommentList(5, userB.getId(), commitB.getId()));
        commitCommentRepository.saveAll(CommitCommentFixture.createDefaultCommitCommentList(5, userC.getId(), commitA.getId()));
        commitCommentRepository.saveAll(CommitCommentFixture.createDefaultCommitCommentList(5, userC.getId(), commitB.getId()));

        // when
        List<CommitCommentInfoResponse> response = commitCommentService.getCommitCommentsList(commitA.getId());

        // then
        for (CommitCommentInfoResponse c : response) {
//            System.out.println("c.getId() = " + c.getId());
//            System.out.println("c.getStudyCommitId() = " + c.getStudyCommitId());
//            System.out.println("c.getUserInfoResponse().getUserId() = " + c.getUserInfoResponse().getUserId());
            assertEquals(commitA.getId(), c.getStudyCommitId());
            assertNotNull(c.getUserInfoResponse().getUserId());
        }

    }

    @Test
    void 커밋_댓글_리스트_조회_실패_테스트() {
        // given
        Long commitID = 1L;

        // when & then
        assertThrows(CommitException.class, () -> {
            commitCommentService.getCommitCommentsList(commitID);
        });
    }

    @Test
    void 커밋이_속한_스터디의_스터디원인지_검증_성공_테스트() {
        // given
        Long userId = 1L;
        Long studyInfoId = 1L;
        String commitSha = "test";

        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(userId, studyInfoId));
        StudyCommit commit = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit(commitSha));

        // when & then
        assertTrue(commitCommentService.isActiveStudyMember(userId, commit.getId()));
    }

    @Test
    void 커밋이_속한_스터디의_스터디원인지_검증_실패_테스트() {
        // given
        Long userId = 1L;
        Long studyInfoId = 2L;
        String commitSha = "test";

        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(userId, studyInfoId));
        StudyCommit commit = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit(commitSha));

        // when & then
        assertThrows(StudyMemberException.class, () -> {
            commitCommentService.isActiveStudyMember(userId, commit.getId());
        });
    }

    @Test
    void 커밋_댓글_저장_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyCommit commit = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit("SHA"));
        String content = "testtesttest";

        AddCommitCommentRequest request = AddCommitCommentRequest.builder().content(content).build();

        // when
        commitCommentService.addCommitComment(user.getId(), commit.getId(), request);
        CommitComment findCommitComment = commitCommentRepository.findById(commit.getId()).get();

        assertEquals(user.getId(), findCommitComment.getUserId());
        assertEquals(content, findCommitComment.getContent());
    }

    @Test
    void 커밋_주인이_커밋_수정을_시도해_성공하는_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyCommit commit = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit("SHA"));

        String updateContent = "update";

        var saveComment = commitCommentRepository.save(CommitCommentFixture.createDefaultCommitComment(user.getId(), commit.getId()));
        var request = AddCommitCommentRequest.builder().content(updateContent).build();

        // when
        commitCommentService.updateCommitComment(user.getId(), saveComment.getId(), request);
        CommitComment comment = commitCommentRepository.findById(saveComment.getId()).get();

        // then
        assertEquals(updateContent, comment.getContent());

    }

    @Test
    void 커밋_주인이_아닌_사람이_커밋_수정을_시도해_실패하는_테스트() {
        // given
        User userA = userRepository.save(UserFixture.generateAuthUser());
        User userB = userRepository.save(UserFixture.generateGoogleUser());
        StudyCommit commit = studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit("SHA"));

        String updateContent = "update";

        var saveComment = commitCommentRepository.save(CommitCommentFixture.createDefaultCommitComment(commit.getUserId(), commit.getId()));
        var request = AddCommitCommentRequest.builder().content(updateContent).build();

        // when
        assertThrows(CommitException.class, () -> {
            commitCommentService.updateCommitComment(userB.getId(), saveComment.getId(), request);
        });
    }
}