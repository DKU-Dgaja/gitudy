package com.example.backend.auth.config.oauth;

import lombok.Getter;
import lombok.Setter;
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

    // 객체를 생성하지 않고도 값을 읽어올 수 있도록 static으로 설정
    @Getter
    @Setter  // Setter를 사용해 자동으로 바인딩해주는 거라서 필수
    public static class Client {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
    }

    @Getter
    @Setter
    public static class Provider {
        private String tokenUri;
        private String authorizationUri;
        private String profileUri;
    }
}
