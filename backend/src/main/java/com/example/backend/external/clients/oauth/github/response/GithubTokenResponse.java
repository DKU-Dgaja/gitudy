package com.example.backend.external.clients.oauth.github.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

/*
    Github에 Access Token을 요청 후 반환받을 DTO
 */
@Getter
@NoArgsConstructor
public class GithubTokenResponse {
    private String access_token;

    public GithubTokenResponse(String access_token) {
        this.access_token = access_token;
    }
}
