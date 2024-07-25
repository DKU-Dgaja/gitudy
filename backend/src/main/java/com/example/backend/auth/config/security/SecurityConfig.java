package com.example.backend.auth.config.security;

import com.example.backend.auth.config.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeHttpRequest ->
                        authorizeHttpRequest
                                // Swagger 추가
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                                // register
                                .requestMatchers("/auth/register").hasAnyAuthority("UNAUTH")
                                // update
                                .requestMatchers("/auth/update").hasAnyAuthority("USER", "ADMIN")
                                // UnAuth Area
                                .requestMatchers("/auth/**").permitAll()
                                // Others
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