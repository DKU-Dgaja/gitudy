package com.example.backend.domain.define.study.commit.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommitStatus {
    COMMIT_APPROVAL("승인"),
    COMMIT_REJECTION("거절"),
    COMMIT_WAITING("대기"),
    COMMIT_DELETE("삭제");

    private final String text;
}
