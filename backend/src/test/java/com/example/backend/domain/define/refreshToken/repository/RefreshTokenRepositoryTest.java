package com.example.backend.domain.define.refreshToken.repository;

import com.example.backend.domain.define.refreshToken.RefreshToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RefreshTokenRepositoryTest {
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
        RefreshToken saveToken = refreshTokenRepository.save(RefreshToken.builder().refreshToken("testToken").email("test@naver.com").build());

        // when
        RefreshToken refreshToken = refreshTokenRepository.findById(saveToken.getRefreshToken()).get();

        // then
        assertThat(refreshToken.getRefreshToken()).isEqualTo(saveToken.getRefreshToken());
        assertThat(refreshToken.getEmail()).isEqualTo(saveToken.getEmail());
    }
}