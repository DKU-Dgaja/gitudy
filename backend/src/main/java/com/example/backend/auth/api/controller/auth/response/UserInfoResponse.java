package com.example.backend.auth.api.controller.auth.response;

import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.domain.define.account.user.constant.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {

    private Long id;
    private String platformId;
    private UserPlatformType platformType;
    private UserRole role;
    private String githubId;
    private String name;
    private String profileImageUrl;
    private boolean pushAlarmYn ;
    private int score;
    private int point;


    private void User(Long id, String platformId, UserPlatformType platformType, UserRole role, String githubId, String name, String profileImageUrl, boolean pushAlarmYn, int score, int point) {
        this.id = id;
        this.platformId = platformId;
        this.platformType = platformType;
        this.role = role;
        this.githubId = githubId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.pushAlarmYn = pushAlarmYn;
        this.score = score;
        this.point = point;
    }

    public static UserInfoResponse of(User user){
        return UserInfoResponse.builder()
                .id(user.getId())
                .platformId(user.getPlatformId())
                .platformType(user.getPlatformType())
                .role(user.getRole())
                .githubId(user.getGithubId())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .pushAlarmYn(user.isPushAlarmYn())
                .score(user.getScore())
                .point(user.getPoint())
                .build();
    }
}
