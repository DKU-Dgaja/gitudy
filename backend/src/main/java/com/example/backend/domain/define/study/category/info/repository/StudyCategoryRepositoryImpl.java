package com.example.backend.domain.define.study.category.info.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.study.category.info.QStudyCategory.studyCategory;
import static com.example.backend.domain.define.study.category.mapping.QStudyCategoryMapping.studyCategoryMapping;

@Component
@RequiredArgsConstructor
public class StudyCategoryRepositoryImpl implements StudyCategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findCategoryNameListByStudyInfoJoinCategoryMapping(Long studyInfoId) {
        return queryFactory.select(studyCategory.name)
                .from(studyCategory)
                .join(studyCategoryMapping).on(studyCategory.id.eq(studyCategoryMapping.studyCategoryId))
                .where(studyCategoryMapping.studyInfoId.eq(studyInfoId))
                .fetch();
    }
}
