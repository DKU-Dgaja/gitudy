package com.example.backend.domain.define.study.member.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudyMemberStatus {
    STUDY_WAITING("승인 대기"),
    STUDY_ACTIVE("활동"),
    STUDY_WITHDRAWAL("탈퇴"),
    STUDY_RESIGNED("강퇴"),
    STUDY_REFUSED("승인 거부");

    private final String text;
}
