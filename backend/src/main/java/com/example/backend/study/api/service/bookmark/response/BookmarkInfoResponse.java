package com.example.backend.study.api.service.bookmark.response;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.study.api.service.info.response.StudyInfoWithIdResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BookmarkInfoResponse {
    private Long id;
    private Long studyInfoId;
    private Long userId;
    private StudyInfoWithIdResponse studyInfoWithIdResponse;
    private UserInfoResponse userInfoResponse;

    @Builder
    public BookmarkInfoResponse(Long id, Long studyInfoId, Long userId, StudyInfoWithIdResponse studyInfoWithIdResponse, UserInfoResponse userInfoResponse) {
        this.id = id;
        this.studyInfoId = studyInfoId;
        this.userId = userId;
        this.studyInfoWithIdResponse = studyInfoWithIdResponse;
        this.userInfoResponse = userInfoResponse;
    }
}
