package com.example.backend.domain.define.study.category.info.repository;

import com.example.backend.domain.define.study.category.info.StudyCategory;

import java.util.List;

public interface StudyCategoryRepositoryCustom {

    // 카테고리 Id 리스트를 통해 스터디들의 모든 카테고리을 조회한다.
    List<StudyCategory> findStudyCategoryListByCategoryIdList(List<Long> categoryIdList);
}
