package com.example.backend.auth.config.security;

import com.example.backend.auth.config.security.filter.JwtAuthenticationFilter;
import com.example.backend.auth.config.security.provider.CustomAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // AuthenticationManager(ProviderManager) 획득 -> 여러 개의 Provider를 등록할 수 있다.
        ProviderManager authenticationManager = (ProviderManager) authenticationConfiguration.getAuthenticationManager();

        // 생성한 Provider를 추가
        authenticationManager.getProviders().add(authenticationProvider);

        return authenticationManager;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeHttpRequest ->
                        authorizeHttpRequest
//                                .anyRequest().permitAll()
                                .requestMatchers("/test").permitAll()
                                .anyRequest().hasAnyAuthority("USER", "ADMIN")
                )
                .sessionManagement((sessionManagement) ->
                        sessionManagement
                                // JWT 토큰 기반의 인증을 사용하기 위해 무상태 세션 정책 사용
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}