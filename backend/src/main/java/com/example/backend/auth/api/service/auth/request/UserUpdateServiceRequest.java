package com.example.backend.auth.api.service.auth.request;

import com.example.backend.auth.api.controller.auth.request.UserUpdateRequest;
import com.example.backend.domain.define.account.user.SocialInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserUpdateServiceRequest {
    private Long userId;
    private String name;                                        // 이름
    private String profileImageUrl;                             // 프로필 사진
    private boolean profilePublicYn;                            // 프로필 공개 여부
    private SocialInfo socialInfo;                              // 소셜 정보

    @Builder
    public UserUpdateServiceRequest(Long userId, String name, String profileImageUrl, boolean profilePublicYn, SocialInfo socialInfo) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.profilePublicYn = profilePublicYn;
        this.socialInfo = socialInfo;
    }

    public static UserUpdateServiceRequest of(Long userId, UserUpdateRequest request) {
        return UserUpdateServiceRequest.builder()
                .userId(userId)
                .name(request.getName())
                .profileImageUrl(request.getProfileImageUrl())
                .profilePublicYn(request.isProfilePublicYn())
                .socialInfo(request.getSocialInfo())
                .build();
    }
}
