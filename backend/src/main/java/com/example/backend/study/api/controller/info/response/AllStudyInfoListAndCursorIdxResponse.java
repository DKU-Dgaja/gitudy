package com.example.backend.study.api.controller.info.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class AllStudyInfoListAndCursorIdxResponse {
    private List<AllStudyInfoResponse> studyInfoList;
    private Long cursorIdx;
    @Builder
    public AllStudyInfoListAndCursorIdxResponse (List<AllStudyInfoResponse> studyInfoList, Long cursorIdx) {
        this.studyInfoList = studyInfoList;
        this.cursorIdx = cursorIdx;
    }
}
