package com.example.backend.domain.define.study.github;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@ToString
@RedisHash(value = "github")
@NoArgsConstructor
@Builder
public class GithubApiToken {
    @Id
    private Long userId;
    private String githubApiToken;

    public GithubApiToken(Long userId, String githubApiToken) {
        this.userId = userId;
        this.githubApiToken = githubApiToken;
    }
}
