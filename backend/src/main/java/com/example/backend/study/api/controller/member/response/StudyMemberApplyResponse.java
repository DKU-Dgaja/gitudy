package com.example.backend.study.api.controller.member.response;


import com.example.backend.domain.define.account.user.SocialInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class StudyMemberApplyResponse {

    private Long id;  // 스터디 member id

    private String signGreeting; // 스터디장에게 한마디

    private Long userId;   // userId

    private String name;  // 이름

    private String githubId; // 깃허브Id

    private SocialInfo socialInfo;  // 소셜 정보

    private String profileImageUrl;  // 프로필 이미지

    private int score;  // 개인 활동점수

    private int point;  // 포인트

    private boolean profilePublicYn; // 프로필 공개 여무

    private LocalDateTime createdDateTime; // 가입 신청 시간

}
