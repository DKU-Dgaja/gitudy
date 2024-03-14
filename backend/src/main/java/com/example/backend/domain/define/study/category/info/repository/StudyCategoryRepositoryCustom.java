package com.example.backend.domain.define.study.category.info.repository;

import java.util.List;

public interface StudyCategoryRepositoryCustom {
    // studyInfoId를 통해 카테고리들의 모든 이름을 조회한다.
    List<String> findCategoryNameListByStudyInfoJoinCategoryMapping(Long studyInfoId);
}
