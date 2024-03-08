package com.example.backend.domain.define.study.info.repository;

import com.example.backend.study.api.controller.info.response.MyStudyInfoListResponse;

import java.util.List;

public interface StudyInfoRepositoryCustom {

    // 정렬 기준이 있는 커서 기반 마이 스터디 페이지네이션
    List<MyStudyInfoListResponse> findMyStudyInfoListByParameter_CursorPaging(Long userId, Long cursorIdx, Long limit, String sortBy);

    // 정렬 기준이 있는 커서 기반 전체 스터디 페이지네이션
    List<MyStudyInfoListResponse> findStudyInfoListByParameter_CursorPaging(Long userId, Long cursorIdx, Long limit, String sortBy);
}
