package com.example.backend.domain.define.study.info.repository;

import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.member.constant.StudyMemberStatus;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.study.api.controller.info.response.StudyInfoListResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.example.backend.domain.define.study.info.QStudyInfo.studyInfo;
import static com.example.backend.domain.define.study.info.constant.StudyStatus.STUDY_PRIVATE;
import static com.example.backend.domain.define.study.info.constant.StudyStatus.STUDY_PUBLIC;
import static com.example.backend.domain.define.study.member.QStudyMember.studyMember;

@Component
@RequiredArgsConstructor
public class StudyInfoRepositoryImpl implements StudyInfoRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<StudyInfoListResponse> findStudyInfoListByParameter_CursorPaging(Long userId, Long cursorIdx, Long limit, String sortBy, boolean myStudy) {
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

        JPAQuery<StudyInfoListResponse> query = queryFactory
                .select(Projections.constructor(StudyInfoListResponse.class,
                        studyInfo.id,
                        studyInfo.userId,
                        studyInfo.topic,
                        studyInfo.score,
                        studyInfo.info,
                        studyInfo.status,
                        studyInfo.maximumMember,
                        studyInfo.currentMember,
                        studyInfo.lastCommitDay,
                        studyInfo.profileImageUrl,
                        studyInfo.periodType,
                        studyInfo.createdDateTime,
                        new CaseBuilder()
                                .when(studyInfo.userId.eq(userId))
                                .then(true)
                                .otherwise(false)
                                .as("isLeader")
                ))
                .from(studyInfo)
                .where(studyInfo.status.ne(StudyStatus.STUDY_DELETED));
        // myStudy에 따라서 동적으로 추가 또는 제거
        if (myStudy) {
            query.innerJoin(studyMember).on(studyMember.studyInfoId.eq(studyInfo.id))
                    .where(studyMember.userId.eq(userId)
                            .and(studyMember.status.eq(StudyMemberStatus.STUDY_ACTIVE)));
        }
        query.orderBy(orderSpecifier, idOrder); // 다중 정렬 조건 적용


        if (cursorIdx != null) {
            NumberExpression<Integer> expression;

            switch (sortBy) {
                case "lastCommitDay":
                    expression = studyInfo.lastCommitDay.year().multiply(10000)
                            .add(studyInfo.lastCommitDay.month().multiply(100))
                            .add(studyInfo.lastCommitDay.dayOfMonth());
                    break;
                case "score":
                    expression = studyInfo.score;
                    break;
                case "createdDateTime":
                default:
                    expression = studyInfo.createdDateTime.year().multiply(10000)
                            .add(studyInfo.createdDateTime.month().multiply(100))
                            .add(studyInfo.createdDateTime.dayOfMonth());
                    break;
            }

            JPQLQuery<Integer> maxFindQuery = JPAExpressions
                    .select(expression)
                    .from(studyInfo)
                    .where(studyInfo.id.eq(cursorIdx));

            query = query.where(
                    expression.loe(maxFindQuery)
                            .and(studyInfo.id.lt(cursorIdx)
                                    .or(expression.lt(maxFindQuery)))
            );
        }
        return query.limit(limit).fetch();
    }

    @Override
    public int findStudyInfoCount(Long userId, boolean myStudy) {
        JPAQuery<Long> query = queryFactory
                .select(studyInfo.count())
                .from(studyInfo)
                .where(studyInfo.status.eq(STUDY_PUBLIC)
                        .or(studyInfo.status.eq(STUDY_PRIVATE)));

        if (myStudy) {
            query.join(studyMember).on(studyMember.studyInfoId.eq(studyInfo.id))
                    .where(studyMember.userId.eq(userId)
                            .and(studyMember.status.eq(StudyMemberStatus.STUDY_ACTIVE)));
        }

        return query.fetchOne().intValue(); // 결과를 int로 변환하여 리턴
    }

    @Override
    public Optional<StudyInfo> findByRepositoryFullName(String owner, String repositoryName) {
        return Optional.ofNullable(queryFactory.selectFrom(studyInfo)
                .where(studyInfo.repositoryInfo.owner.eq(owner)
                        .and(studyInfo.repositoryInfo.name.eq(repositoryName)))
                .fetchOne());
    }

}