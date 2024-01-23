package com.example.backend.auth.api.service.oauth.response;

import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthResponse {
    private String platformId;
    private UserPlatformType platformType;
    private String name;
    private String profileImageUrl;

    @Builder
    public OAuthResponse(String platformId, UserPlatformType platformType, String name, String profileImageUrl) {
        this.platformId = platformId;
        this.platformType = platformType;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }
}
