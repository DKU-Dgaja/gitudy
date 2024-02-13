package com.example.backend.domain.define.study.member.repository;

import com.example.backend.domain.define.study.member.constant.StudyMemberStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
}
