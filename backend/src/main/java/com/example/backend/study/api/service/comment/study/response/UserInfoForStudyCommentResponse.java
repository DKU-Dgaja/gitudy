package com.example.backend.study.api.service.comment.study.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoForStudyCommentResponse {
    private Long userId;

    private String name;

    private String profileImageUrl;
}
