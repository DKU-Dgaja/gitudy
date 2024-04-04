package com.example.backend.domain.define.fcm;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@ToString
@RedisHash(value = "fcm")
@NoArgsConstructor
@Builder
public class FcmToken {
    @Id
    private Long userId;
    private String fcmToken;

    public FcmToken(Long userId, String fcmToken) {
        this.userId = userId;
        this.fcmToken = fcmToken;
    }
}
