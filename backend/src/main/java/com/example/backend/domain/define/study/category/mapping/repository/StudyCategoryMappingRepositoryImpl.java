package com.example.backend.domain.define.study.category.mapping.repository;

import com.example.backend.study.api.controller.info.response.CategoryResponseWithStudyId;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.study.category.info.QStudyCategory.studyCategory;
import static com.example.backend.domain.define.study.category.mapping.QStudyCategoryMapping.studyCategoryMapping;

@Component
@RequiredArgsConstructor
public class StudyCategoryMappingRepositoryImpl implements StudyCategoryMappingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CategoryResponseWithStudyId> findCategoryListByStudyInfoListJoinCategoryMapping(List<Long> studyInfoIdList) {
        JPAQuery<CategoryResponseWithStudyId> query = queryFactory
                .select(Projections.constructor(
                        CategoryResponseWithStudyId.class,
                        studyCategoryMapping.studyInfoId,
                        studyCategory.name
                ))
                .from(studyCategory)
                .join(studyCategoryMapping).on(studyCategory.id.eq(studyCategoryMapping.studyCategoryId))
                .where(studyCategoryMapping.studyInfoId.in(studyInfoIdList))
                .orderBy(studyCategory.id.desc());
        return query.fetch();
    }
}
