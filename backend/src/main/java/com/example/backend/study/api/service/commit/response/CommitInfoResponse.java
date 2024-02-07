package com.example.backend.study.api.service.commit.response;

import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
import com.example.backend.domain.define.study.commit.constant.LikeCount;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CommitInfoResponse {
    private Long id;              // 커밋 아이디
    private Long studyInfoId;     // 스터디 정보 아이디
    private Long userId;          // 사용자 아이디
    private String commitSHA;     // 커밋 식별자 SHA 값
    private String message;       // 커밋 메시지
    private LocalDate commitDate; // 커밋 날짜
    private CommitStatus status;  // 커밋 상태
    private String rejectionReason; // 커밋 거절 이유
    private LikeCount likeCount;  // 커밋 좋아요 수

    @Builder
    public CommitInfoResponse(Long id, Long studyInfoId, Long userId, String commitSHA, String message, LocalDate commitDate, CommitStatus status, String rejectionReason, LikeCount likeCount) {
        this.id = id;
        this.studyInfoId = studyInfoId;
        this.userId = userId;
        this.commitSHA = commitSHA;
        this.message = message;
        this.commitDate = commitDate;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.likeCount = likeCount;
    }

    public static CommitInfoResponse of(StudyCommit commit) {
        return CommitInfoResponse.builder()
                .id(commit.getId())
                .studyInfoId(commit.getStudyInfoId())
                .userId(commit.getUserId())
                .commitSHA(commit.getCommitSHA())
                .message(commit.getMessage())
                .commitDate(commit.getCommitDate())
                .status(commit.getStatus())
                .rejectionReason(commit.getRejectionReason())
                .likeCount(commit.getLikeCount())
                .build();
    }
}
