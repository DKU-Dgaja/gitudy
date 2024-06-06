package com.example.backend.domain.define.study.member;

import com.example.backend.domain.define.BaseEntity;
import com.example.backend.domain.define.study.member.constant.StudyMemberRole;
import com.example.backend.domain.define.study.member.constant.StudyMemberStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "STUDY_MEMBER")
public class StudyMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STUDY_MEMBER_ID")
    private Long id;                            // 아이디

    @Column(name = "STUDY_INFO_ID", nullable = false)
    private Long studyInfoId;                   // 스터디 ID

    @Column(name = "USER_ID", nullable = false)
    private Long userId;                        // 사용자 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "STUDY_MEMBER_ROLE")
    @ColumnDefault(value = "'STUDY_MEMBER'")
    private StudyMemberRole role;               // 스터디 구성원 역할

    @Enumerated(EnumType.STRING)
    @Column(name = "STUDY_MEMBER_STATUS")
    @ColumnDefault(value = "'STUDY_WAITING'")
    private StudyMemberStatus status;           // 스터디 구성원 상태

    @Column(name = "SCORE")
    private int score = 0;                      // 기여도 (활동점수)

    @Column(name = "SIGN_GREETING")
    private String signGreeting;               // 가입 한마디

    @Builder
    public StudyMember(Long studyInfoId, Long userId, StudyMemberRole role, StudyMemberStatus status, int score, String signGreeting) {
        this.studyInfoId = studyInfoId;
        this.userId = userId;
        this.role = role;
        this.status = status;
        this.score = score;
        this.signGreeting = signGreeting;
    }
    public void updateWithdrawalStudyMember(){
        this.status=StudyMemberStatus.STUDY_WITHDRAWAL;
    }

    // 스터디 멤버 상태 업데이트
    public void updateStudyMemberStatus(StudyMemberStatus status) {
        this.status = status;
    }

    // 승인 대기중 유저 생성 메서드
    public static StudyMember waitingStudyMember(Long studyInfoId, Long userId, String signGreeting) {
        return StudyMember.builder()
                .studyInfoId(studyInfoId)
                .userId(userId)
                .role(StudyMemberRole.STUDY_MEMBER)
                .status(StudyMemberStatus.STUDY_WAITING)
                .score(0)
                .signGreeting(signGreeting)
                .build();
    }

    public void updateSignGreeting(String signGreeting) {
        this.signGreeting = signGreeting;
    }
}
