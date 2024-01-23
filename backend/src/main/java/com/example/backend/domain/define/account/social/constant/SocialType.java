package com.example.backend.domain.define.account.social.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialType {
    EMAIL("이메일"),
    GITHUB("깃허브"),
    BLOG("블로그"),                // 네이버, 티스토리 ..
    LINKED_IN("링크드인"),
    OTHER("기타");                // 기타 포트폴리오

    private final String text;
}
