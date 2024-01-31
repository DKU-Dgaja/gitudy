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
    JWT_SUBJECT_IS_NULL("해당 JWT 토큰의 식별자가 null입니다."),
    JWT_INVALID_HEADER("Header의 형식이 올바르지 않습니다."),
    JWT_NOT_EXIST_RTK("Refresh Token이 존재하지 않습니다."),
    JWT_INVALID_RTK("Refresh Token의 형식이 올바르지 않습니다."),

    // Refreshtoken
    REFRESHTOKEN_NOT_EXIST("Refresh Token이 존재하지 않습니다."),
    REFRESHTOKEN_INVALID("유효하지 않은 Refresh Token 입니다."),


    // OAuthException
    OAUTH_INVALID_TOKEN_URL("token URL이 올바르지 않습니다."),
    OAUTH_INVALID_ACCESS_TOKEN("access_token이 올바르지 않습니다."),
    OAUTH_CONFIG_NULL("application.yml 파일에서 속성 값을 읽어오지 못했습니다."),

    // LoginState
    LOGINSTATE_IS_NOT_USE("해당 LoginState를 사용할 수 없습니다."),
    LOGINSTATE_INVALID_VALUE("LoginState 정보가 잘못되었습니다."),
    LOGINSTATE_NOT_FOUND("LoginState를 찾을 수 없습니다."),

    // AuthException
    UNAUTHORIZED_AUTHORITY("잘못된 권한 입니다.")

    ;
    private final String text;
}
