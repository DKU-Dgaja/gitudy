package com.example.backend.study.api.controller.info.response;

import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.study.api.service.info.response.UserNameAndProfileImageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyInfoListWithMemberResponse {
    private Long id;                                // 아이디

    private Long userId;                            // 스터디장 ID

    private String topic;                           // 스터디 이름

    private int score;                              // 스터디 활동점수

    private String info;                            // 스터디 소개

    private StudyStatus status;                     // 스터디 상태

    private int maximumMember;                      // 스터디 제한 인원

    private int currentMember;                      // 스터디 현재 인원

    private LocalDate lastCommitDay;                // 스터디 마지막 활동 시간

    private String profileImageUrl;                 // 스터디 프로필 사진

    private StudyPeriodType periodType;             // 스터디 커밋 규칙(주기)

    private LocalDateTime createdDateTime;           // 스터디 개설 시간

    private Boolean isLeader;                       // 스터디 리더인지

    private List<UserNameAndProfileImageResponse> userInfo; // 유저 정보

    public static StudyInfoListWithMemberResponse from(StudyInfoListResponse response, List<UserNameAndProfileImageResponse> userInfo) {
        return StudyInfoListWithMemberResponse.builder()
                .id(response.getId())
                .userId(response.getUserId())
                .topic(response.getTopic())
                .score(response.getScore())
                .info(response.getInfo())
                .status(response.getStatus())
                .maximumMember(response.getMaximumMember())
                .currentMember(response.getCurrentMember())
                .lastCommitDay(response.getLastCommitDay())
                .profileImageUrl(response.getProfileImageUrl())
                .periodType(response.getPeriodType())
                .createdDateTime(response.getCreatedDateTime())
                .isLeader(response.getIsLeader())
                .userInfo(userInfo)
                .build();
    }
}
