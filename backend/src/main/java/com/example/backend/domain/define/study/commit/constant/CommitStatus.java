package com.example.backend.domain.define.study.commit.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommitStatus {
    COMMIT_APPROVAL("승인된 커밋"),
    COMMIT_REJECTION("거절된 커밋"),
    COMMIT_WAITING("팀장의 승인을 기다리는 커밋"),
    COMMIT_DELETE("삭제된 커밋")
    ;

    private final String text;
}
