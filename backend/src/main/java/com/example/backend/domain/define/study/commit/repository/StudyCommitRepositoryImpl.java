package com.example.backend.domain.define.study.commit.repository;

import com.example.backend.domain.define.study.commit.StudyCommit;
import com.querydsl.core.QueryResults;
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
    public Page<StudyCommit> findStudyCommitListByUserId_CursorPaging(Pageable pageable, Long userId, Long cursorIdx) {
        // 마이 커밋 리스트를 내림차순 정렬 후 커서 기반 페이지 조회
        List<StudyCommit> content = queryFactory
                .selectFrom(studyCommit)
                .where(studyCommit.userId.eq(userId)
                        .and(studyCommit.id.lt(cursorIdx)))
                .orderBy(studyCommit.id.desc())
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
