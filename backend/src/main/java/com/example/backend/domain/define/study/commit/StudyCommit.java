package com.example.backend.domain.define.study.commit;

import com.example.backend.domain.define.BaseEntity;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
import com.example.backend.domain.define.study.info.StudyInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STUDY_COMMIT")
public class StudyCommit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDY_COMMIT_ID")
    private Long id;                            // 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_INFO_ID", nullable = false)
    private StudyInfo studyInfo;                // 속한 스터디 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;                          // 소유자 정보

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

    @Builder
    public StudyCommit(StudyInfo studyInfo, User user, String commitSHA, String message, LocalDate commitDate, CommitStatus status, String rejectionReason) {
        this.studyInfo = studyInfo;
        this.user = user;
        this.commitSHA = commitSHA;
        this.message = message;
        this.commitDate = commitDate;
        this.status = status;
        this.rejectionReason = rejectionReason;
    }
}
