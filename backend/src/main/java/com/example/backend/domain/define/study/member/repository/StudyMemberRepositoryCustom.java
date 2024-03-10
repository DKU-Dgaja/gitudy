package com.example.backend.domain.define.study.member.repository;

import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.study.api.controller.info.response.StudyMemberWithUserInfoResponse;
import com.example.backend.study.api.controller.member.response.StudyMembersResponse;

import java.util.List;

public interface StudyMemberRepositoryCustom {
    // UserId와 StudyInfoId를 통해 사용자가 해당 스터디의 활동중인 멤버인지 판별한다.
    public boolean existsStudyMemberByUserIdAndStudyInfoId(Long userId, Long studyInfoId);

    // UserId와 StudyInfoId를 통해 사용자가 해당 스터디의 스터디장인지 판별한다.
    public boolean isStudyLeaderByUserIdAndStudyInfoId(Long userId, Long studyInfoId);

    // StudyInfoId를 통해 스터디의 모든 멤버들 중에 활동중인 멤버를 조회한다.
    public List<StudyMember> findActiveMembersByStudyInfoId(Long studyInfoId);
  
    // StudyInfoId와 orderByScore 를 통해 스터디의 모든 멤버들을 기여도별 or 가입순 정렬하여 조회한다.
    public List<StudyMembersResponse> findStudyMembersByStudyInfoIdOrderByScore(Long studyInfoId, boolean orderByScore);

    // studyInfoIdList를 통해 스터디들의 모든 멤버를 조회한다.
    List<StudyMemberWithUserInfoResponse> findStudyMemberListByStudyInfoListJoinUserInfo(List<Long> studyInfoIdList);
}
