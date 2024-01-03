package com.example.backend.auth.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProviderService authenticationProvider;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // AuthenticationManager(ProviderManager) 획득 -> 여러 개의 Provider를 등록할 수 있다.
        ProviderManager authenticationManager = (ProviderManager) authenticationConfiguration.getAuthenticationManager();

        // 생성한 Provider를 추가
        authenticationManager.getProviders().add(authenticationProvider);

        return authenticationManager;
    }

}