package com.example.backend.study.api.service.info.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyCategoryMappingListResponse {
    private Long studyInfoId;                   // 스터디 ID
    private Long studyCategoryId;               // 카테고리 ID
}
