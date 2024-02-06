package com.example.backend.domain.define.account.bookmark.repository;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.study.api.service.bookmark.response.BookmarkInfoResponse;
import com.example.backend.study.api.service.info.response.StudyInfoWithIdResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.account.bookmark.QStudyBookmark.studyBookmark;
import static com.example.backend.domain.define.account.user.QUser.user;
import static com.example.backend.domain.define.study.info.QStudyInfo.studyInfo;

@Component
@RequiredArgsConstructor
public class StudyBookmarkRepositoryImpl implements StudyBookmarkRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<BookmarkInfoResponse> findStudyBookmarkListByUserIdJoinStudyInfo(Long userId, Long cursorIdx, Long limit) {
        JPAQuery<BookmarkInfoResponse> query = jpaQueryFactory
                .select(Projections.constructor(
                        BookmarkInfoResponse.class,
                        studyBookmark.id,
                        studyBookmark.studyInfoId,
                        studyBookmark.userId,
                        Projections.constructor(
                                StudyInfoWithIdResponse.class,
                                studyInfo.id,
                                studyInfo.topic,
                                studyInfo.score,
                                studyInfo.endDate,
                                studyInfo.info,
                                studyInfo.status,
                                studyInfo.maximumMember,
                                studyInfo.currentMember,
                                studyInfo.lastCommitDay,
                                studyInfo.profileImageUrl,
                                studyInfo.notice,
                                studyInfo.repositoryInfo
                        ),
                        Projections.constructor(
                                UserInfoResponse.class,
                                user.id,
                                user.role,
                                user.githubId,
                                user.name,
                                user.profileImageUrl,
                                user.pushAlarmYn,
                                user.profilePublicYn,
                                user.score,
                                user.point
                        )
                ))
                .from(studyBookmark)
                .join(studyInfo).on(studyInfo.id.eq(studyBookmark.studyInfoId))
                .join(user).on(user.id.eq(studyBookmark.userId))
                .where(studyBookmark.userId.eq(userId))
                .orderBy(studyBookmark.id.desc());

        // cursorIdx가 null이 아닌 경우 커서 기반으로 데이터 가져오도록
        if (cursorIdx != null) {
            query = query.where(studyBookmark.id.lt(cursorIdx));
        }

        // 커서 다음부터 limit만큼 가져오기
        return query
                .limit(limit)
                .fetch();
    }
}
