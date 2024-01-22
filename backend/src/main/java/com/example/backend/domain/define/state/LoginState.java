package com.example.backend.domain.define.state;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Getter
@ToString
@RedisHash(value = "state", timeToLive = 60 * 3) // 3분
public class LoginState {

    @Id
    private String state;  // state 검증


    @Builder
    private LoginState() {
    }
}
