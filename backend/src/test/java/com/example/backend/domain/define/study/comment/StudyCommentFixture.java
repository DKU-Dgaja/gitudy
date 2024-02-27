package com.example.backend.domain.define.study.comment;

import com.example.backend.domain.define.study.comment.study.StudyComment;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentRegisterRequest;

public class StudyCommentFixture {

    public static StudyComment createDefaultStudyComment(Long userId, Long studyInfoId) {
        return StudyComment.builder()
                .studyInfoId(studyInfoId)
                .userId(userId)
                .content("스터디 댓글")
                .build();
    }
    public static StudyCommentRegisterRequest createDefaultStudyCommentRegisterRequest(Long userId) {
        return StudyCommentRegisterRequest.builder()
                .userId(userId)
                .content("스터디 댓글")
                .build();
    }
}
