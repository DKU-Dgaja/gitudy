package com.example.backend.study.api.controller.comment.study.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyCommentUpdateRequest {

    private String content;                     // 댓글 내용
}
