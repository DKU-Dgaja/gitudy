package com.example.backend.domain.define.study.member.repository;

import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.study.api.controller.info.response.StudyMemberWithUserInfoResponse;
import com.example.backend.study.api.controller.member.response.StudyMemberApplyResponse;
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

    // UserId와 StudyInfoId를 통해 사용자가 해당 스터디의 강퇴자 였는지 판별한다.
    public boolean isResignedStudyMemberByUserIdAndStudyInfoId(Long userId, Long studyInfoId);

    // UserId와 StudyInfoId를 통해 사용자가 해당 스터디에 이미 가입 신청했는지 판별한다.
    public boolean isWaitingStudyMemberByUserIdAndStudyInfoId(Long userId, Long studyInfoId);

    // StudyInfoId를 통해 승인 대기중인 멤버들의 가입신청 목록을 가져온다.
    List<StudyMemberApplyResponse> findStudyApplyListByStudyInfoId_CursorPaging(Long studyInfoId, Long cursorIdx, Long limit);
}
