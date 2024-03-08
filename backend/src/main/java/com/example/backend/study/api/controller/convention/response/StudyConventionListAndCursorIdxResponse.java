package com.example.backend.study.api.controller.convention.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class StudyConventionListAndCursorIdxResponse {

    private List<StudyConventionResponse> studyConventionList; // 컨벤션 정보

    private Long cursorIdx; // 다음위치 커서

    public void setNextCursorIdx() {
        cursorIdx = studyConventionList == null || studyConventionList.isEmpty() ?
                0L : studyConventionList.get(studyConventionList.size() - 1).getConventionId();
    }

}
