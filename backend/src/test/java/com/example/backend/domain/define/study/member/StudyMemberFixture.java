package com.example.backend.domain.define.study.member;

import com.example.backend.domain.define.study.member.constant.StudyMemberRole;
import com.example.backend.domain.define.study.member.constant.StudyMemberStatus;

public class StudyMemberFixture {
    // 테스트용 스터디장 생성 메서드
    public static StudyMember createStudyMemberLeader(Long userId, Long studyInfoId) {
        return StudyMember.builder()
                .userId(userId)
                .studyInfoId(studyInfoId)
                .role(StudyMemberRole.STUDY_LEADER)
                .status(StudyMemberStatus.STUDY_ACTIVE)
                .build();
    }

    // 테스트용 활동중인 스터디원 생성 메서드
    public static StudyMember createDefaultStudyMember(Long userId, Long studyInfoId) {
        return StudyMember.builder()
                .userId(userId)
                .studyInfoId(studyInfoId)
                .role(StudyMemberRole.STUDY_MEMBER)
                .status(StudyMemberStatus.STUDY_ACTIVE)
                .build();
    }

    // 테스트용 강퇴당한 스터디원 생성 메서드
    public static StudyMember createStudyMemberResigned(Long userId, Long studyInfoId) {
        return StudyMember.builder()
                .userId(userId)
                .studyInfoId(studyInfoId)
                .role(StudyMemberRole.STUDY_MEMBER)
                .status(StudyMemberStatus.STUDY_RESIGNED)
                .build();
    }

    // 테스트용 비활동중인(탈퇴) 스터디원 생성 메서드
    public static StudyMember createStudyMemberWithdrawal(Long userId, Long studyInfoId) {
        return StudyMember.builder()
                .userId(userId)
                .studyInfoId(studyInfoId)
                .role(StudyMemberRole.STUDY_MEMBER)
                .status(StudyMemberStatus.STUDY_WITHDRAWAL)
                .build();
    }

    // 테스트용 스코어 활동 스터디원 생성 메서드
    public static StudyMember createStudyActiveMembersByScore(Long userId, Long studyInfoId, int score) {
        return StudyMember.builder()
                .userId(userId)
                .studyInfoId(studyInfoId)
                .score(score)
                .role(StudyMemberRole.STUDY_MEMBER)
                .status(StudyMemberStatus.STUDY_ACTIVE)
                .build();
    }

    // 테스트용 스코어 활동x 스터디원 생성 메서드
    public static StudyMember createStudyWithdrawalMembersByScore(Long userId, Long studyInfoId, int score) {
        return StudyMember.builder()
                .userId(userId)
                .studyInfoId(studyInfoId)
                .score(score)
                .role(StudyMemberRole.STUDY_MEMBER)
                .status(StudyMemberStatus.STUDY_WITHDRAWAL)
                .build();
    }

}
