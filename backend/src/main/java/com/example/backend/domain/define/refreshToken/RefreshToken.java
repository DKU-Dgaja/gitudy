package com.example.backend.domain.define.refreshToken;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@ToString
@RedisHash(value = "refresh", timeToLive = 60 * 60 * 24 * 7) // 7일
@NoArgsConstructor
@Builder
public class RefreshToken {
    @Id
    private String refreshToken;
    private String subject;

    public RefreshToken(String refreshToken, String subject) {
        this.refreshToken = refreshToken;
        this.subject = subject;
    }
}
