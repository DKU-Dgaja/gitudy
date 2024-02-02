package com.example.backend.auth.api.service.auth.response;

import com.example.backend.domain.define.account.user.SocialInfo;
import com.example.backend.domain.define.account.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserUpdatePageResponse {
    private String name;                                        // 이름
    private String profileImageUrl;                             // 프로필 사진
    private boolean profilePublicYn;                            // 프로필 공개 여부
    private SocialInfo socialInfo;                              // 소셜 정보

    @Builder
    public UserUpdatePageResponse(String name, String profileImageUrl, boolean profilePublicYn, SocialInfo socialInfo) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.profilePublicYn = profilePublicYn;
        this.socialInfo = socialInfo;
    }

    public static UserUpdatePageResponse of(User user) {
        return UserUpdatePageResponse.builder()
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .profilePublicYn(user.isProfilePublicYn())
                .socialInfo(user.getSocialInfo())
                .build();
    }
}
