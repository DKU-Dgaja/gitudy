package com.example.backend.domain.define.study.commit;

import com.example.backend.domain.define.BaseEntity;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
import com.example.backend.domain.define.study.commit.constant.LikeCount;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.study.api.service.github.response.GithubCommitResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;

@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STUDY_COMMIT")
public class StudyCommit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDY_COMMIT_ID")
    private Long id;                            // 아이디

    @Column(name = "STUDY_INFO_ID", nullable = false)
    private Long studyInfoId;                   // 스터디 ID

    @Column(name = "STUDY_TODO_ID", nullable = false)
    private Long studyTodoId;                   // 투두 정보

    @Column(name = "USER_ID", nullable = false)
    private Long userId;                        // 사용자 정보

    @Column(name = "COMMIT_SHA", nullable = false, unique = true)
    private String commitSHA;                   // 커밋의 식별자 SHA 값

    @Column(name = "MESSAGE", nullable = false)
    private String message;                     // 커밋 메세지

    @Temporal(TemporalType.DATE)
    @Column(name = "COMMIT_DATE", nullable = false)
    private LocalDate commitDate;               // 커밋 날짜

    @Enumerated(EnumType.STRING)
    @Column(name = "COMMIT_STATUS")
    @ColumnDefault(value = "'COMMIT_APPROVAL'")
    private CommitStatus status;                // 커밋 상태 (승인 여부)

    @Column(name = "REJECTION_REASON")
    private String rejectionReason;             // 커밋 거절 이유

    @Embedded
    private LikeCount likeCount;                // 커밋 좋아요

    @Builder
    public StudyCommit(Long studyInfoId, Long studyTodoId, Long userId, String commitSHA, String message, LocalDate commitDate, CommitStatus status, String rejectionReason) {
        this.studyInfoId = studyInfoId;
        this.studyTodoId = studyTodoId;
        this.userId = userId;
        this.commitSHA = commitSHA;
        this.message = message;
        this.commitDate = commitDate;
        this.status = status;
        this.rejectionReason = rejectionReason;
        likeCount = LikeCount.createDefault();
    }

    public void approveCommit() {
        this.status = CommitStatus.COMMIT_APPROVAL;
    }

    public void rejectCommit(String rejectionReason) {
        this.status = CommitStatus.COMMIT_REJECTION;
        this.rejectionReason = rejectionReason;
    }

    public static StudyCommit of(Long userId, StudyTodo todo, GithubCommitResponse commit, CommitStatus status) {
        return StudyCommit.builder()
                .studyInfoId(todo.getStudyInfoId())
                .studyTodoId(todo.getId())
                .userId(userId)
                .commitSHA(commit.getSha())
                .message(commit.getMessage())
                .commitDate(commit.getCommitDate())
                .status(status)
                .build();
    }
}
