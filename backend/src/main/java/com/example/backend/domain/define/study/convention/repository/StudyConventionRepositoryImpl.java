package com.example.backend.domain.define.study.convention.repository;


import com.example.backend.study.api.controller.convention.response.StudyConventionResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.study.convention.QStudyConvention.studyConvention;

@Component
@RequiredArgsConstructor
public class StudyConventionRepositoryImpl implements StudyConventionRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<StudyConventionResponse> findStudyConventionListByStudyInfoId_CursorPaging(Long studyInfoId, Long cursorIdx, Long limit) {

        JPAQuery<StudyConventionResponse> query = queryFactory
                .select(Projections.constructor(StudyConventionResponse.class,
                        studyConvention.id,
                        studyConvention.studyInfoId,
                        studyConvention.name,
                        studyConvention.description,
                        studyConvention.content,
                        studyConvention.isActive))
                .from(studyConvention)
                .where(studyConvention.studyInfoId.eq(studyInfoId))
                .orderBy(studyConvention.id.desc()); // 컨벤션 Id가 큰값부터 내림차순 (최신항목순)

        if (cursorIdx != null) {
            query = query.where(studyConvention.id.lt(cursorIdx));
        }

        return query.limit(limit)
                .fetch();
    }

    @Override
    public StudyConventionResponse findActiveConventionByStudyInId(Long studyInfoId) {

        return queryFactory.select(Projections.constructor(StudyConventionResponse.class,
                        studyConvention.id,
                        studyConvention.studyInfoId,
                        studyConvention.name,
                        studyConvention.description,
                        studyConvention.content,
                        studyConvention.isActive))
                .from(studyConvention)
                .where(studyConvention.studyInfoId.eq(studyInfoId)
                        .and(studyConvention.isActive))
                .fetchFirst();
    }

}
