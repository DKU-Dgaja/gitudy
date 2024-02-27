package com.example.backend.study.api.controller.member.response;


import com.example.backend.domain.define.study.member.constant.StudyMemberRole;
import com.example.backend.domain.define.study.member.constant.StudyMemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyMembersResponse {

    private Long userId;  // 사용자 Id

    private StudyMemberRole role; // 스터디 구성원 역할

    private StudyMemberStatus status; // 스터디 구성원 상태

    private  int score; // 기여도

}
