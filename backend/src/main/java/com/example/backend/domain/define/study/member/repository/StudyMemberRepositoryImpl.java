package com.example.backend.domain.define.study.member.repository;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.constant.StudyMemberRole;
import com.example.backend.domain.define.study.member.constant.StudyMemberStatus;
import com.example.backend.study.api.controller.info.response.StudyMemberWithUserInfoResponse;
import com.example.backend.study.api.controller.member.response.StudyMemberApplyResponse;
import com.example.backend.study.api.controller.member.response.StudyMembersResponse;
import com.example.backend.study.api.service.info.response.UserNameAndProfileImageResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.account.user.QUser.user;
import static com.example.backend.domain.define.study.member.QStudyMember.studyMember;

@Component
@RequiredArgsConstructor
public class StudyMemberRepositoryImpl implements StudyMemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsStudyMemberByUserIdAndStudyInfoId(Long userId, Long studyInfoId) {

        return queryFactory.from(studyMember)
                .where(studyMember.studyInfoId.eq(studyInfoId)
                        .and(studyMember.userId.eq(userId))
                        .and(studyMember.status.eq(StudyMemberStatus.STUDY_ACTIVE)))
                .fetchFirst() != null;

    }

    @Override
    public boolean isStudyLeaderByUserIdAndStudyInfoId(Long userId, Long studyInfoId) {
        return queryFactory.from(studyMember)
                .where(studyMember.studyInfoId.eq(studyInfoId)
                        .and(studyMember.userId.eq(userId))
                        .and(studyMember.role.eq(StudyMemberRole.STUDY_LEADER)))
                .fetchFirst() != null;
    }

    @Override
    public List<StudyMember> findActiveMembersByStudyInfoId(Long studyInfoId) {
        return queryFactory
                .selectFrom(studyMember)
                .where(studyMember.studyInfoId.eq(studyInfoId)
                        .and(studyMember.status.eq(StudyMemberStatus.STUDY_ACTIVE)))
                .fetch();
    }

    @Override
    public List<StudyMembersResponse> findStudyMembersByStudyInfoIdOrderByScore(Long studyInfoId, boolean orderByScore) {
        JPAQuery<StudyMembersResponse> query = queryFactory
                .select(Projections.constructor(StudyMembersResponse.class,
                        studyMember.userId,
                        studyMember.role,
                        studyMember.status,
                        studyMember.score,
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
                .from(studyMember)
                .join(user).on(user.id.eq(studyMember.userId))
                .where(studyMember.studyInfoId.eq(studyInfoId)
                        .and(studyMember.status.eq(StudyMemberStatus.STUDY_ACTIVE)));

        if (orderByScore) {
            query = query.orderBy(studyMember.score.desc(), studyMember.userId.asc()); // 기여도별 내림차순, 동일 점수 시 사용자 ID 오름차순
        } else {
            query = query.orderBy(studyMember.userId.asc()); // 사용자 Id 오름차순 (가입순)
        }
        return query.fetch();
    }

    @Override
    public List<StudyMemberWithUserInfoResponse> findStudyMemberListByStudyInfoListJoinUserInfo(List<Long> studyInfoIdList) {
        JPAQuery<StudyMemberWithUserInfoResponse> query = queryFactory
                .select(Projections.constructor(
                        StudyMemberWithUserInfoResponse.class,
                        studyMember.studyInfoId,
                        Projections.constructor(
                                UserNameAndProfileImageResponse.class,
                                user.id,
                                user.name,
                                user.profileImageUrl
                        )
                ))
                .from(studyMember)
                .join(user).on(user.id.eq(studyMember.userId))
                .where(studyMember.studyInfoId.in(studyInfoIdList)
                        .and(studyMember.status.eq(StudyMemberStatus.STUDY_ACTIVE)))
                .orderBy(studyMember.id.desc());
        return query.fetch();
    }

    @Override
    public boolean isResignedStudyMemberByUserIdAndStudyInfoId(Long userId, Long studyInfoId) {

        return queryFactory.from(studyMember)
                .where(studyMember.studyInfoId.eq(studyInfoId)
                        .and(studyMember.userId.eq(userId))
                        .and(studyMember.status.eq(StudyMemberStatus.STUDY_RESIGNED)))
                .fetchFirst() != null;
    }

    @Override
    public boolean isWaitingStudyMemberByUserIdAndStudyInfoId(Long userId, Long studyInfoId) {
        return queryFactory.from(studyMember)
                .where(studyMember.studyInfoId.eq(studyInfoId)
                        .and(studyMember.userId.eq(userId))
                        .and(studyMember.status.eq(StudyMemberStatus.STUDY_WAITING)))
                .fetchFirst() != null;
    }

    @Override
    public List<StudyMemberApplyResponse> findStudyApplyListByStudyInfoId_CursorPaging(Long studyInfoId, Long cursorIdx, Long limit) {

        JPAQuery<StudyMemberApplyResponse> query = queryFactory
                .select(Projections.constructor(StudyMemberApplyResponse.class,
                        studyMember.id,
                        studyMember.signGreeting,
                        user.id.as("userId"),
                        user.name,
                        user.githubId,
                        user.socialInfo,
                        user.profileImageUrl,
                        user.score,
                        user.point,
                        user.profilePublicYn,
                        studyMember.createdDateTime))
                .from(studyMember)
                .join(user).on(studyMember.userId.eq(user.id))
                .where(studyMember.studyInfoId.eq(studyInfoId)
                        .and(studyMember.status.eq(StudyMemberStatus.STUDY_WAITING)))
                .orderBy(studyMember.id.asc());

        if (cursorIdx != null) {
            query = query.where(studyMember.id.gt(cursorIdx)); // 먼저 신청한순 (마지막 항목기준 Id 값이 큰값들 조회)
        }

        return query.limit(limit)
                .fetch();

    }

    @Override
    public boolean existsStudyMemberByGithubIdAndStudyInfoId(String githubId, Long studyInfoId) {
        return queryFactory.selectOne()
                .from(studyMember)
                .join(user).on(user.id.eq(studyMember.userId))
                .where(user.githubId.eq(githubId)
                        .and(studyMember.studyInfoId.eq(studyInfoId))
                        .and(studyMember.status.eq(StudyMemberStatus.STUDY_ACTIVE)))
                .fetchFirst() != null;
    }
}
