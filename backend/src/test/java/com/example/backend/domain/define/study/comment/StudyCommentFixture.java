package com.example.backend.domain.define.study.comment;

import com.example.backend.domain.define.study.comment.study.StudyComment;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentRegisterRequest;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentUpdateRequest;
import com.example.backend.study.api.controller.comment.study.response.StudyCommentListAndCursorIdxResponse;
import com.example.backend.study.api.controller.comment.study.response.StudyCommentResponse;
import com.example.backend.study.api.service.info.response.UserNameAndProfileImageResponse;

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
    public static StudyCommentRegisterRequest createDefaultStudyCommentRegisterRequest() {
        return StudyCommentRegisterRequest.builder()
                .content("스터디 댓글")
                .build();
    }

    public static StudyCommentUpdateRequest createDefaultStudyCommentUpdateRequest() {
        return StudyCommentUpdateRequest.builder()
                .content("ChangedContent")
                .build();
    }
    public static StudyCommentResponse createDefaultStudyCommentResponse(Long userId, Long studyInfoId) {
        return StudyCommentResponse.builder()
                .studyInfoId(studyInfoId)
                .userId(userId)
                .userInfoResponse(createDefaultUserInfoResponse(userId))
                .build();
    }
    public static StudyCommentListAndCursorIdxResponse generateStudyCommentListAndCursorIdxResponse(Long userId, Long studyInfoId) {
        Long cursorIdx = 5L;
        List<StudyCommentResponse> comments = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            comments.add(createDefaultStudyCommentResponse(userId, studyInfoId));
        }
        return StudyCommentListAndCursorIdxResponse.builder()
                .studyCommentList(comments)
                .cursorIdx(cursorIdx)
                .build();
    }
    public static UserNameAndProfileImageResponse createDefaultUserInfoResponse(Long userId) {
        return UserNameAndProfileImageResponse.builder()
                .id(userId)
                .name("user")
                .profileImageUrl("profileImageUrl")
                .build();
    }
}
