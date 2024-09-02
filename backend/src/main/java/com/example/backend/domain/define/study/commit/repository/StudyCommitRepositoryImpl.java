package com.example.backend.domain.define.study.commit.repository;

import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.example.backend.study.api.service.github.response.GithubCommitResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.account.user.QUser.user;
import static com.example.backend.domain.define.study.commit.QStudyCommit.studyCommit;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyCommitRepositoryImpl implements StudyCommitRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommitInfoResponse> findStudyCommitListByUserId_CursorPaging(Long userId, Long studyId, Long cursorIdx, Long limit) {

        JPAQuery<CommitInfoResponse> query = queryFactory
                .select(Projections.constructor(CommitInfoResponse.class,
                        studyCommit.id,
                        studyCommit.studyInfoId,
                        studyCommit.studyTodoId,
                        studyCommit.userId,
                        studyCommit.commitSHA,
                        studyCommit.message,
                        studyCommit.commitDate,
                        studyCommit.status,
                        studyCommit.rejectionReason,
                        studyCommit.likeCount,
                        user.name,
                        user.profileImageUrl))
                .from(studyCommit)
                .leftJoin(user).on(studyCommit.userId.eq(user.id))
                .where(studyCommit.userId.eq(userId))
                .orderBy(studyCommit.id.desc());

        // cursorIdx가 null이 아닌 경우 커서 기반으로 데이터 가져오도록
        if (cursorIdx != null) {
            query = query.where(studyCommit.id.lt(cursorIdx));
        }

        // 해당하는 studyId로 필터링
        if (studyId != null) {
            query.where(studyCommit.studyInfoId.eq(studyId));
        }

        // 커서 다음부터 limit만큼 가져오기
        return query
                .limit(limit)
                .fetch();
    }

    @Override
    public List<GithubCommitResponse> findUnsavedGithubCommits(List<GithubCommitResponse> githubCommitList) {
        // 저장되지 않은 커밋 조회
        List<String> savedCommitShaList = queryFactory.select(studyCommit.commitSHA)
                .from(studyCommit)
                .where(studyCommit.commitSHA.in(
                        githubCommitList.stream()
                                .map(GithubCommitResponse::getSha)
                                .toList())
                ).fetch();

        // 저장된 커밋을 제외한 커밋 리스트 반환
        return githubCommitList.stream()
                .filter(commit -> !savedCommitShaList.contains(commit.getSha()))
                .toList();
    }

}
