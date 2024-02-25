package com.example.backend.domain.define.study.category.mapping.repository;

import com.example.backend.domain.define.study.category.mapping.StudyCategoryMapping;

import java.util.List;

public interface StudyCategoryMappingRepositoryCustom {

    // studyInfoIdList를 통해 스터디들의 모든 카테고리 매핑을 조회한다.
    List<StudyCategoryMapping> findStudyCategoryMappingListByStudyInfoIdList(List<Long> studyInfoIdList);
}
