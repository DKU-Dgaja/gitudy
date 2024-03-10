package com.example.backend.study.api.service.comment.study.response;

import com.example.backend.domain.define.account.user.constant.UserRole;
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
