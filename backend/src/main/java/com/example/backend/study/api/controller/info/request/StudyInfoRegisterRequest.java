package com.example.backend.study.api.controller.info.request;


import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class StudyInfoRegisterRequest {
    private Long userId;                            // 스터디장 ID

    private String topic;                           // 스터디 이름

    private String info;                            // 스터디 소개

    private StudyStatus status;                     // 스터디 상태

    @Min(value = 1)
    @Max(value = 10)
    private int maximumMember;                      // 스터디 제한 인원

    private String profileImageUrl;                 // 스터디 프로필 사진

    private String branchName;                      // 브랜치 이름

    private StudyPeriodType periodType;             // 스터디 커밋 규칙(주기)

    private List<Long> categoriesId;                // 카테고리 ID 리스트
    public static StudyInfoRegisterRequest of(StudyInfo request) {
        return StudyInfoRegisterRequest.builder()
                .userId(request.getUserId())
                .topic(request.getTopic())
                .info(request.getInfo())
                .status(request.getStatus())
                .maximumMember(request.getMaximumMember())
                .profileImageUrl(request.getProfileImageUrl())
                .branchName(request.getRepositoryInfo().getBranchName())
                .build();
    }
}
