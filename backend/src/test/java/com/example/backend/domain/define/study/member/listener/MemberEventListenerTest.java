package com.example.backend.domain.define.study.member.listener;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.domain.define.fcmToken.FcmToken;
import com.example.backend.domain.define.fcmToken.repository.FcmTokenRepository;
import com.example.backend.domain.define.study.member.MemberEventFixture;
import com.example.backend.domain.define.study.member.listener.event.ResignMemberEvent;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("NonAsciiCharacters")
class MemberEventListenerTest extends TestConfig {

    @Mock
    private FcmTokenRepository fcmTokenRepository;

    @InjectMocks
    private MemberEventListener memberEventListener;

    @Mock
    private FcmService fcmService;

    @AfterEach()
    void tearDown() {
        fcmTokenRepository.deleteAll();
    }

    @Test
    void 스터디_강퇴_리스너_테스트() throws FirebaseMessagingException {
        // given
        Long resignMemberId = 2L;

        ResignMemberEvent mockEvent = MemberEventFixture.generateApplyMemberEvent(resignMemberId);
        FcmToken mockFcmTokenObj = FcmFixture.generateDefaultFcmToken(resignMemberId);

        when(fcmService.findFcmTokenByIdOrThrowException(resignMemberId)).thenReturn(mockFcmTokenObj);

        // when
        memberEventListener.resignMemberListener(mockEvent);

        // then
        verify(fcmService).sendMessageSingleDevice(any(FcmSingleTokenRequest.class)); // sendMessageSingleDevice 호출 검증
    }
}