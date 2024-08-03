package com.example.backend.auth.api.service.token;

import com.example.backend.TestConfig;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.jwt.JwtException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserRole;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.refreshToken.RefreshToken;
import com.example.backend.domain.define.refreshToken.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Optional;

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.KAKAO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
    }

    @Test
    @DisplayName("Refresh Token 저장 & 조회 테스트")
    void redisRefreshTokenGenerate() {
        // given
        String testRefreshToken = "testToken";

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
                .platformType(KAKAO)
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


    @Test
    void Subject로_토큰찾기() {
        // given
        RefreshToken saveToken = refreshTokenRepository.save(RefreshToken.builder()
                .refreshToken("redis_Token")
                .subject("12345_GITHUB")
                .build());

        // when
        Optional<RefreshToken> rtk = refreshTokenRepository.findBySubject(saveToken.getSubject());

        // then
        assertThat(rtk).isNotEmpty();


        assertThat(rtk.get().getRefreshToken()).isEqualTo(saveToken.getRefreshToken());
        assertThat(rtk.get().getSubject()).isEqualTo(saveToken.getSubject());
    }

    @Test
    void 이미_존재하는_토큰이면_삭제_테스트() {
        // given
        RefreshToken saveToken = refreshTokenRepository.save(RefreshToken.builder()
                .refreshToken("이미 존재하는 RefreshToken")
                .subject("12345_GITHUB")
                .build());

        // when
        Optional<RefreshToken> rtk = refreshTokenRepository.findBySubject(saveToken.getSubject());
        refreshTokenRepository.delete(rtk.get());

        // then
        Optional<RefreshToken> deletedToken = refreshTokenRepository.findBySubject(saveToken.getSubject());
        assertFalse(deletedToken.isPresent());
    }

    @Test
    void 이미_토큰이_존재하면_예외처리_하지않고_로그() {
        // given
        User user = User.builder()
                .name("이정우")
                .role(UserRole.USER)
                .platformType(KAKAO)
                .platformId("1234")
                .profileImageUrl("https://kakao.com")
                .build();
        User savedUser = userRepository.save(user);


        HashMap<String, String> map = new HashMap<>();
        map.put("role", savedUser.getRole().name());
        map.put("platformId", savedUser.getPlatformId());
        map.put("platformType", String.valueOf(savedUser.getPlatformType()));

        String accessToken = jwtService.generateAccessToken(map, savedUser);


        // when
        String sub = jwtService.extractAllClaims(accessToken).getSubject();

        Optional<RefreshToken> rtk = refreshTokenRepository.findBySubject(sub);

        rtk.ifPresentOrElse(
                refreshToken -> {

                },
                () -> System.out.print("이미_토큰이_존재하면_예외처리_하지않고_로그")
        );
    }
}