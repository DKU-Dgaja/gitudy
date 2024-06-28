package com.example.backend.domain.define.study.convention.repository;

import com.example.backend.study.api.controller.convention.response.StudyConventionResponse;

import java.util.List;

public interface StudyConventionRepositoryCustom {

    // StudyInfoId로 Convention 전체 가져오기
    List<StudyConventionResponse> findStudyConventionListByStudyInfoId_CursorPaging(Long studyInfoId, Long cursorIdx, Long limit);

    // StudyInfoId를 통해 활성화된 Convention 가져오기
    StudyConventionResponse findActiveConventionByStudyInId(Long studyInfoId);
}
