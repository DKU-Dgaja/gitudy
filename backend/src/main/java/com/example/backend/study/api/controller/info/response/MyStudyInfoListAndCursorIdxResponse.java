package com.example.backend.study.api.controller.info.response;

import com.example.backend.study.api.service.info.response.StudyCategoryMappingListResponse;
import com.example.backend.study.api.service.info.response.StudyMembersIdListResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyStudyInfoListAndCursorIdxResponse {
    private List<MyStudyInfoListResponse> studyInfoList;
    private Long cursorIdx;
    private List<StudyMembersIdListResponse> studyMembersIds;
    private List<StudyCategoryMappingListResponse> studyCategoryMappingResponse;
    @Builder
    public MyStudyInfoListAndCursorIdxResponse(List<MyStudyInfoListResponse> studyInfoList, Long cursorIdx, List<StudyMembersIdListResponse> studyMembersIds, List<StudyCategoryMappingListResponse> studyCategoryMappingResponse) {
        this.studyInfoList = studyInfoList;
        this.cursorIdx = cursorIdx;
        this.studyMembersIds = studyMembersIds;
        this.studyCategoryMappingResponse=studyCategoryMappingResponse;
    }

    public void setNextCursorIdx() {
        cursorIdx = studyInfoList == null || studyInfoList.isEmpty() ?
                0L : studyInfoList.get(studyInfoList.size() - 1).getId();
    }
}