package com.example.backend.domain.define.study.member;

public class StudyMemberFixture {

    // 테스트용 StudyMember 생성
    public static StudyMember createStudyMember(Long studyInfoId, Long userId)
    {
        return StudyMember.builder()
                .studyInfoId(studyInfoId)
                .userId(userId)
                .build();
    }

}
