package com.example.backend.auth.api.service.jwt;

import com.example.backend.auth.TestConfig;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.jwt.JwtException;
import com.example.backend.domain.define.user.User;
import com.example.backend.domain.define.user.constant.UserRole;
import com.example.backend.domain.define.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtServiceTest extends TestConfig {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("JWT Access 토큰에서 모든 Claims를 추출한다.")
    void extractAllClaimsTest() {
        // given
        User savedUser = userRepository.save(generateUser());

        HashMap<String, String> map = new HashMap<>();
        map.put("role", savedUser.getRole().name());
        map.put("name", savedUser.getName());
        map.put("profileImageUrl", savedUser.getProfileImageUrl());

        // when
        String atk = jwtService.generateAccessToken(map, savedUser);
        Claims claims = jwtService.extractAllClaims(atk);

        // then
        assertAll(
                () -> assertThat(claims.getSubject()).isEqualTo("hong@kakao.com"),
                () -> assertThat(claims.get("role")).isEqualTo(UserRole.USER.name()),
                () -> assertThat(claims.get("name")).isEqualTo("홍길동"),
                () -> assertThat(claims.get("profileImageUrl")).isEqualTo("https://google.com")
        );
    }

    @Test
    @DisplayName("JWT Access 토큰에서 Subject(식별자)를 추출한다.")
    void extractSubjectTest() {
        // given
        User savedUser = userRepository.save(generateUser());

        HashMap<String, String> map = new HashMap<>();
        map.put("role", savedUser.getRole().name());
        map.put("name", savedUser.getName());
        map.put("profileImageUrl", savedUser.getProfileImageUrl());

        // when
        String atk = jwtService.generateAccessToken(map, savedUser);
        String subject = jwtService.extractSubject(atk);

        // then
        assertThat(subject).isEqualTo(savedUser.getUsername());

    }

    @Test
    @DisplayName("JWT Access 토큰에서 만료 일자를 추출한다.")
    void extractExpirationTest() {
        // given
        User savedUser = userRepository.save(generateUser());

        HashMap<String, String> map = new HashMap<>();
        map.put("role", savedUser.getRole().name());
        map.put("name", savedUser.getName());
        map.put("profileImageUrl", savedUser.getProfileImageUrl());

        // when
        String atk = jwtService.generateAccessToken(map, savedUser);
        Date expiration = jwtService.extractExpiration(atk);

        // then
        assertThat(expiration).isAfter(new Date());
    }

    @Test
    @DisplayName("Claims를 지정해 JWT Access 토큰을 생성한다.")
    void generateAccessTokenTest() {
        // given
        User savedUser = userRepository.save(generateUser());

        HashMap<String, String> map = new HashMap<>();
        map.put("role", savedUser.getRole().name());
        map.put("name", savedUser.getName());
        map.put("profileImageUrl", savedUser.getProfileImageUrl());

        // when
        String atk = jwtService.generateAccessToken(map, savedUser);
        Claims claims = jwtService.extractAllClaims(atk);
        boolean result = jwtService.isTokenValid(atk, savedUser.getUsername());

        // then
        assertThat(result).isTrue();
        assertAll(
                () -> assertThat(claims.getSubject()).isEqualTo("hong@kakao.com"),
                () -> assertThat(claims.get("role")).isEqualTo(UserRole.USER.name()),
                () -> assertThat(claims.get("name")).isEqualTo("홍길동"),
                () -> assertThat(claims.get("profileImageUrl")).isEqualTo("https://google.com")
        );
    }

    @Test
    @DisplayName("요청의 JWT 토큰이 올바르지 않은 형식일 경우 JWT_ILLEGAL_ARGUMENT 예외가 발생한다.")
    void isTokenIllegal() {
        // given
        User savedUser = userRepository.save(generateUser());

        HashMap<String, String> map = new HashMap<>();
//        map.put("role", savedUser.getRole().name());
        map.put("name", savedUser.getName());
        map.put("profileImageUrl", savedUser.getProfileImageUrl());

        // when
        String atk = jwtService.generateAccessToken(map, savedUser);

        // then
        JwtException exception = assertThrows(JwtException.class,
                () -> jwtService.isTokenValid(atk, savedUser.getUsername()));
        assertThat(exception.getMessage()).isEqualTo(ExceptionMessage.JWT_ILLEGAL_ARGUMENT.getText());

    }

    @Test
    @DisplayName("요청의 JWT 토큰의 식별자와 요청자의 식별자가 다를 경우 토큰 검증에 실패한다.")
    void isTokenInvalid() {
        // given
        String s = "Another Requestor";
        User savedUser = userRepository.save(generateUser());

        HashMap<String, String> map = new HashMap<>();
        map.put("role", savedUser.getRole().name());
        map.put("name", savedUser.getName());
        map.put("profileImageUrl", savedUser.getProfileImageUrl());

        // when
        String atk = jwtService.generateAccessToken(map, savedUser);
        boolean result = jwtService.isTokenValid(atk, s);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("요청의 JWT 토큰이 만료되었을 경우 토큰 검증에 실패한다.")
    void isTokenExpired() {
        // given
        User savedUser = userRepository.save(generateUser());

        HashMap<String, String> map = new HashMap<>();
        map.put("role", savedUser.getRole().name());
        map.put("name", savedUser.getName());
        map.put("profileImageUrl", savedUser.getProfileImageUrl());

        // when
        // 만료된 시간으로 설정된 JWT 토큰 생성
        String expiredToken = jwtService.generateAccessToken(map, savedUser, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24));

        // then
        assertThrows(ExpiredJwtException.class,
                () -> jwtService.isTokenValid(expiredToken, savedUser.getUsername()));
    }
}