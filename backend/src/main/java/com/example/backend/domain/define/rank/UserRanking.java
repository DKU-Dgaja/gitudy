package com.example.backend.domain.define.rank;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@ToString
@RedisHash(value = "user_ranking")
@NoArgsConstructor
@Builder
public class UserRanking {
    @Id
    private Long userId;

    private int score;

    public UserRanking(Long userId, int score) {
        this.userId = userId;
        this.score = score;
    }
}
