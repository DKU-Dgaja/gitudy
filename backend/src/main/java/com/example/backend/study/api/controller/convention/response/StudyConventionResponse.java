package com.example.backend.study.api.controller.convention.response;


import com.example.backend.domain.define.study.convention.StudyConvention;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class StudyConventionResponse {

    private Long conventionId; // 컨벤션 Id

    private Long studyInfoId;  // 스터디 Id

    private String name;   // 컨벤션 이름

    private String description; // 컨벤션 설명

    private String content;  // 컨벤션 내용(정규식)

    private boolean active; // 컨벤션 적용 여부


    public static StudyConventionResponse of(StudyConvention studyConvention) {
        return StudyConventionResponse.builder()
                .conventionId(studyConvention.getId())
                .studyInfoId(studyConvention.getStudyInfoId())
                .name(studyConvention.getName())
                .description(studyConvention.getDescription())
                .content(studyConvention.getContent())
                .active(studyConvention.isActive())
                .build();
    }

}