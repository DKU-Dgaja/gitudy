package com.example.backend.study.api.controller.comment.commit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddCommitCommentRequest {
    @NotBlank(message = "댓글 내용은 공백일 수 없습니다.")
    @Size(max = 100, message = "댓글 내용은 100자를 넘을 수 없습니다.")
    private String content;

    private Long studyInfoId;  // 프론트에서 처리할 스터디Id
}
