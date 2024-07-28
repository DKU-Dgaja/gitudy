package com.example.backend.domain.define.study.comment.study.repository;

import com.example.backend.study.api.controller.comment.study.response.StudyCommentResponse;
import com.example.backend.study.api.service.info.response.UserNameAndProfileImageResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.account.user.QUser.user;
import static com.example.backend.domain.define.study.comment.study.QStudyComment.studyComment;

@Component
@RequiredArgsConstructor
public class StudyCommentRepositoryImpl implements StudyCommentRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<StudyCommentResponse> findStudyCommentListByStudyInfoIdJoinUser(Long studyId, Long cursorIdx, Long limit) {
        JPAQuery<StudyCommentResponse> query = jpaQueryFactory
                .select(Projections.constructor(
                        StudyCommentResponse.class,
                        studyComment.id,
                        studyComment.studyInfoId,
                        studyComment.userId,
                        studyComment.content,
                        Projections.constructor(
                                UserNameAndProfileImageResponse.class,
                                user.id,
                                user.name,
                                user.profileImageUrl
                        )
                ))
                .from(studyComment)
                .join(user).on(user.id.eq(studyComment.userId))
                .where(studyComment.studyInfoId.eq(studyId))
                .orderBy(studyComment.id.desc());

        // cursorIdx가 null이 아닌 경우 커서 기반으로 데이터 가져오도록
        if (cursorIdx != null) {
            query = query.where(studyComment.id.lt(cursorIdx));
        }

        // 커서 다음부터 limit만큼 가져오기
        return query
                .limit(limit)
                .fetch();
    }
}
