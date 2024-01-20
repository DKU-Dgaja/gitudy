package com.example.backend.domain.define.state;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@Getter
@ToString
@RedisHash(value = "state", timeToLive = 60 * 10) // 10분
public class LoginState {

    @Id
    private UUID state;  // state 검증

    private  boolean isUse; // 사용 가능 여부

    @Builder
    private LoginState(boolean isUse) {
        this.isUse = isUse;
    }
}
