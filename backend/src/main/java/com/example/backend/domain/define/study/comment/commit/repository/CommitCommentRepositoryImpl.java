package com.example.backend.domain.define.study.comment.commit.repository;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.study.api.controller.comment.commit.response.CommitCommentInfoResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.account.user.QUser.user;
import static com.example.backend.domain.define.study.comment.commit.QCommitComment.commitComment;

@Component
@RequiredArgsConstructor
public class CommitCommentRepositoryImpl implements CommitCommentRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CommitCommentInfoResponse> findCommitCommentListByCommitIdJoinUser(Long commitId, Long currentUserId) {

        return jpaQueryFactory.select(Projections.constructor(CommitCommentInfoResponse.class,
                        commitComment.id,
                        commitComment.studyCommitId,
                        commitComment.userId,
                        commitComment.content,
                        commitComment.createdDateTime,
                        Projections.constructor(UserInfoResponse.class,
                                user.id,
                                user.role,
                                user.githubId,
                                user.name,
                                user.profileImageUrl,
                                user.pushAlarmYn,
                                user.profilePublicYn,
                                user.score,
                                user.point
                        ),
                    commitComment.userId.eq(currentUserId).as("isMyComment")
                ))
                .from(commitComment)
                .join(user).on(user.id.eq(commitComment.userId))
                .where(commitComment.studyCommitId.eq(commitId))
                .orderBy(commitComment.id.desc())
                .fetch();
    }
}
