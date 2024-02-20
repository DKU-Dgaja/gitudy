package com.example.backend.domain.define.study.info.repository;

import com.example.backend.study.api.controller.info.response.MyStudyInfoListResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.study.info.QStudyInfo.studyInfo;

@Component
@RequiredArgsConstructor
public class StudyInfoRepositoryImpl implements StudyInfoRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    @Override
    public List<MyStudyInfoListResponse> findMyStudyInfoListByParameter_CursorPaging(Long userId, Long cursorIdx, Long limit, String sortBy) {
        OrderSpecifier<?> orderSpecifier;
        BooleanExpression cursorPredicate = null;

        if (sortBy.equals("lastCommitDay")) {
            orderSpecifier = studyInfo.lastCommitDay.desc();
            if (cursorIdx != null) {
                cursorPredicate = studyInfo.lastCommitDay.loe(JPAExpressions.select(studyInfo.lastCommitDay.max()).from(studyInfo).where(studyInfo.id.eq(cursorIdx)));
            }
        } else if (sortBy.equals("score")) {
            orderSpecifier = studyInfo.score.desc();
            if (cursorIdx != null) {
                cursorPredicate = studyInfo.score.loe(JPAExpressions.select(studyInfo.score.max()).from(studyInfo).where(studyInfo.id.eq(cursorIdx)));
            }
        } else if (sortBy.equals("createdDateTime")) {
            orderSpecifier = studyInfo.createdDateTime.desc();
            if (cursorIdx != null) {
                cursorPredicate = studyInfo.createdDateTime.loe(JPAExpressions.select(studyInfo.createdDateTime.max()).from(studyInfo).where(studyInfo.id.eq(cursorIdx)));
            }
        } else {
            orderSpecifier = studyInfo.createdDateTime.desc(); // 기본적으로 createdDateTime 내림차순으로 정렬
        }

        // ID 내림차순으로 정렬
        OrderSpecifier<Long> idOrder = studyInfo.id.desc();

        JPAQuery<MyStudyInfoListResponse> query = queryFactory
                .select(Projections.constructor(MyStudyInfoListResponse.class,
                        studyInfo.id,
                        studyInfo.userId,
                        studyInfo.topic,
                        studyInfo.score,
                        studyInfo.info,
                        studyInfo.maximumMember,
                        studyInfo.currentMember,
                        studyInfo.lastCommitDay,
                        studyInfo.profileImageUrl,
                        studyInfo.periodType,
                        studyInfo.createdDateTime))
                .from(studyInfo)
                .where(studyInfo.userId.eq(userId))
                .orderBy(orderSpecifier, idOrder);

        // cursorPredicate가 null이 아닌 경우 커서 기반으로 데이터 가져오도록
        if (cursorIdx != null && cursorPredicate != null) {
            query = query.where(
                    cursorPredicate.and(
                            studyInfo.id.lt(cursorIdx).or(
                                    studyInfo.lastCommitDay.lt(JPAExpressions.select(studyInfo.lastCommitDay.max()).from(studyInfo).where(studyInfo.id.eq(cursorIdx)))
                            )
                    )
            );
        }

        return query.limit(limit).fetch();
    }

}