package com.example.backend.study.api.controller.info.response;

import com.example.backend.study.api.service.info.response.UserNameAndProfileImageResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class StudyInfoListAndCursorIdxResponse {
    private List<StudyInfoListResponse> studyInfoList;

    private Long cursorIdx;

    // Map<STUDY_INFO_ID, List<UserNameAndProfileImageResponse>>
    Map<Long, List<UserNameAndProfileImageResponse>> studyUserInfoMap;

    // Map<STUDY_INFO_ID, List<STUDY_CATEGORY_NAME>>
    private Map<Long, List<String>> studyCategoryMappingMap;

    @Builder
    public StudyInfoListAndCursorIdxResponse(List<StudyInfoListResponse> studyInfoList, Long cursorIdx, Map<Long, List<UserNameAndProfileImageResponse>> studyUserInfoMap, Map<Long, List<String>> studyCategoryMappingMap) {
        this.studyInfoList = studyInfoList;
        this.cursorIdx = cursorIdx;
        this.studyUserInfoMap = studyUserInfoMap;
        this.studyCategoryMappingMap = studyCategoryMappingMap;
    }

    public void setNextCursorIdx() {
        cursorIdx = studyInfoList == null || studyInfoList.isEmpty() ?
                0L : studyInfoList.get(studyInfoList.size() - 1).getId();
    }
}