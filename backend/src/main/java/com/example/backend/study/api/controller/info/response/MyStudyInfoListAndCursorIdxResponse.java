package com.example.backend.study.api.controller.info.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyStudyInfoListAndCursorIdxResponse {
    private List<MyStudyInfoListResponse> studyInfoList;
    private Long cursorIdx;

    @Builder
    public MyStudyInfoListAndCursorIdxResponse(List<MyStudyInfoListResponse> studyInfoList, Long cursorIdx) {
        this.studyInfoList = studyInfoList;
        this.cursorIdx = cursorIdx;
    }

    public void setNextCursorIdx() {
        cursorIdx = studyInfoList == null || studyInfoList.isEmpty() ?
                0L : studyInfoList.get(studyInfoList.size() - 1).getId();
    }
}