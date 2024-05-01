package com.example.backend.domain.define.fcm.repository;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.fcm.FcmToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class FcmTokenRepositoryTest extends TestConfig {
    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        fcmTokenRepository.deleteAll();
        userRepository.deleteAllInBatch();
    }
    @Test
    @DisplayName("FcmToken을 저장할 수 있다.")
    void redisLoginStateSave() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        FcmToken fcmToken = fcmTokenRepository.save(FcmToken.builder()
                .userId(user.getId())
                .fcmToken("fcmToken")
                .build());

        // when
        FcmToken savedFcmToken = fcmTokenRepository.findById(fcmToken.getUserId()).get();

        // then
        assertThat(fcmToken.getUserId()).isEqualTo(savedFcmToken.getUserId());
        assertThat(fcmToken.getFcmToken()).isEqualTo(savedFcmToken.getFcmToken());
    }
}