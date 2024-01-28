package com.example.backend.domain.define.study.member.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudyMemberRole {
    STUDY_LEADER("스터디장"),
    STUDY_MEMBER("스터디원"),
    ;

    private final String text;
}
