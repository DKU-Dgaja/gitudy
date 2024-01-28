package com.example.backend.domain.define.study.info;

import com.example.backend.domain.define.BaseEntity;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
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
@Entity(name = "STUDY_INFO")
public class StudyInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDY_INFO_ID")
    private Long id;                                // 아이디

    @Column(name = "USER_ID", nullable = false)
    private Long userId;                            // 스터디장 ID

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

    @Column(name = "MAXIMUM_MEMBER")
    private int maximumMember;                      // 스터디 제한 인원

    @Column(name = "CURRENT_MEMBER")
    private int currentMember;                      // 스터디 현재 인원

    @Temporal(TemporalType.DATE)
    @Column(name = "LAST_COMMIT_DAY")
    private LocalDate lastCommitDay;                // 스터디 마지막 활동 시간

    @Column(name = "PROFILE_IMAGE_URL")
    private String profileImageUrl;                 // 스터디 프로필 사진

    @Column(name = "NOTICE")
    private String notice;                          // 스터디 공지

    @Embedded
    private RepositoryInfo repositoryInfo;          // 연동할 깃허브 레포지토리 정보

    @Enumerated(EnumType.STRING)
    @Column(name = "STUDY_PREIOD_TYPE")
    @ColumnDefault(value = "'STUDY_PERIOD_NONE'")
    private StudyPeriodType periodType;             // 스터디 커밋 규칙(주기)

    @Builder
    public StudyInfo(Long userId, String topic, int score, LocalDate endDate, String info, StudyStatus status, String joinCode, int maximumMember, int currentMember, LocalDate lastCommitDay, String profileImageUrl, String notice, RepositoryInfo repositoryInfo, StudyPeriodType periodType) {
        this.userId = userId;
        this.topic = topic;
        this.score = score;
        this.endDate = endDate;
        this.info = info;
        this.status = status;
        this.joinCode = joinCode;
        this.maximumMember = maximumMember;
        this.currentMember = currentMember;
        this.lastCommitDay = lastCommitDay;
        this.profileImageUrl = profileImageUrl;
        this.notice = notice;
        this.repositoryInfo = repositoryInfo;
        this.periodType = periodType;
    }
}
