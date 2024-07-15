package com.example.backend.auth.api.service.jwt;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        User savedUser = userRepository.save(generateAuthUser());
        String role = savedUser.getRole().name();
        String platformId = savedUser.getPlatformId();
        String platformType = String.valueOf(savedUser.getPlatformType());

        String expectedSubject = savedUser.getPlatformId() + "_" + savedUser.getPlatformType();

        HashMap<String, String> map = new HashMap<>();
        map.put("role", role);
        map.put("platformId", platformId);
        map.put("platformType", platformType);


        // when
        String atk = jwtService.generateAccessToken(map, savedUser);
        Claims claims = jwtService.extractAllClaims(atk);

        // then
        assertAll(
                () -> assertThat(claims.getSubject()).isEqualTo(expectedSubject),
                () -> assertThat(claims.get("role")).isEqualTo(role),
                () -> assertThat(claims.get("platformId")).isEqualTo(platformId),
                () -> assertThat(claims.get("platformType")).isEqualTo(platformType)

        );
    }

    @Test
    @DisplayName("JWT Access 토큰에서 Subject(식별자)를 추출한다.")
    void extractSubjectTest() {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        String platformId = savedUser.getPlatformId();
        String platformType = String.valueOf(savedUser.getPlatformType());
        String role = savedUser.getRole().name();

        String expectedSubject = savedUser.getPlatformId() + "_" + savedUser.getPlatformType();

        HashMap<String, String> map = new HashMap<>();
        map.put("role", role);
        map.put("platformId", platformId);
        map.put("platformType", platformType);


        // when
        String atk = jwtService.generateAccessToken(map, savedUser);
        String subject = jwtService.extractSubject(atk);

        // then
        assertThat(subject).isEqualTo(expectedSubject);

    }

    @Test
    @DisplayName("JWT Access 토큰에서 만료 일자를 추출한다.")
    void extractExpirationTest() {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        String role = savedUser.getRole().name();
        String platformId = savedUser.getPlatformId();
        String platformType = String.valueOf(savedUser.getPlatformType());

        HashMap<String, String> map = new HashMap<>();
        map.put("role", role);
        map.put("platformId", platformId);
        map.put("platformType", platformType);

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
        User savedUser = userRepository.save(generateAuthUser());
        String role = savedUser.getRole().name();
        String platformId = savedUser.getPlatformId();
        String platformType = String.valueOf(savedUser.getPlatformType());

        String subject = savedUser.getUsername();

        HashMap<String, String> map = new HashMap<>();
        map.put("role", role);
        map.put("platformId", platformId);
        map.put("platformType", platformType);

        // when
        String atk = jwtService.generateAccessToken(map, savedUser);
        Claims claims = jwtService.extractAllClaims(atk);
        boolean result = jwtService.isTokenValid(atk, subject);

        // then
        assertThat(result).isTrue();
        assertAll(
                () -> assertThat(claims.getSubject()).isEqualTo(subject),
                () -> assertThat(claims.get("role")).isEqualTo(role),
                () -> assertThat(claims.get("platformId")).isEqualTo(platformId),
                () -> assertThat(claims.get("platformType")).isEqualTo(platformType)
        );
    }

    @Test
    @DisplayName("Claims를 지정해 refresh 토큰을 생성한다.")
    void generateRefreshTokenTest() {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        String role = savedUser.getRole().name();
        String platformId = savedUser.getPlatformId();
        String platformType = String.valueOf(savedUser.getPlatformType());


        HashMap<String, String> map = new HashMap<>();
        map.put("role", role);
        map.put("platformId", platformId);
        map.put("platformType", platformType);

        // when
        String atk = jwtService.generateRefreshToken(map, savedUser);
        Claims claims = jwtService.extractAllClaims(atk);
        boolean result = jwtService.isTokenValid(atk, platformId+"_"+platformType);

        // then
        assertThat(result).isTrue();
        assertAll(
                () -> assertThat(claims.getSubject()).isEqualTo(platformId+"_"+platformType),
                () -> assertThat(claims.get("role")).isEqualTo(role),
                () -> assertThat(claims.get("platformId")).isEqualTo(platformId),
                () -> assertThat(claims.get("platformType")).isEqualTo(platformType)
        );
    }

    @Test
    @DisplayName("요청의 JWT 토큰이 올바르지 않은 형식일 경우 토큰 검증에 실패한다.")
    void isTokenIllegal() {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        String role = savedUser.getRole().name();
        String platformId = savedUser.getPlatformId();
        String platformType = String.valueOf(savedUser.getPlatformType());
        String subject = savedUser.getUsername();

        HashMap<String, String> map = new HashMap<>();
        map.put("role", role);
        map.put("platformId", platformId);
        // map.put("platformType", platformType);


        // when
        String atk = jwtService.generateAccessToken(map, savedUser);
        boolean result = jwtService.isTokenValid(atk, subject);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("요청의 JWT 토큰의 식별자와 요청자의 식별자가 다를 경우 토큰 검증에 실패한다.")
    void isTokenInvalid() {
        // given
        String s = "Another Requestor";

        User savedUser = userRepository.save(generateAuthUser());
        String role = savedUser.getRole().name();
        String platformId = savedUser.getPlatformId();
        String platformType = String.valueOf(savedUser.getPlatformType());

        HashMap<String, String> map = new HashMap<>();
        map.put("role", role);
        map.put("platformId", platformId);
        map.put("platformType", platformType);

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
        User savedUser = userRepository.save(generateAuthUser());
        String role = savedUser.getRole().name();
        String platformId = savedUser.getPlatformId();
        String platformType = String.valueOf(savedUser.getPlatformType());


        HashMap<String, String> map = new HashMap<>();
        map.put("role", role);
        map.put("platformId", platformId);
        map.put("platformType", platformType);

        // when
        // 만료된 시간으로 설정된 JWT 토큰 생성
        String expiredToken = jwtService.generateAccessToken(map, savedUser, new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24));

        // then
        assertThrows(ExpiredJwtException.class,
                () -> jwtService.isTokenValid(expiredToken, platformId+"_"+platformType));
    }
    @Test
    void 무제한_토큰_만들기() {
        String key = "3e46db1c90098ae0dc37ea641d11f79f1f3f0f10e2624471e0c643f3696bd12662b6e6623b49440b74447f243c4340bdf9a2532ee557818fbd5258348879afec";

        SecretKey secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(key));

        Map<String, String> claims = new HashMap<>();
        claims.put("role", "USER");
        claims.put("platformId", "149556660");
        claims.put("platformType", "GITHUB");

        String sub = "149556660_GITHUB";

        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date(1735689600000L); // 2025년 1월 1일 (Unix 시간 1735689600000 밀리초)

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(sub)  // User의 식별자
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();

        System.out.println("Generated Token: " + token);
    }
}