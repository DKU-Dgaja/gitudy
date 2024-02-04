package com.example.backend.study.api.controller.commit.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserCommitInfoRequest {
    private int pageSize;
    private Long cursorIdx;

    @Builder
    public UserCommitInfoRequest(int pageSize, Long cursorIdx) {
        this.pageSize = pageSize;
        this.cursorIdx = cursorIdx;
    }
}
