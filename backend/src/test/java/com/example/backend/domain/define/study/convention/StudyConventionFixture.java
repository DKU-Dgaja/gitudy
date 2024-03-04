package com.example.backend.domain.define.study.convention;

import com.example.backend.study.api.controller.convention.request.StudyConventionRequest;

public class StudyConventionFixture {

    // 테스트용 컨벤션 등록
    public static StudyConventionRequest generateStudyConventionRequest(Long studyInfoId) {
        return StudyConventionRequest.builder()
                .studyInfoId(studyInfoId)
                .name("컨벤션")
                .description("설명")
                .content("정규식")
                .active(true)
                .build();
    }

}
