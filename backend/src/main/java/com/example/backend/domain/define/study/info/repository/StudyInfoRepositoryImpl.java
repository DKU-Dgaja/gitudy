package com.example.backend.domain.define.study.info.repository;

import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.study.api.controller.info.response.AllStudyInfoResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import static com.example.backend.domain.define.study.info.QStudyInfo.studyInfo;

@Component
@RequiredArgsConstructor
public class StudyInfoRepositoryImpl implements StudyInfoRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<StudyInfo> findStudyInfoListByUserId_OffsetPaging(Pageable pageable, Long userId) {
        // 마이 스터디 리스트를 내림차순 정렬 후 오프셋 기반 페이지 조회
        List<StudyInfo> content = queryFactory
                .selectFrom(studyInfo)
                .where(studyInfo.userId.eq(userId))
                .orderBy(studyInfo.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 마이 스터디 총 개수
        Long total = queryFactory
                .select(studyInfo.count())
                .from(studyInfo)
                .where(studyInfo.userId.eq(userId))
                .fetchOne();
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<StudyInfoResponse> findStudyInfoListByUserId_CursorPaging(Long userId, Long cursorIdx, Long limit) {
        // 마이 스터디 리스트를 내림차순 정렬 후 커서 기반 페이지 조회
        JPAQuery<StudyInfoResponse> query = queryFactory
                .select(Projections.constructor(StudyInfoResponse.class,
                        studyInfo.id,
                        studyInfo.userId,
                        studyInfo.topic,
                        studyInfo.score,
                        studyInfo.endDate,
                        studyInfo.info,
                        studyInfo.status,
                        studyInfo.maximumMember,
                        studyInfo.currentMember,
                        studyInfo.profileImageUrl,
                        studyInfo.notice,
                        studyInfo.repositoryInfo,
                        studyInfo.periodType))
                .from(studyInfo)
                .where(studyInfo.userId.eq(userId))
                .orderBy(studyInfo.id.desc());

        // cursorIdx가 null이 아닌 경우 커서 기반으로 데이터 가져오도록
        if (cursorIdx != null) {
            query = query.where(studyInfo.id.lt(cursorIdx));
        }
        return query.limit(limit).fetch();
    }

    @Override
    public List<AllStudyInfoResponse> findStudyInfoListByParameter_CursorPaging(Long userId, Long cursorIdx, Long limit, String sortBy) {
        OrderSpecifier<?> orderSpecifier;
        if (sortBy.equals("lastCommitDay")) {
            orderSpecifier = studyInfo.lastCommitDay.desc();
        } else if (sortBy.equals("score")) {
            orderSpecifier = studyInfo.score.desc();
        } else {
            // 정렬 기준이 잘못된 경우 기본적으로 score를 사용
            orderSpecifier = studyInfo.score.desc();
        }

        // 마이 스터디 리스트를 정렬 후 커서 기반 페이지 조회
        JPAQuery<AllStudyInfoResponse> query = queryFactory
                .select(Projections.constructor(AllStudyInfoResponse.class,
                        studyInfo.id,
                        studyInfo.userId,
                        studyInfo.topic,
                        studyInfo.score,
                        studyInfo.endDate,
                        studyInfo.info,
                        studyInfo.status,
                        studyInfo.maximumMember,
                        studyInfo.currentMember,
                        studyInfo.lastCommitDay,
                        studyInfo.profileImageUrl,
                        studyInfo.periodType))
                .from(studyInfo)
                .where(studyInfo.userId.eq(userId))
                .orderBy(orderSpecifier);

        // cursorIdx가 null이 아닌 경우 커서 기반으로 데이터 가져오도록
        if (cursorIdx != null) {
            if (sortBy.equals("lastCommitDay")) {
                query = query.where(studyInfo.lastCommitDay.lt(
                        JPAExpressions
                                .select(studyInfo.lastCommitDay)
                                .from(studyInfo)
                                .where(studyInfo.id.eq(cursorIdx))
                ));
            } else if (sortBy.equals("score")) {
                query = query.where(studyInfo.score.lt(
                        JPAExpressions
                                .select(studyInfo.score)
                                .from(studyInfo)
                                .where(studyInfo.id.eq(cursorIdx))
                ));
            }
        }

        return query.limit(limit).fetch();
    }

}
