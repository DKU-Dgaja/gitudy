package com.example.backend.study.api.controller.member.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class StudyMemberApplyListAndCursorIdxResponse {

    private List<StudyMemberApplyResponse> applyList; // 신청 정보

    private Long cursorIdx;

    private String studyTopic;

    public void setNextCursorIdx() {
        cursorIdx = applyList == null || applyList.isEmpty() ?
                0L : applyList.get(applyList.size() - 1).getId();

    }
}
