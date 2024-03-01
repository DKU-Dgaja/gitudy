package com.example.backend.domain.define.study.info.repository;

import com.example.backend.study.api.controller.info.response.MyStudyInfoListResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.account.user.QUser.user;
import static com.example.backend.domain.define.study.info.QStudyInfo.studyInfo;
import static com.example.backend.domain.define.study.member.QStudyMember.studyMember;

@Component
@RequiredArgsConstructor
public class StudyInfoRepositoryImpl implements StudyInfoRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    @Override
    public List<MyStudyInfoListResponse> findMyStudyInfoListByParameter_CursorPaging(Long userId, Long cursorIdx, Long limit, String sortBy) {
        OrderSpecifier<?> orderSpecifier;

        switch (sortBy) {
            case "lastCommitDay":
                orderSpecifier = studyInfo.lastCommitDay.desc();
                break;
            case "score":
                orderSpecifier = studyInfo.score.desc();
                break;
            case "createdDateTime":
            default:
                orderSpecifier = studyInfo.createdDateTime.desc(); // sortBy가 null 또는 다른 값인 경우 기본적으로 createdDateTime 내림차순으로 정렬
                break;
        }

        OrderSpecifier<Long> idOrder = studyInfo.id.desc(); // ID 내림차순으로 정렬

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
                .innerJoin(studyMember).on(studyMember.studyInfoId.eq(studyInfo.id))
                .where(studyMember.userId.eq(userId))
                .orderBy(orderSpecifier, idOrder); // 다중 정렬 조건 적용

        if (cursorIdx != null) {
            NumberExpression<Integer> maxExpression;
            NumberExpression<Integer> expression;

            switch (sortBy) {
                case "lastCommitDay":
                    maxExpression = studyInfo.lastCommitDay.max().dayOfMonth().castToNum(Integer.class);
                    expression = studyInfo.lastCommitDay.dayOfMonth().castToNum(Integer.class);
                    break;
                case "score":
                    maxExpression = studyInfo.score.max();
                    expression = studyInfo.score;
                    break;
                case "createdDateTime":
                default:
                    maxExpression = studyInfo.createdDateTime.max().dayOfMonth().castToNum(Integer.class);
                    expression = studyInfo.createdDateTime.dayOfMonth().castToNum(Integer.class);
                    break;
            }
            query = query.where(
                    expression.loe(
                            JPAExpressions
                                    .select(maxExpression)
                                    .from(studyInfo)
                                    .where(studyInfo.id.eq(cursorIdx))
                    ).and(studyInfo.id.lt(cursorIdx)
                            .or(expression.lt(
                                    JPAExpressions
                                            .select(maxExpression)
                                            .from(studyInfo)
                                            .where(studyInfo.id.eq(cursorIdx)))))
            );
        }
        return query.limit(limit).fetch();
    }
}