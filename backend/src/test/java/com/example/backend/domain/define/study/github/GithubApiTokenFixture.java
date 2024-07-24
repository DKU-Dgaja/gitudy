package com.example.backend.domain.define.study.github;

public class GithubApiTokenFixture {
    public static GithubApiToken createToken(String token, Long userId) {
        return new GithubApiToken(token, userId);
    }
}
