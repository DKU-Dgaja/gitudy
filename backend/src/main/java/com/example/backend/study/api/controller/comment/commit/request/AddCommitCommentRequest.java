package com.example.backend.study.api.controller.comment.commit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddCommitCommentRequest {
    @NotBlank(message = "댓글 내용은 공백일 수 없습니다.")
    @Size(max = 60, message = "댓글 내용은 60자를 넘을 수 없습니다.")
    private String content;

    @Builder
    public AddCommitCommentRequest(String content) {
        this.content = content;
    }
}
