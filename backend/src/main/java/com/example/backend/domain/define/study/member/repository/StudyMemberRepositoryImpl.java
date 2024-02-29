package com.example.backend.domain.define.study.member.repository;

import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.constant.StudyMemberRole;
import com.example.backend.domain.define.study.member.constant.StudyMemberStatus;
import com.example.backend.study.api.controller.member.response.StudyMembersResponse;
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
                        user.name,
                        user.profileImageUrl
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
}
