package com.example.backend.domain.define.study.commit.repository;

import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.account.user.QUser.user;
import static com.example.backend.domain.define.study.commit.QStudyCommit.studyCommit;
import static com.example.backend.domain.define.study.info.QStudyInfo.studyInfo;

@Component
@RequiredArgsConstructor
public class StudyCommitRepositoryImpl implements StudyCommitRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<StudyCommit> findStudyCommitListByUserId_OffsetPaging(Pageable pageable, Long userId) {
        // 마이 커밋 리스트를 내림차순 정렬 후 오프셋 기반 페이지 조회
        List<StudyCommit> content = queryFactory
                .selectFrom(studyCommit)
                .where(studyCommit.userId.eq(userId))
                .orderBy(studyCommit.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 마이 커밋 총 개수
        Long total = queryFactory
                .select(studyCommit.count())
                .from(studyCommit)
                .where(studyCommit.userId.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<CommitInfoResponse> findStudyCommitListByUserId_CursorPaging(Pageable pageable, Long userId, Long cursorIdx) {

        JPAQuery<CommitInfoResponse> query = queryFactory
                .select(Projections.constructor(CommitInfoResponse.class,
                        studyCommit.id,
                        studyCommit.studyInfoId,
                        studyCommit.userId,
                        studyCommit.commitSHA,
                        studyCommit.message,
                        studyCommit.commitDate,
                        studyCommit.status,
                        studyCommit.rejectionReason,
                        studyCommit.likeCount))
                .from(studyCommit)
                .where(studyCommit.userId.eq(userId))
                .orderBy(studyCommit.id.desc());

        // cursorIdx가 null이 아닌 경우 커서 기반으로 데이터 가져오도록
        if (cursorIdx != null) {
            query = query.where(studyCommit.id.lt(cursorIdx));
        }

        // pageSize만큼 가져오기
        List<CommitInfoResponse> content = query
                .limit(pageable.getPageSize())
                .fetch();

        // 마이 커밋 총 개수
        Long total = queryFactory
                .select(studyCommit.count())
                .from(studyCommit)
                .where(studyCommit.userId.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
