package com.example.backend.domain.define.study.commit.repository;

import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.study.commit.QStudyCommit.studyCommit;

@Component
@RequiredArgsConstructor
public class StudyCommitRepositoryImpl implements StudyCommitRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    @Override
    public List<CommitInfoResponse> findStudyCommitListByUserId_CursorPaging(Long userId, Long cursorIdx, Long limit) {

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

        // 커서 다음부터 limit만큼 가져오기
        return query
                .limit(limit)
                .fetch();
    }
}
