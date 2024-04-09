package com.example.backend.domain.define.study.category.info.repository;

import com.example.backend.study.api.service.category.info.response.CategoryResponse;

import java.util.List;

public interface StudyCategoryRepositoryCustom {
    // studyInfoId를 통해 카테고리들의 모든 이름을 조회한다.
    List<String> findCategoryNameListByStudyInfoJoinCategoryMapping(Long studyInfoId);

    // studyInfoId를 통해 카테고리 리스트 조회 - 커서기반 페이지네이션
    List<CategoryResponse> findCategoryListByStudyInfoIdJoinCategoryMapping(Long studyInfoId, Long cursorIdx, Long limit);
}
