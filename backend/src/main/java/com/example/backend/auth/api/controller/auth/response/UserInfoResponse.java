package com.example.backend.auth.api.controller.auth.response;

import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.domain.define.account.user.constant.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfoResponse {
    private UserRole role;
    private String githubId;
    private String name;
    private String profileImageUrl;
    private boolean pushAlarmYn;
    private boolean profilePublicYn;
    private int score;
    private int point;

    @Builder
    public UserInfoResponse(UserRole role, String githubId, String name, String profileImageUrl, boolean pushAlarmYn, boolean profilePublicYn, int score, int point) {
        this.role = role;
        this.githubId = githubId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.pushAlarmYn = pushAlarmYn;
        this.profilePublicYn = profilePublicYn;
        this.score = score;
        this.point = point;
    }

    public static UserInfoResponse of(User user){
        return UserInfoResponse.builder()
                .role(user.getRole())
                .githubId(user.getGithubId())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .pushAlarmYn(user.isPushAlarmYn())
                .profilePublicYn(user.isProfilePublicYn())
                .score(user.getScore())
                .point(user.getPoint())
                .build();
    }
}
