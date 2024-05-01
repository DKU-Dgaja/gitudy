package com.example.backend.domain.define.fcm.listener;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.info.StudyEventFixture;
import com.example.backend.domain.define.study.info.event.ApplyMemberEvent;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.google.firebase.messaging.FirebaseMessaging;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
public class ApplyMemberListenerTest extends TestConfig {

    @InjectMocks
    private ApplyMemberListener applyMemberListener;

    @Mock
    private FcmService fcmService;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Test
    @DisplayName("스터디 가입 신청 리스너 테스트")
    void apply_test() throws Exception {
        // given
        Long leaderId = 1L;

        ApplyMemberEvent applyMemberEvent = StudyEventFixture.generateApplyMemberEvent(leaderId);

        FcmToken fcmToken = FcmFixture.generateDefaultFcmToken(leaderId);

        when(fcmService.findFcmTokenByIdOrThrowException(leaderId)).thenReturn(fcmToken);

        when(firebaseMessaging.send(any())).thenReturn("메시지 전송 완료");

        // when
        applyMemberListener.applyMemberListener(applyMemberEvent);

        // then
        verify(fcmService).sendMessageSingleDevice(any(FcmSingleTokenRequest.class)); // sendMessageSingleDevice 호출 검증
    }
}
