package com.example.backend.study.api.controller.comment.study.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class StudyCommentListAndCursorIdxResponse {
    private List<StudyCommentResponse> studyCommentList;
    private Long cursorIdx;

    @Builder
    public StudyCommentListAndCursorIdxResponse(List<StudyCommentResponse> studyCommentList, Long cursorIdx) {
        this.studyCommentList = studyCommentList;
        this.cursorIdx = cursorIdx;
    }

    public void getNextCursorIdx() {
        cursorIdx = studyCommentList == null || studyCommentList.isEmpty() ?
                0L : studyCommentList.get(studyCommentList.size() - 1).getId();
    }
}
