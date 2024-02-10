package com.example.backend.study.api.controller.info.response;

import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyInfoResponse {

    private Long id;                                // 아이디

    @JsonProperty("userId")
    private Long userId;                            // 스터디장 ID

    @JsonProperty("topic")
    private String topic;                           // 스터디 이름

    @JsonProperty("score")
    private int score = 0;                          // 스터디 활동점수

    @JsonProperty("endDate")
    private LocalDate endDate;                      // 스터디 종료일

    @JsonProperty("info")
    private String info;                            // 스터디 소개

    @JsonProperty("studyStatus")
    private StudyStatus status;                     // 스터디 상태

    @JsonProperty("maximumMember")
    private int maximumMember;                      // 스터디 제한 인원

    @JsonProperty("currentMember")
    @Column(name = "CURRENT_MEMBER")
    private int currentMember;                      // 스터디 현재 인원

    @JsonProperty("profileImageURL")
    private String profileImageUrl;                 // 스터디 프로필 사진

    @JsonProperty("notice")
    private String notice;                          // 스터디 공지

    @JsonProperty("repositoryInfo")
    private RepositoryInfo repositoryInfo;          // 연동할 깃허브 레포지토리 정보

    @JsonProperty("studyPeriodNone")
    private StudyPeriodType periodType;             // 스터디 커밋 규칙(주기)

    public static StudyInfoResponse of(StudyInfo request) {
        return StudyInfoResponse.builder()
                .userId(request.getUserId())
                .topic(request.getTopic())
                .score(request.getScore())
                .endDate(request.getEndDate())
                .info(request.getInfo())
                .status(request.getStatus())
                .maximumMember(request.getMaximumMember())
                .currentMember(request.getCurrentMember())
                .profileImageUrl(request.getProfileImageUrl())
                .notice(request.getNotice())
                .repositoryInfo(RepositoryInfo.builder()
                        .owner(request.getRepositoryInfo().getOwner())
                        .name(request.getRepositoryInfo().getName()).
                        branchName(request.getRepositoryInfo().getBranchName())
                        .build())
                .periodType(request.getPeriodType())
                .build();
    }
}
