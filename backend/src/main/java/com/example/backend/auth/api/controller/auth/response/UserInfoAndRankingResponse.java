package com.example.backend.auth.api.controller.auth.response;

import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class UserInfoAndRankingResponse {

    private Long userId;
    private UserRole role;
    private String githubId;
    private String name;
    private String profileImageUrl;
    private boolean pushAlarmYn;
    private boolean profilePublicYn;
    private int score;
    private int point;
    private Long rank;

    @Builder
    public UserInfoAndRankingResponse(Long userId, UserRole role, String githubId, String name, String profileImageUrl, boolean pushAlarmYn, boolean profilePublicYn, int score, int point, Long rank) {
        this.userId = userId;
        this.role = role;
        this.githubId = githubId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.pushAlarmYn = pushAlarmYn;
        this.profilePublicYn = profilePublicYn;
        this.score = score;
        this.point = point;
        this.rank = rank;
    }

    public static UserInfoAndRankingResponse of(User user, Long rank) {
        return UserInfoAndRankingResponse.builder()
                .userId(user.getId())
                .role(user.getRole())
                .githubId(user.getGithubId())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .pushAlarmYn(user.isPushAlarmYn())
                .profilePublicYn(user.isProfilePublicYn())
                .score(user.getScore())
                .point(user.getPoint())
                .rank(rank)
                .build();
    }
}
