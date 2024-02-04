package com.example.backend.study.api.controller.commit.response;

import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CommitInfoListAndCursorIdxResponse {
    private List<CommitInfoResponse> commitInfoList;
    private Long cursorIdx;

    @Builder
    public CommitInfoListAndCursorIdxResponse(List<CommitInfoResponse> commitInfoList, Long cursorIdx) {
        this.commitInfoList = commitInfoList;
        this.cursorIdx = cursorIdx;
    }
}
