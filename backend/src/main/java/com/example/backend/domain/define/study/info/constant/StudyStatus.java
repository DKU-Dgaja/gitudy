package com.example.backend.domain.define.study.info.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudyStatus {
    STUDY_PUBLIC("공개"),
    STUDY_PRIVATE("비공개"),
    STUDY_DELETED("삭제된");

    private final String text;
}
