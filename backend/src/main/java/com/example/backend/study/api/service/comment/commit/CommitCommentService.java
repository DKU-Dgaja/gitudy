package com.example.backend.study.api.service.comment.commit;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.domain.define.study.comment.commit.CommitComment;
import com.example.backend.domain.define.study.comment.commit.repository.CommitCommentRepository;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.study.api.controller.comment.commit.request.AddCommitCommentRequest;
import com.example.backend.study.api.controller.comment.commit.response.CommitCommentInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommitCommentService {
    private final StudyCommitRepository studyCommitRepository;
    private final CommitCommentRepository commitCommentRepository;

    public List<CommitCommentInfoResponse> getCommitCommentsList(Long commitId) {
        // 커밋이 존재하는지 확인
        studyCommitRepository.findById(commitId).orElseThrow(() -> {
            log.error(">>>> {} : {} <<<<", commitId, ExceptionMessage.COMMIT_NOT_FOUND.getText());
            throw new CommitException(ExceptionMessage.COMMIT_NOT_FOUND);
        });

        // 커밋 Id로 댓글리스트 가져오기 + 유저 조인
        return commitCommentRepository.findCommitCommentListByCommitIdJoinUser(commitId);
    }

    @Transactional
    public void addCommitComment(Long userId, Long commitId, AddCommitCommentRequest request) {
        // 댓글 저장
        commitCommentRepository.save(CommitComment.builder()
                .userId(userId)
                .studyCommitId(commitId)
                .content(request.getContent())
                .build());
    }

    @Transactional
    public void updateCommitComment(Long userId, Long commentId, AddCommitCommentRequest request) {
        CommitComment commitComment = commitCommentRepository.findById(commentId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", commentId, ExceptionMessage.COMMIT_COMMENT_NOT_FOUND.getText());

            throw new CommitException(ExceptionMessage.COMMIT_COMMENT_NOT_FOUND);
        });

        // 커밋 댓글의 주인이 아닐 경우 예외 발생
        if (userId != commitComment.getUserId()) {
            log.warn(">>>> {} : {} <<<<", userId, ExceptionMessage.COMMIT_COMMENT_PERMISSION_DENIED.getText());

            throw new CommitException(ExceptionMessage.COMMIT_COMMENT_PERMISSION_DENIED);
        }

        // 댓글 수정
        commitComment.updateComment(request.getContent());
    }
}
