package com.example.backend.study.api.service.bookmark.response;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.study.api.service.info.response.StudyInfoResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BookmarkInfoResponse {
    private Long id;
    private Long studyInfoId;
    private Long userId;
    private StudyInfoResponse studyInfoResponse;
    private UserInfoResponse userInfoResponse;

    @Builder
    public BookmarkInfoResponse(Long id, Long studyInfoId, Long userId, StudyInfoResponse studyInfoResponse, UserInfoResponse userInfoResponse) {
        this.id = id;
        this.studyInfoId = studyInfoId;
        this.userId = userId;
        this.studyInfoResponse = studyInfoResponse;
        this.userInfoResponse = userInfoResponse;
    }
}
