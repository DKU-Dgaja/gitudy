package com.example.backend.study.api.controller.info.response;

import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyInfoListResponse {
    private Long id;                                // 아이디

    private Long userId;                            // 스터디장 ID

    private String topic;                           // 스터디 이름

    private int score;                              // 스터디 활동점수

    private String info;                            // 스터디 소개

    private int maximumMember;                      // 스터디 제한 인원

    private int currentMember;                      // 스터디 현재 인원

    private LocalDate lastCommitDay;                // 스터디 마지막 활동 시간

    private String profileImageUrl;                 // 스터디 프로필 사진

    private StudyPeriodType periodType;             // 스터디 커밋 규칙(주기)

    private LocalDateTime createdDateTime;           // 스터디 개설 시간

    private Boolean isLeader;                       // 스터디 리더인지
}