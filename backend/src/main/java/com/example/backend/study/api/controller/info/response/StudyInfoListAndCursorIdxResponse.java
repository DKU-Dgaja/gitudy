package com.example.backend.study.api.controller.info.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class StudyInfoListAndCursorIdxResponse {
    private List<StudyInfoResponse> studyInfoList;
    private Long cursorIdx;
    @Builder
    public StudyInfoListAndCursorIdxResponse (List<StudyInfoResponse> studyInfoList, Long cursorIdx) {
        this.studyInfoList = studyInfoList;
        this.cursorIdx = cursorIdx;
    }
}
