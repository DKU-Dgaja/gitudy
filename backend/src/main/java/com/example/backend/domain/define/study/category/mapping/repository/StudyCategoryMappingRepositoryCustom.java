package com.example.backend.domain.define.study.category.mapping.repository;

import com.example.backend.study.api.controller.info.response.CategoryResponseWithStudyId;

import java.util.List;

public interface StudyCategoryMappingRepositoryCustom {


    // studyInfoIdList를 통해 스터디들의 모든 카테고리를 조회한다.
    List<CategoryResponseWithStudyId> findCategoryListByStudyInfoListJoinCategoryMapping(List<Long> studyInfoIdList);
}
