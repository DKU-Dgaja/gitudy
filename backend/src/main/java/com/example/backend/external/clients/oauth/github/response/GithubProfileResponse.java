package com.example.backend.external.clients.oauth.github.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
    Github에 사용자 정보를 요청 후 반환받을 DTO
 */
@Getter
@NoArgsConstructor
public class GithubProfileResponse {
    private Long id;
    private String login;                   // 사용자 닉네임 ex) jusung-c
    private String name;                    // 사용자 이름 ex) 이주성
    private String email;                   // 사용자 이메일
    private String avatar_url;              // 썸네일 이미지
    private String html_url;                // 사용자 깃허브 주소

    @Builder
    public GithubProfileResponse(Long id, String login, String name, String email, String avatar_url, String html_url) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.email = email;
        this.avatar_url = avatar_url;
        this.html_url = html_url;
    }
}
