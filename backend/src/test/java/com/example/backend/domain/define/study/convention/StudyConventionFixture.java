package com.example.backend.domain.define.study.convention;

import com.example.backend.study.api.controller.convention.request.StudyConventionRequest;
import com.example.backend.study.api.controller.convention.request.StudyConventionUpdateRequest;

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

    // 기본 컨벤션 등록
    public static StudyConvention createDefaultConvention(Long studyInfoId) {
        return StudyConvention.builder()
                .studyInfoId(studyInfoId)
                .name("default convention")
                .content("^[a-zA-Z0-9]{6} .*")
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

    // 비활성화된 Convention
    public static StudyConvention createNonActiveConvention(Long studyInfoId) {
        return StudyConvention.builder()
                .studyInfoId(studyInfoId)
                .name("이름")
                .description("설명")
                .content("정규식")
                .isActive(false)
                .build();
    }
}
