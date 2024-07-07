package com.example.backend.domain.define.study.category.info.repository;

import com.example.backend.study.api.service.category.info.response.CategoryResponse;
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

    @Override
    public List<CategoryResponse> findCategoryListByStudyInfoIdJoinCategoryMapping(Long studyInfoId, Long cursorIdx, Long limit) {
        JPAQuery<CategoryResponse> query = queryFactory
                .select(Projections.constructor(
                        CategoryResponse.class,
                        studyCategory.id,
                        studyCategory.name)
                )
                .from(studyCategory)
                .join(studyCategoryMapping).on(studyCategory.id.eq(studyCategoryMapping.studyCategoryId))
                .where(studyCategoryMapping.studyInfoId.eq(studyInfoId))
                .orderBy(studyCategory.id.desc());

        // cursorIdx가 null이 아닌 경우 커서 기반으로 데이터 가져오도록
        if (cursorIdx != null) {
            query = query.where(studyCategory.id.lt(cursorIdx));
        }

        // 커서 다음부터 limit만큼 가져오기
        return query
                .limit(limit)
                .fetch();
    }
}
