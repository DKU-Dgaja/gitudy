package com.example.backend.study.api.service.commit;

import com.example.backend.auth.api.service.rank.event.StudyScoreUpdateEvent;
import com.example.backend.auth.api.service.rank.event.UserScoreUpdateEvent;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
import com.example.backend.domain.define.study.commit.event.CommitApproveEvent;
import com.example.backend.domain.define.study.commit.event.CommitRefuseEvent;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.example.backend.study.api.service.info.StudyInfoService;
import com.example.backend.study.api.service.member.StudyMemberService;
import com.example.backend.study.api.service.todo.StudyTodoService;
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
public class StudyCommitService {
    private final static Long MAX_LIMIT = 50L;

    private final StudyCommitRepository studyCommitRepository;

    private final UserService userService;
    private final StudyMemberService studyMemberService;
    private final StudyInfoService studyInfoService;
    private final ApplicationEventPublisher eventPublisher;
    private final StudyTodoService studyTodoService;

    public CommitInfoResponse getCommitDetailsById(Long commitId) {
        // 커밋 조회 예외처리
        StudyCommit commit = findStudyCommitByIdOrThrowException(commitId);

        return CommitInfoResponse.of(commit);
    }

    public List<CommitInfoResponse> selectUserCommitList(Long userId, Long studyId, Long cursorIdx, Long limit) {

        limit = Math.min(limit, MAX_LIMIT);

        return studyCommitRepository.findStudyCommitListByUserId_CursorPaging(userId, studyId, cursorIdx, limit);
    }

    public StudyCommit findStudyCommitByIdOrThrowException(Long commitId) {
        return studyCommitRepository.findById(commitId)
                .orElseThrow(() -> {
                    log.error(">>>> {} : {} <<<<", commitId, ExceptionMessage.COMMIT_NOT_FOUND.getText());
                    return new CommitException(ExceptionMessage.COMMIT_NOT_FOUND);
                });
    }

    @Transactional
    public void approveCommit(Long commitId) {
        StudyCommit commit = findStudyCommitByIdOrThrowException(commitId);

        commit.approveCommit();

        // 유저 점수로직 추가
        User user = userService.findUserByIdOrThrowException(commit.getUserId());
        user.addUserScore(2);

        // 멤버 점수로직 추가
        StudyMember studyMember = studyMemberService.findStudyMemberByStudyInfoIdAndUserIdOrThrowException(commit.getStudyInfoId(), commit.getUserId());
        studyMember.addMemberScore(2);

        // 스터디 점수로직 추가
        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(commit.getStudyInfoId());
        studyInfo.addStudyScore(2);

        // 유저점수 이벤트 발생
        eventPublisher.publishEvent(UserScoreUpdateEvent.builder()
                .userid(user.getId())
                .score(2)
                .build());
        // 스터디점수 이벤트 발생
        eventPublisher.publishEvent(StudyScoreUpdateEvent.builder()
                .studyInfoId(commit.getStudyInfoId())
                .score(2)
                .build());

        // 투두 정보 조회
        StudyTodo studyTodo = studyTodoService.findByIdOrThrowStudyTodoException(commit.getStudyTodoId());

        // 커밋 승인 알림 이벤트 발생
        eventPublisher.publishEvent(CommitApproveEvent.builder()
                .isPushAlarmYn(user.isPushAlarmYn())
                .userId(commit.getUserId())
                .studyInfoId(commit.getStudyInfoId())
                .studyTopic(studyInfo.getTopic())
                .studyTodoTopic(studyTodo.getTitle()));
    }

    @Transactional
    public void rejectCommit(Long commitId, String rejectionReason) {
        StudyCommit commit = findStudyCommitByIdOrThrowException(commitId);

        commit.rejectCommit(rejectionReason);

        // 유저 정보 조회
        User user = userService.findUserByIdOrThrowException(commit.getUserId());
        // 스터디 정보 조회
        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(commit.getStudyInfoId());
        // 투두 정보 조회
        StudyTodo studyTodo = studyTodoService.findByIdOrThrowStudyTodoException(commit.getStudyTodoId());

        // 커밋 거부 알림 이벤트 발생
        eventPublisher.publishEvent(CommitRefuseEvent.builder()
                .isPushAlarmYn(user.isPushAlarmYn())
                .userId(commit.getUserId())
                .studyInfoId(commit.getStudyInfoId())
                .studyTopic(studyInfo.getTopic())
                .studyTodoTopic(studyTodo.getTitle()));
    }

    public List<CommitInfoResponse> selectWaitingCommit(Long studyInfoId) {
        return studyCommitRepository.findStudyCommitListByStudyInfoIdAndStatus(studyInfoId, CommitStatus.COMMIT_WAITING)
                .stream()
                .map(CommitInfoResponse::of)
                .toList();
    }
}
