package com.example.backend.domain.define.study.convention;

import com.example.backend.study.api.controller.convention.request.StudyConventionRequest;
import com.example.backend.study.api.controller.convention.request.StudyConventionUpdateRequest;

import java.util.List;

public class StudyConventionFixture {

    // 테스트용 컨벤션 등록
    public static StudyConventionRequest generateStudyConventionRequest() {
        return StudyConventionRequest.builder()
                .name("컨벤션")
                .description("설명")
                .content("정규식")
                .active(true)
                .build();
    }

    // 테스트용 컨벤션 수정
    public static StudyConventionUpdateRequest generateStudyConventionUpdateRequest() {
        return StudyConventionUpdateRequest.builder()
                .name("컨벤션 수정")
                .description("설명 수정")
                .content("정규식 수정")
                .active(true)
                .build();
    }

    // 테스트용 StudyConvention 생성
    public static StudyConvention createStudyDefaultConvention(Long studyInfoId) {
        return StudyConvention.builder()
                .studyInfoId(studyInfoId)
                .name("컨벤션")
                .description("설명")
                .content("정규식")
                .isActive(true)
                .build();
    }

    // 테스트용 StudyConvention
    public static StudyConvention createStudyConventionName(Long studyInfoId, String name) {
        return StudyConvention.builder()
                .studyInfoId(studyInfoId)
                .name(name)
                .description("설명")
                .content("정규식")
                .isActive(true)
                .build();
    }

}
