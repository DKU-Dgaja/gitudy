package com.example.backend.study.api.event.service;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.fcmToken.repository.FcmTokenRepository;
import com.example.backend.study.api.event.controller.request.FcmTokenSaveRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("NonAsciiCharacters")
class FcmServiceTest extends TestConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FcmService fcmService;

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        fcmTokenRepository.deleteAll();
    }

    @Test
    void FCM_token_저장_테스트() {
        // given
        String fcmToken = "FCM_token";
        User user = userRepository.save(generateAuthUser());

        FcmTokenSaveRequest token = FcmTokenSaveRequest.builder()
                .token(fcmToken)
                .build();
        // when
        fcmService.saveFcmTokenRequest(user, token);

        // then
        assertEquals(fcmTokenRepository.findById(user.getId()).get().getFcmToken(), fcmToken);
    }
}