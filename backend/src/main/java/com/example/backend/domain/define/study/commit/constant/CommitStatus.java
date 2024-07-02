package com.example.backend.domain.define.study.commit.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommitStatus {
    APPROVAL("승인"),
    REJECTION("거절"),
    WAITING("대기"),
    DELETE("삭제");

    private final String text;
}
