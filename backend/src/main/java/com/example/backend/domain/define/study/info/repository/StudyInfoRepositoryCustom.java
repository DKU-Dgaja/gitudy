package com.example.backend.domain.define.study.info.repository;

import com.example.backend.study.api.controller.info.response.StudyInfoListResponse;

import java.util.List;

public interface StudyInfoRepositoryCustom {

    // 정렬 기준이 있는 커서 기반 스터디 페이지네이션
    List<StudyInfoListResponse> findStudyInfoListByParameter_CursorPaging(Long userId, Long cursorIdx, Long limit, String sortBy, boolean myStudy);

    // 마이/전체 스터디 개수 조회
    int findStudyInfoCount(Long userId, boolean myStudy);
}
