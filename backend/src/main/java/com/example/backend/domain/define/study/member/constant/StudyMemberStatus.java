package com.example.backend.domain.define.study.member.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudyMemberStatus {
    STUDY_ACTIVE("활동중인 스터디원"),
    STUDY_WITHDRAWAL("탈퇴한 스터디원"),
    STUDY_RESIGNED("강퇴된 스터디원"),
    STUDY_WAITING("스터디 승인 대기중인 유저"),
    STUDY_REFUSED("스터디 승인 거부된 유저")
    ;

    private final String text;
}
