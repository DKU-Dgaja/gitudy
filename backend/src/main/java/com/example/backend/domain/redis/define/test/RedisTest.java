package com.example.backend.domain.redis.define.test;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@RedisHash(value = "test", timeToLive = 60 * 3) // 3분
public class RedisTest {
    @Id
    private Long id;

    private String description;

    @Builder
    public RedisTest(String description) {
        this.description = description;
    }
}
