package com.example.backend.study.api.controller.comment.commit.response;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class CommitCommentInfoResponse {
    private Long id;                            // 아이디
    private Long studyCommitId;                 // 커밋 ID
    private Long userId;                        // 사용자 ID
    private String content;                     // 댓글 내용
    private LocalDateTime createdDateTime;
    private LocalDateTime modifiedDateTime;
    private UserInfoResponse userInfoResponse;
    private boolean isMyComment;
    @Builder
    public CommitCommentInfoResponse(Long id, Long studyCommitId, Long userId, String content, LocalDateTime createdDateTime, LocalDateTime modifiedDateTime, UserInfoResponse userInfoResponse, boolean isMyComment) {
        this.id = id;
        this.studyCommitId = studyCommitId;
        this.userId = userId;
        this.content = content;
        this.createdDateTime = createdDateTime;
        this.modifiedDateTime = modifiedDateTime;
        this.userInfoResponse = userInfoResponse;
        this.isMyComment = isMyComment;
    }
}
