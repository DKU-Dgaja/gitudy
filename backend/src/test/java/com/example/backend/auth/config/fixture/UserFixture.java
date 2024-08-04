package com.example.backend.auth.config.fixture;

import com.example.backend.auth.api.controller.auth.request.UserNameRequest;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.oauth.response.OAuthResponse;
import com.example.backend.domain.define.account.user.SocialInfo;
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
                .score(10)
                .build();
    }

    public static User generateAdminUser() {
        return User.builder()
                .platformId("111")
                .platformType(GITHUB)
                .role(ADMIN)
                .name("관리자")
                .githubId("관리자")
                .profileImageUrl("관리자")
                .score(100)
                .build();
    }

    public static User generateAuthUserByGithubId(String githubId) {
        return User.builder()
                .platformId("1")
                .platformType(KAKAO)
                .role(USER)
                .name("이름")
                .githubId(githubId)
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
                .role(USER)
                .name("이름")
                .githubId("깃허브아이디")
                .profileImageUrl("프로필이미지")
                .score(30)
                .build();
    }

    public static User generateKaKaoUser() {
        return User.builder()
                .platformId("1")
                .platformType(KAKAO)
                .role(USER)
                .name("이름")
                .githubId("카카오아이디")
                .profileImageUrl("프로필이미지")
                .score(50)
                .build();
    }

    public static OAuthResponse generateOauthResponse() {
        return OAuthResponse.builder()
                .platformId("1")
                .platformType(GITHUB)
                .name("이름")
                .profileImageUrl("프로필이미지")
                .githubApiToken("githubApiToken")
                .build();
    }

    // 테스트용 스터디원 조회(플랫폼Id,이름,프로필사진)
    public static User generatePlatfomIdAndNameAndProfile(String platformId, String name, String profileImageUrl) {
        return User.builder()
                .platformId(platformId)
                .platformType(GOOGLE)
                .role(USER)
                .name(name)
                .profileImageUrl(profileImageUrl)
                .githubId("구글아이디")
                .build();
    }


    public static User generateDefaultUser(String platformId, String name) {

        SocialInfo socialInfo = SocialInfo.builder()
                .blogLink("블로그 링크")
                .githubLink("깃허브 링크")
                .linkedInLink("링크드인 링크")
                .build();

        return User.builder()
                .platformId(platformId)
                .name(name)
                .platformType(GOOGLE)
                .role(USER)
                .githubId("깃허브아이디")
                .socialInfo(socialInfo)
                .profileImageUrl("이미지")
                .build();
    }

    public static UserNameRequest generateUserNameRequest(String name) {
        return UserNameRequest.builder()
                .name(name)
                .build();
    }

    public static User generateAuthUserPushAlarmY() {
        return User.builder()
                .platformId("test")
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId("깃허브아이디")
                .profileImageUrl("프로필이미지")
                .pushAlarmYn(true)
                .build();
    }

    public static User generateAuthUserPushAlarmN() {
        return User.builder()
                .platformId("test")
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId("깃허브아이디")
                .profileImageUrl("프로필이미지")
                .pushAlarmYn(false)
                .build();
    }

    public static User generateAuthUserPushAlarmYs(String platformId) {
        return User.builder()
                .platformId(platformId)
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId("깃허브아이디")
                .profileImageUrl("프로필이미지")
                .pushAlarmYn(true)
                .build();
    }

    public static User generateAuthUserPushAlarmNs(String platformId) {
        return User.builder()
                .platformId(platformId)
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId("깃허브아이디")
                .profileImageUrl("프로필이미지")
                .pushAlarmYn(false)
                .build();
    }

    public static User generateAuthJusung() {
        return User.builder()
                .platformId("platformId")
                .platformType(GITHUB)
                .role(USER)
                .name("이주성")
                .githubId("jusung-c")
                .profileImageUrl("www.naver.com")
                .pushAlarmYn(false)
                .build();
    }

    public static UserInfoResponse createDefaultUserInfoResponse(Long userId) {
        return UserInfoResponse.builder()
                .userId(userId)
                .name("user")
                .profileImageUrl("profileImageUrl")
                .pushAlarmYn(false)
                .profilePublicYn(true)
                .score(0)
                .point(0)
                .build();
    }
}
