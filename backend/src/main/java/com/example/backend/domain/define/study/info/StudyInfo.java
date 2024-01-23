package com.example.backend.domain.define.study.info;

import com.example.backend.domain.define.BaseEntity;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STUDY_INFO")
public class StudyInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDY_INFO_ID")
    private Long id;                                // 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;                            // 스터디장 정보

    @Column(name = "TOPIC", nullable = false)
    private String topic;                           // 스터디 이름

    @Column(name = "SCORE")
    private int score = 0;                          // 스터디 활동점수

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    private LocalDate endDate;                      // 스터디 종료일

    @Column(name = "INFO", length = 512)
    private String info;                            // 스터디 소개

    @Enumerated(EnumType.STRING)
    @Column(name = "STUDY_STATUS")
    @ColumnDefault(value = "'STUDY_PUBLIC'")
    private StudyStatus status;                     // 스터디 상태

    @Column(name = "JOIN_CODE")
    private String joinCode;                        // 스터디 참여 코드

    @Column(name = "CAPACITY")
    private int capacity;                           // 스터디 제한 인원

    @Column(name = "REPOSITORY_URL")
    private String repositoryUrl;                   // 연동할 깃허브 레포지토리 주소

    @Enumerated(EnumType.STRING)
    @Column(name = "STUDY_PREIOD_TYPE")
    @ColumnDefault(value = "'STUDY_PERIOD_NONE'")
    private StudyPeriodType periodType;             // 스터디 커밋 규칙(주기)

    @Builder
    public StudyInfo(User user, String topic, int score, LocalDate endDate, String info, StudyStatus status, String joinCode, int capacity, String repositoryUrl, StudyPeriodType periodType) {
        this.user = user;
        this.topic = topic;
        this.score = score;
        this.endDate = endDate;
        this.info = info;
        this.status = status;
        this.joinCode = joinCode;
        this.capacity = capacity;
        this.repositoryUrl = repositoryUrl;
        this.periodType = periodType;
    }
}
