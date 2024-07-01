package com.example.backend.study.api.controller.info.response;

import com.example.backend.study.api.service.info.response.UserNameAndProfileImageResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class StudyInfoListAndCursorIdxResponse {
    private List<StudyInfoListWithMemberResponse> studyInfoList;

    private Long cursorIdx;


    // Map<STUDY_INFO_ID, List<STUDY_CATEGORY_NAME>>
    private Map<Long, List<String>> studyCategoryMappingMap;

    @Builder
    public StudyInfoListAndCursorIdxResponse(List<StudyInfoListWithMemberResponse> studyInfoList, Long cursorIdx, Map<Long, List<String>> studyCategoryMappingMap) {
        this.studyInfoList = studyInfoList;
        this.cursorIdx = cursorIdx;
        this.studyCategoryMappingMap = studyCategoryMappingMap;
    }

    public void setNextCursorIdx() {
        cursorIdx = studyInfoList == null || studyInfoList.isEmpty() ?
                0L : studyInfoList.get(studyInfoList.size() - 1).getId();
    }
}