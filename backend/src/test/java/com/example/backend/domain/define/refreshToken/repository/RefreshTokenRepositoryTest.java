package com.example.backend.domain.define.refreshToken.repository;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.refreshToken.RefreshToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenRepositoryTest extends TestConfig {
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
    }
    @Test
    @DisplayName("RefreshToken을 저장할 수 있다.")
    void redisLoginStateSave() {
        // given
        RefreshToken saveToken = refreshTokenRepository.save(RefreshToken.builder().refreshToken("testToken").subject("KAKAO_1234").build());

        // when
        RefreshToken refreshToken = refreshTokenRepository.findById(saveToken.getRefreshToken()).get();

        // then
        assertThat(refreshToken.getRefreshToken()).isEqualTo(saveToken.getRefreshToken());
        assertThat(refreshToken.getSubject()).isEqualTo(saveToken.getSubject());
    }
}