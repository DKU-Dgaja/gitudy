package com.example.backend.auth.api.service.oauth.response;

import com.example.backend.domain.define.user.constant.UserPlatformType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthResponse {
    private String platformId;
    private UserPlatformType platformType;
    private String email;
    private String name;
    private String profileImageUrl;

    @Builder
    public OAuthResponse(String platformId, UserPlatformType platformType, String email, String name, String profileImageUrl) {
        this.platformId = platformId;
        this.platformType = platformType;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }
}
