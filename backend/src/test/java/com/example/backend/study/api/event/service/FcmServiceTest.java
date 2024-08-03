package com.example.backend.study.api.event.service;

import com.example.backend.MockTestConfig;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.domain.define.fcm.repository.FcmTokenRepository;
import com.example.backend.study.api.event.FcmMultiTokenRequest;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.controller.request.FcmTokenSaveRequest;
import com.google.firebase.messaging.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
class FcmServiceTest extends MockTestConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FcmService fcmService;

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @MockBean
    private FirebaseMessaging firebaseMessaging;


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


    @Test
    @DisplayName("Fcm single 테스트")
    public void FcmSingleTest() throws FirebaseMessagingException {
        // given
        FcmSingleTokenRequest fcmSingleTokenRequest = FcmFixture.generateFcmSingleTokenRequest();

        when(firebaseMessaging.send(any())).thenReturn("메시지 전송 완료");

        // when
        fcmService.sendMessageSingleDevice(fcmSingleTokenRequest);

        // then
        verify(firebaseMessaging).send(any(Message.class));
    }

    @Test
    @DisplayName("Fcm multi 테스트")
    public void FcmMultiTest() throws FirebaseMessagingException {
        // given
        FcmMultiTokenRequest fcmMultiTokenRequest = FcmFixture.generateFcmMultiTokenRequest();

        BatchResponse mockResponse = Mockito.mock(BatchResponse.class);
        when(mockResponse.getSuccessCount()).thenReturn(3); // ex) 토큰 3개일때
        when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class))).thenReturn(mockResponse);


        // when
        fcmService.sendMessageMultiDevice(fcmMultiTokenRequest);

        // then
        verify(firebaseMessaging, times(1)).sendEachForMulticast(any());
    }
}