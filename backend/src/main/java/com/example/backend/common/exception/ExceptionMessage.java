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
    UNAUTHORIZED_AUTHORITY("현재 요청한 작업을 수행할 권한이 없습니다."),

    // UserException
    USER_NOT_FOUND("데이터베이스에서 사용자를 찾을 수 없습니다."),
    USER_NAME_DUPLICATION("중복된 이름입니다."),

    // AuthException
    AUTH_INVALID_REGISTER("잘못된 회원가입 요청입니다."),
    AUTH_DUPLICATE_UNAUTH_REGISTER("중복된 회원가입 요청입니다."),
    AUTH_NOT_FOUND("계정 정보를 찾을 수 없습니다."),
    AUTH_DELETE_FAIL("계정 삭제에 실패했습니다."),

    // CommitException
    COMMIT_NOT_FOUND("커밋 정보를 찾을 수 없습니다."),
    COMMIT_COMMENT_NOT_FOUND("커밋 댓글 정보를 찾을 수 없습니다."),
    COMMIT_COMMENT_PERMISSION_DENIED("커밋 댓글 정보를 변경할 권한이 없습니다."),

    // BookmarkException
    BOOKMARK_DELETE_FAIL("북마크 삭제에 실패했습니다."),

    // TodoException
    STUDY_INFO_NOT_FOUND("해당 스터디정보를 찾을 수 없습니다."),
    STUDY_MEMBER_NOT_LEADER("스터디장이 아닙니다."),
    STUDY_NOT_MEMBER("해당 스터디 멤버가 아닙니다."),
    TODO_NOT_FOUND("해당 Todo를 찾을 수 없습니다."),

    // TodoMappingException
    STUDY_TODO_MAPPING_NOT_FOUND("해당 스터디원에 할당된 투두 정보를 찾을 수 없습니다."),

    // StudyMemberException
    USER_NOT_STUDY_MEMBER("해당 스터디원을 찾을 수 없습니다."),
    STUDY_ALREADY_MEMBER("이미 해당 스터디의 멤버 입니다."),
    STUDY_RESIGNED_MEMBER("이미 강퇴당한 스터디 입니다."),
    STUDY_WAITING_MEMBER("이미 가입신청 완료하여 승인 대기중인 스터디 입니다."),
    STUDY_WAITING_NOT_MEMBER("해당 스터디에 가입 대기중인 유저가 아닙니다."),
    STUDY_JOIN_CODE_FAIL("스터디 참여 코드가 맞지 않습니다."),
    STUDY_NOT_APPLY_LIST("해당 스터디에 가입신청이 없습니다"),

    // StudyCommentException
    STUDY_COMMENT_NOT_FOUND("해당 스터디 댓글을 찾을 수 없습니다."),
    STUDY_COMMENT_NOT_AUTHORIZED("스터디 댓글을 수정할 권한이 없습니다."),

    // StudyConventionException
    CONVENTION_NOT_FOUND("해당 스터디 컨벤션을 찾을 수 없습니다."),

    // GithubApiException
    GITHUB_API_CONNECTION_ERROR("Github Api 통신에 실패했습니다."),
    GITHUB_API_GET_REPOSITORY_ERROR("Repository 정보를 불러오는 데에 실패했습니다."),
    GITHUB_API_GET_COMMITS_ERROR("커밋 리스트를 불러오는데 실패했습니다."),
    GITHUB_API_GET_COMMIT_ERROR("커밋을 불러오는데 실패했습니다."),

    // CategoryException
    CATEGORY_NOT_FOUND("해당 카테고리를 찾을 수 없습니다."),

    // FCM Exception
    FCM_DEVICE_NOT_FOUND("해당 유저의 기기를 찾을 수 없습니다."),

    // Notice Exception
    NOTICE_NOT_FOUND("해당 알림을 찾을 수 없습니다.")
    ;
    private final String text;
}
