package com.example.backend.study.api.service.comment.commit;

import com.example.backend.auth.api.service.rank.event.StudyScoreUpdateEvent;
import com.example.backend.auth.api.service.rank.event.UserScoreUpdateEvent;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.comment.commit.CommitComment;
import com.example.backend.domain.define.study.comment.commit.repository.CommitCommentRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.study.api.controller.comment.commit.request.AddCommitCommentRequest;
import com.example.backend.study.api.controller.comment.commit.response.CommitCommentInfoResponse;
import com.example.backend.study.api.service.commit.StudyCommitService;
import com.example.backend.study.api.service.info.StudyInfoService;
import com.example.backend.study.api.service.member.StudyMemberService;
import com.example.backend.study.api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommitCommentService {
    private final CommitCommentRepository commitCommentRepository;
    private final StudyCommitService studyCommitService;
    private final UserService userService;
    private final StudyMemberService studyMemberService;
    private final StudyInfoService studyInfoService;
    private final ApplicationEventPublisher eventPublisher;

    public List<CommitCommentInfoResponse> getCommitCommentsList(Long commitId, Long currentUserId) {
        // 커밋 조회 예외처리
        studyCommitService.findStudyCommitByIdOrThrowException(commitId);

        // 커밋 Id로 댓글리스트 가져오기 + 유저 조인
        return commitCommentRepository.findCommitCommentListByCommitIdJoinUser(commitId, currentUserId);
    }

    @Transactional
    public void addCommitComment(Long userId, Long commitId, AddCommitCommentRequest request) {
        // 댓글 저장
        commitCommentRepository.save(CommitComment.builder()
                .userId(userId)
                .studyCommitId(commitId)
                .content(request.getContent())
                .build());

        // 유저 점수로직 추가
        User user = userService.findUserByIdOrThrowException(userId);
        user.addUserScore(1);

        // 멤버 점수로직 추가
        StudyMember studyMember = studyMemberService.findStudyMemberByStudyInfoIdAndUserIdOrThrowException(request.getStudyInfoId(), userId);
        studyMember.addMemberScore(1);

        // 스터디 점수로직 추가
        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(request.getStudyInfoId());
        studyInfo.addStudyScore(1);

        // 유저점수 이벤트 발생
        eventPublisher.publishEvent(UserScoreUpdateEvent.builder()
                .userid(user.getId())
                .score(1)
                .build());
        // 스터디점수 이벤트 발생
        eventPublisher.publishEvent(StudyScoreUpdateEvent.builder()
                .studyInfoId(request.getStudyInfoId())
                .score(1)
                .build());
    }

    @Transactional
    public void updateCommitComment(Long userId, Long commentId, AddCommitCommentRequest request) {
        // 커밋 댓글 조회 예외처리
        CommitComment commitComment = findCommitCommentByIdOrThrowException(commentId);

        // 커밋 댓글의 주인이 아닐 경우 예외 발생
        isCommitOwner(userId, commitComment);

        // 댓글 수정
        commitComment.updateComment(request.getContent());
    }

    @Transactional
    public void deleteCommitComment(Long userId, Long commentId) {
        // 커밋 댓글 조회 예외처리
        CommitComment commitComment = findCommitCommentByIdOrThrowException(commentId);

        // 커밋 댓글의 주인이 아닐 경우 예외 발생
        isCommitOwner(userId, commitComment);

        // 댓글 삭제
        commitCommentRepository.delete(commitComment);
    }

    public CommitComment findCommitCommentByIdOrThrowException(Long commentId) {
        return commitCommentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", commentId, ExceptionMessage.COMMIT_COMMENT_NOT_FOUND.getText());
                    return new CommitException(ExceptionMessage.COMMIT_COMMENT_NOT_FOUND);
                });
    }

    private static void isCommitOwner(Long userId, CommitComment commitComment) {
        if (userId != commitComment.getUserId()) {
            log.warn(">>>> {} : {} <<<<", userId, ExceptionMessage.COMMIT_COMMENT_PERMISSION_DENIED.getText());
            throw new CommitException(ExceptionMessage.COMMIT_COMMENT_PERMISSION_DENIED);
        }
    }
}
