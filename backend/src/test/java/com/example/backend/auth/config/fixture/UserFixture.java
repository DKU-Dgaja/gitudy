package com.example.backend.auth.config.fixture;

import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.domain.define.account.user.User;

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.*;
import static com.example.backend.domain.define.account.user.constant.UserRole.*;

public class UserFixture {
    public static User generateAuthUser() {
        return User.builder()
                .platformId("1")
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId("깃허브아이디")
                .profileImageUrl("프로필이미지")
                .build();
    }

    public static User generateAuthUserByPlatformId(String platformId) {
        return User.builder()
                .platformId(platformId)
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId("깃허브아이디")
                .profileImageUrl("프로필이미지")
                .build();
    }
    public static User generateUNAUTHUser() {
        return User.builder()
                .platformId("1")
                .platformType(GITHUB)
                .role(UNAUTH)
                .name("이름")
                .githubId("깃허브아이디")
                .profileImageUrl("프로필이미지")
                .build();
    }

    public static User generateGoogleUser() {
        return User.builder()
                .platformId("1")
                .platformType(GOOGLE)
                .role(UNAUTH)
                .name("이름")
                .githubId("깃허브아이디")
                .profileImageUrl("프로필이미지")
                .build();
    }

    public static User generateKaKaoUser() {
        return User.builder()
                .platformId("1")
                .platformType(KAKAO)
                .role(UNAUTH)
                .name("이름")
                .githubId("카카오아이디")
                .profileImageUrl("프로필이미지")
                .build();
    }

    public static OAuthResponse generateOauthResponse() {
        return OAuthResponse.builder()
                .platformId("1")
                .platformType(GITHUB)
                .name("이름")
                .profileImageUrl("프로필이미지")
                .build();
    }
}
