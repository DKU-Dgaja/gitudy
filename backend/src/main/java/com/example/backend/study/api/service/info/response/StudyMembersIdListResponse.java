package com.example.backend.study.api.service.info.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyMembersIdListResponse {
    private Long studyInfoId;                   // 스터디 ID

    private Long userId;                        // 사용자 ID
}
