package com.example.backend.study.api.controller.info.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseWithStudyId {
    private Long studyInfoId;

    private String name;
}
