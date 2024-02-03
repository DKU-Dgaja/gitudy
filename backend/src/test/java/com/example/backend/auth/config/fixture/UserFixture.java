package com.example.backend.auth.config.fixture;

import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.domain.define.account.user.User;

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;
import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GOOGLE;
import static com.example.backend.domain.define.account.user.constant.UserRole.*;

public class UserFixture {
    public final static String expectedUserPlatformId = "1";
    public final static String expectedUserName = "이름";
    public final static String expectedUserGithubId = "깃허브아이디";
    public final static String expectedUserProfileImageUrl = "프로필이미지";

    public static User generateAuthUser() {
        return User.builder()
                .platformId(expectedUserPlatformId)
                .platformType(GITHUB)
                .role(USER)
                .name(expectedUserName)
                .githubId(expectedUserGithubId)
                .profileImageUrl(expectedUserProfileImageUrl)
                .build();
    }

    public static User generateUNAUTHUser() {
        return User.builder()
                .platformId(expectedUserPlatformId)
                .platformType(GITHUB)
                .role(UNAUTH)
                .name(expectedUserName)
                .githubId(expectedUserGithubId)
                .profileImageUrl(expectedUserProfileImageUrl)
                .build();
    }

    public static User generateGoogleUser() {
        return User.builder()
                .platformId(expectedUserPlatformId)
                .platformType(GOOGLE)
                .role(USER)
                .name(expectedUserName)
                .githubId(expectedUserGithubId)
                .profileImageUrl(expectedUserProfileImageUrl)
                .build();
    }

    public static OAuthResponse generateOauthResponse() {
        return OAuthResponse.builder()
                .platformId(expectedUserPlatformId)
                .platformType(GITHUB)
                .name(expectedUserName)
                .profileImageUrl(expectedUserProfileImageUrl)
                .build();
    }
}
