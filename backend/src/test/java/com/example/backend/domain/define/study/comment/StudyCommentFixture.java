package com.example.backend.domain.define.study.comment;

import com.example.backend.domain.define.study.comment.study.StudyComment;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentRegisterRequest;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentUpdateRequest;

import java.util.ArrayList;
import java.util.List;

public class StudyCommentFixture {

    public static StudyComment createDefaultStudyComment(Long userId, Long studyInfoId) {
        return StudyComment.builder()
                .studyInfoId(studyInfoId)
                .userId(userId)
                .content("스터디 댓글")
                .build();
    }

    // 테스트용 스터디 댓글 리스트 생성 메서드
    public static List<StudyComment> createDefaultStudyCommentList(int count, Long userId, Long studyInfoId) {
        List<StudyComment> studyComments = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            studyComments.add(createDefaultStudyComment(userId, studyInfoId));
        }
        return studyComments;
    }
    public static StudyCommentRegisterRequest createDefaultStudyCommentRegisterRequest(Long userId) {
        return StudyCommentRegisterRequest.builder()
                .userId(userId)
                .content("스터디 댓글")
                .build();
    }

    public static StudyCommentUpdateRequest createDefaultStudyCommentUpdateRequest(Long userId) {
        return StudyCommentUpdateRequest.builder()
                .userId(userId)
                .content("ChangedContent")
                .build();
    }
}
