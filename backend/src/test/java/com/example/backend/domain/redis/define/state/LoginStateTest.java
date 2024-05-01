package com.example.backend.domain.redis.define.state;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.state.LoginState;
import com.example.backend.domain.define.state.LoginStateRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class LoginStateTest extends TestConfig {
    @Autowired
    private LoginStateRepository loginStateRepository;

    @AfterEach  // 테스트 후 데이터 삭제
    void tearDown() {
        loginStateRepository.deleteAll();
    }

    @Test
    @DisplayName("LoginState 저장")
    void redisLoginStateSave() {
        // given
        LoginState savedEntity = loginStateRepository.save(LoginState.builder()
                .build());

        // when
        Optional<LoginState> byId = loginStateRepository.findById(savedEntity.getState());

        // then
        assertThat(byId).isPresent();
        assertThat(byId.get().getState()).isEqualTo(savedEntity.getState());

    }

    @Test
    @DisplayName("LoginState 삭제")
    void redisLoginStateDelete() {
        // given
        LoginState savedEntity = loginStateRepository.save(LoginState.builder()
                .build());

        loginStateRepository.deleteById(savedEntity.getState());

        // when
        Optional<LoginState> findLoginState = loginStateRepository.findById(savedEntity.getState());

        // then
        assertThat(findLoginState.isEmpty()).isTrue();
    }
}
