package com.example.backend.domain.define.study.github;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Builder
@RedisHash(value = "githubToken")
public record GithubApiToken (
        @Id String githubApiToken,
        @Indexed Long userId
){
}
