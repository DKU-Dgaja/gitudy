package com.example.backend.study.api.controller.info.response;

import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyInfoRegisterResponse {
    private Long userId;                            // 스터디장 ID

    private String topic;                           // 스터디 이름

    private LocalDate endDate;                      // 스터디 종료일

    private String info;                            // 스터디 소개

    private StudyStatus status;                     // 스터디 상태

    @Size(max = 10)
    private String joinCode;                        // 스터디 참여 코드

    private int maximumMember;                      // 스터디 제한 인원

    private String profileImageUrl;                 // 스터디 프로필 사진

    private RepositoryInfo repositoryInfo;          // 연동할 깃허브 레포지토리 정보

    private StudyPeriodType periodType;             // 스터디 커밋 규칙(주기)

    private List<Long> categoriesId;              // 카테고리 ID 리스트

    public static StudyInfoRegisterResponse of(StudyInfo response, List<Long> categoriesId) {
        return StudyInfoRegisterResponse.builder()
                .userId(response.getUserId())
                .topic(response.getTopic())
                .endDate(response.getEndDate())
                .info(response.getInfo())
                .status(response.getStatus())
                .joinCode(response.getJoinCode())
                .maximumMember(response.getMaximumMember())
                .profileImageUrl(response.getProfileImageUrl())
                .repositoryInfo(response.getRepositoryInfo())
                .periodType(response.getPeriodType())
                .categoriesId(categoriesId)
                .build();
    }
}