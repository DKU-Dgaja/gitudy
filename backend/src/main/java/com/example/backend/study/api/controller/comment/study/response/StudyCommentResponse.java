package com.example.backend.study.api.controller.comment.study.response;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class StudyCommentResponse {
    private Long id;
    private Long studyInfoId;
    private Long userId;
    private UserInfoResponse userInfoResponse;
    private String content;
    private boolean isMyComment;
    private LocalDateTime createdDateTime;

    @Builder
    public StudyCommentResponse(Long id, Long studyInfoId, Long userId, String content, UserInfoResponse userInfoResponse, boolean isMyComment, LocalDateTime createdDateTime) {
        this.id = id;
        this.studyInfoId = studyInfoId;
        this.userId = userId;
        this.content = content;
        this.userInfoResponse = userInfoResponse;
        this.isMyComment = isMyComment;
        this.createdDateTime = createdDateTime;
    }
}
