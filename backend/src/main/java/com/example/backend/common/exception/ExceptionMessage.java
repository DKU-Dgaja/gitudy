package com.example.backend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {
    // JwtException
    JWT_TOKEN_EXPIRED("JWT 토큰이 만료되었습니다."),
    JWT_UNSUPPORTED("지원하지 않는 JWT 토큰입니다."),
    JWT_MALFORMED("올바른 JWT 토큰의 형태가 아닙니다."),
    JWT_SIGNATURE("올바른 SIGNATURE가 아닙니다."),
    JWT_ILLEGAL_ARGUMENT("JWT 토큰의 구성 요소가 올바르지 않습니다."),
    JWT_EMAIL_IS_NULL("해당 JWT 토큰의 식별자인 email이 null입니다."),
    JWT_INVALID_HEADER("Header의 형식이 올바르지 않습니다."),

    // SecurityException
    SECURITY_USER_NOT_FOUND("해당 email을 가진 사용자를 찾을 수 없습니다."),

    // OAuthException
    OAUTH_INVALID_TOKEN_URL("token URL이 올바르지 않습니다."),
    OAUTH_INVALID_ACCESS_TOKEN("access_token이 올바르지 않습니다."),
    OAUTH_CONFIG_NULL("application.yml 파일에서 속성 값을 읽어오지 못했습니다.")

    ;
    private final String text;
}