package com.example.backend.study.api.controller.info.request;


import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyInfoRegisterRequest {
    @JsonProperty("userId")
    private Long userId;                            // 스터디장 ID

    @JsonProperty("topic")
    private String topic;                           // 스터디 이름

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonProperty("endDate")
    private LocalDate endDate;                      // 스터디 종료일

    @JsonProperty("info")
    private String info;                            // 스터디 소개

    @JsonProperty("studyStatus")
    private StudyStatus status;                     // 스터디 상태

    @JsonProperty("joinCode")
    private String joinCode;                        // 스터디 참여 코드

    @Min(value = 1)
    @Max(value = 10)
    @JsonProperty("maximumMember")
    private int maximumMember;                      // 스터디 제한 인원

    @JsonProperty("profileImageURL")
    private String profileImageUrl;                 // 스터디 프로필 사진

    @JsonProperty("repositoryInfo")
    private RepositoryInfo repositoryInfo;          // 연동할 깃허브 레포지토리 정보

    @JsonProperty("studyPeriodNone")
    private StudyPeriodType periodType;             // 스터디 커밋 규칙(주기)
    public static StudyInfoRegisterRequest of(StudyInfo request) {
        return StudyInfoRegisterRequest.builder()
                .userId(request.getUserId())
                .topic(request.getTopic())
                .endDate(request.getEndDate())
                .info(request.getInfo())
                .status(request.getStatus())
                .maximumMember(request.getMaximumMember())
                .profileImageUrl(request.getProfileImageUrl())
                .repositoryInfo(request.getRepositoryInfo())
                .build();
    }
}
