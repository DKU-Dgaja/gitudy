package com.example.backend.domain.define.user.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserPlatformType {
    KAKAO("카카오 로그인"),
    NAVER("네이버 로그인"),
    GOOGLE("구글 로그인");

    private final String text;
}
