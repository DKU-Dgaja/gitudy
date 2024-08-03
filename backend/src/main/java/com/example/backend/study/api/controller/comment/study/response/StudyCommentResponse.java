package com.example.backend.study.api.controller.comment.study.response;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.study.api.service.info.response.UserNameAndProfileImageResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StudyCommentResponse {
    private Long id;
    private Long studyInfoId;
    private Long userId;
    private UserInfoResponse userInfoResponse;
    private String content;

    @Builder
    public StudyCommentResponse(Long id, Long studyInfoId, Long userId, String content, UserInfoResponse userInfoResponse) {
        this.id = id;
        this.studyInfoId = studyInfoId;
        this.userId = userId;
        this.content = content;
        this.userInfoResponse = userInfoResponse;
    }
}
