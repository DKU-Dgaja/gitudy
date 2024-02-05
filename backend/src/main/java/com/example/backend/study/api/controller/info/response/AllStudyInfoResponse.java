package com.example.backend.study.api.controller.info.response;

import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllStudyInfoResponse {

    @JsonProperty("userId")
    private Long userId;                            // 스터디장 ID

    @JsonProperty("topic")
    private String topic;                           // 스터디 이름

    @JsonProperty("score")
    private int score = 0;
    // 스터디 활동점수

    @JsonProperty("endDate")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate endDate;                      // 스터디 종료일

    @JsonProperty("info")
    private String info;                            // 스터디 소개

    @JsonProperty("studyStatus")
    private StudyStatus status;                     // 스터디 상태


    @JsonProperty("maximumMember")
    private int maximumMember;                      // 스터디 제한 인원

    @JsonProperty("currentMember")
    private int currentMember;                      // 스터디 현재 인원


    @JsonProperty("lastCommitDay")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate lastCommitDay;                // 스터디 마지막 활동 시간

    @JsonProperty("profileImageURL")
    private String profileImageUrl;                 // 스터디 프로필 사진


    @JsonProperty("studyPeriodNone")
    private StudyPeriodType periodType;             // 스터디 커밋 규칙(주기)

    public static AllStudyInfoResponse  of(StudyInfo request) {
        return AllStudyInfoResponse.builder()
                .userId(request.getUserId())
                .topic(request.getTopic())
                .score(request.getScore())
                .endDate(request.getEndDate())
                .info(request.getInfo())
                .status(request.getStatus())
                .maximumMember(request.getMaximumMember())
                .currentMember(request.getCurrentMember())
                .lastCommitDay(request.getLastCommitDay())
                .profileImageUrl(request.getProfileImageUrl())
                .periodType(request.getPeriodType())
                .build();
    }
}
