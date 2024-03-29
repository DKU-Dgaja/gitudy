package com.example.backend.auth.api.service.token;

import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.jwt.JwtException;
import com.example.backend.domain.define.refreshToken.RefreshToken;
import com.example.backend.domain.define.refreshToken.repository.RefreshTokenRepository;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserRole;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import com.example.backend.auth.TestConfig;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RefreshTokenServiceTest extends TestConfig {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;
    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }
    @Test
    @DisplayName("Refresh Token 저장 & 조회 테스트")
    void redisRefreshTokenGenerate() {
        // given
        String testRefreshToken="testToken";

        String testSubject = "KAKAO_1234";

        RefreshToken saveToken = RefreshToken.builder()
                .refreshToken(testRefreshToken)
                .subject(testSubject)

                .build();

        refreshTokenService.saveRefreshToken(saveToken);

        // when
        Optional<RefreshToken> testToken = refreshTokenRepository.findById(testRefreshToken);

        // then
        assertThat(testToken.get().getRefreshToken()).isEqualTo(testRefreshToken);

        assertThat(testToken.get().getSubject()).isEqualTo(testSubject);
    }
    @Test
    @DisplayName("Redis에 존재하지 않는 Refresh Token으로 요청할 경우 JWT_NOT_EXIST_RTK 예외가 발생한다.")
    void ReissueWhenRedisNotExistRefreshTokenTest() {
        // given
        User user = User.builder()
                .name("구영민")
                .role(UserRole.USER)
                .platformType(UserPlatformType.KAKAO)
                .platformId("1234")
                .profileImageUrl("https://kakao.com")
                .build();
        User savedUser = userRepository.save(user);

        HashMap<String, String> map = new HashMap<>();
        map.put("role", savedUser.getRole().name());
        map.put("platformId", savedUser.getPlatformId());
        map.put("platformType", String.valueOf(savedUser.getPlatformType()));

        String refreshToken = jwtService.generateRefreshToken(map, savedUser);

        refreshTokenService.saveRefreshToken(RefreshToken.builder()
                .refreshToken(refreshToken)
                .subject(savedUser.getUsername())
                .build());

        Claims claims = jwtService.extractAllClaims(refreshToken);
        // when : refresh 토큰 삭제
        refreshTokenRepository.deleteById(refreshToken);

        // then
        JwtException exception = assertThrows(JwtException.class,
                () -> refreshTokenService.reissue(claims, refreshToken));
        assertThat(exception.getMessage()).isEqualTo(ExceptionMessage.JWT_NOT_EXIST_RTK.getText());

    }
}