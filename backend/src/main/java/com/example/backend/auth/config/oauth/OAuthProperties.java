package com.example.backend.auth.config.oauth;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/*
 * application.yml 파일에서 "oauth2"로 시작하는 속성 값들을 읽어온다.
 * Clients, Provider 2개의 Map으로 바인딩
 */
@Getter
@ConfigurationProperties(prefix = "oauth2")
public class OAuthProperties {
    private final Map<String, Client> client = new HashMap<>();
    private final Map<String, Provider> provider = new HashMap<>();

    // 불변 객체로 속성 값을 읽어오기 위해 record로 선언
    // 각 필드는 반드시 있어야 하는 불변 값이므로 @NonNull을 적용한다. -> NullPointerException 방지
    public record Client(@NonNull String clientId, @NonNull String clientSecret, @NonNull String redirectUri) {
    }

    public record Provider(@NonNull String tokenUri, @NonNull String authorizationUri, @NonNull String profileUri) {
    }
}
