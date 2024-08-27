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
    private Long studyTodoId;     // 스터디 투두 아이디
    private Long userId;          // 사용자 아이디
    private String commitSHA;     // 커밋 식별자 SHA 값
    private String message;       // 커밋 메시지
    private LocalDate commitDate; // 커밋 날짜
    private CommitStatus status;  // 커밋 상태
    private String rejectionReason; // 커밋 거절 이유
    private LikeCount likeCount;  // 커밋 좋아요 수
    private String name;          // 커밋 사용자 이름
    private String profileImageUrl;  // 커밋 사용자 프로필 이미지

    @Builder
    public CommitInfoResponse(Long id, Long studyInfoId, Long studyTodoId, Long userId, String commitSHA, String message, LocalDate commitDate, CommitStatus status, String rejectionReason, LikeCount likeCount, String name, String profileImageUrl) {
        this.id = id;
        this.studyInfoId = studyInfoId;
        this.studyTodoId = studyTodoId;
        this.userId = userId;
        this.commitSHA = commitSHA;
        this.message = message;
        this.commitDate = commitDate;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.likeCount = likeCount;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public static CommitInfoResponse of(StudyCommit commit, String name, String profileImageUrl) {
        return CommitInfoResponse.builder()
                .id(commit.getId())
                .studyInfoId(commit.getStudyInfoId())
                .studyTodoId(commit.getStudyTodoId())
                .userId(commit.getUserId())
                .commitSHA(commit.getCommitSHA())
                .message(commit.getMessage())
                .commitDate(commit.getCommitDate())
                .status(commit.getStatus())
                .rejectionReason(commit.getRejectionReason())
                .likeCount(commit.getLikeCount())
                .name(name)
                .profileImageUrl(profileImageUrl)
                .build();
    }

}
