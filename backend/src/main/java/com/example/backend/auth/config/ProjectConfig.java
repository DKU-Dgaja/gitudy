package com.example.backend.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class ProjectConfig implements WebMvcConfigurer {

    // 암호 인코더 정의 - bcrypt 해싱 알고리즘 사용
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // JSON 직렬화/역직렬화 시 사용
    @Bean
    public ObjectMapper objectMapper() {

        return new ObjectMapper()
                // 객체의 속성 이름을 snake-case로 설정
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    // Cors 모두 오픈 (개발환경)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
