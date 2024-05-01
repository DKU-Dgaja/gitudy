package com.example.backend.domain.define.study.comment.commit.repository;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.comment.commit.CommitCommentFixture;
import com.example.backend.study.api.controller.comment.commit.response.CommitCommentInfoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings("NonAsciiCharacters")
class CommitCommentRepositoryTest extends TestConfig {
    @Autowired
    private CommitCommentRepository commitCommentRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        commitCommentRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void 사용자_정보가_포함된_커밋_댓글_리스트_조회_성공_테스트() {
        // given
        Long commitId = 1L;
        Long otherCommitId = 2L;

        User userA = userRepository.save(User.builder().platformId("A").profileImageUrl("testA").build());
        User userB = userRepository.save(User.builder().platformId("B").profileImageUrl("testB").build());
        User userC = userRepository.save(User.builder().platformId("C").profileImageUrl("testC").build());

        commitCommentRepository.saveAll(CommitCommentFixture.createDefaultCommitCommentList(5, userA.getId(), commitId));
        commitCommentRepository.saveAll(CommitCommentFixture.createDefaultCommitCommentList(5, userB.getId(), otherCommitId));
        commitCommentRepository.saveAll(CommitCommentFixture.createDefaultCommitCommentList(5, userC.getId(), commitId));
        commitCommentRepository.saveAll(CommitCommentFixture.createDefaultCommitCommentList(5, userC.getId(), otherCommitId));

        // when
        List<CommitCommentInfoResponse> response = commitCommentRepository.findCommitCommentListByCommitIdJoinUser(commitId);

        // then
        for (CommitCommentInfoResponse c : response) {
//            System.out.println("c.getId() = " + c.getId());
//            System.out.println("c.getStudyCommitId() = " + c.getStudyCommitId());
//            System.out.println("c.getUserInfoResponse().getUserId() = " + c.getUserInfoResponse().getUserId());
            assertEquals(commitId, c.getStudyCommitId());
            assertNotNull(c.getUserInfoResponse().getUserId());
        }
    }

    @Test
    void 댓글이_없는_커밋_테스트() {
        // given
        Long commitId = 1L;
        userRepository.save(User.builder().platformId("A").profileImageUrl("testA").build());

        List<CommitCommentInfoResponse> response = commitCommentRepository.findCommitCommentListByCommitIdJoinUser(commitId);

        assertNotNull(response);
        assertThat(response).isEmpty();
    }

}
