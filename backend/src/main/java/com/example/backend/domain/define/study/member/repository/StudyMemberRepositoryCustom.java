package com.example.backend.domain.define.study.member.repository;

public interface StudyMemberRepositoryCustom {
    // UserId와 StudyInfoId를 통해 사용자가 해당 스터디의 활동중인 멤버인지 판별한다.
    public boolean existsStudyMemberByUserIdAndStudyInfoId(Long userId, Long studyInfoId);
}
