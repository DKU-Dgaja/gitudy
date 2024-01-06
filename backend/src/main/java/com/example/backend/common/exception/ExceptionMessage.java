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
    JWT_USER_NOT_FOUND("해당 JWT 토큰으로 사용자를 찾을 수 없습니다."),
    JWT_INVALID_HEADER("Header의 형식이 올바르지 않습니다."),

    // SecurityException
    SECURITY_USER_NOT_FOUND("해당 email을 가진 사용자를 찾을 수 없습니다.");

    private final String text;
}
