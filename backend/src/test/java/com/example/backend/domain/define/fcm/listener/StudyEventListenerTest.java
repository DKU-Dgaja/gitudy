package com.example.backend.domain.define.fcm.listener;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.fcm.repository.FcmTokenRepository;
import com.example.backend.domain.define.study.info.StudyEventFixture;
import com.example.backend.domain.define.study.info.event.ApplyApproveRefuseMemberEvent;
import com.example.backend.domain.define.study.info.event.ApplyMemberEvent;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.google.firebase.messaging.FirebaseMessaging;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
public class StudyEventListenerTest extends TestConfig {
    @Mock
    private FcmTokenRepository fcmTokenRepository;

    @InjectMocks
    private StudyEventListener studyEventListener;

    @Mock
    private FcmService fcmService;

    @Mock
    private FirebaseMessaging firebaseMessaging;


    @AfterEach()
    void tearDown() {
        fcmTokenRepository.deleteAll();
    }

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
        studyEventListener.applyMemberListener(applyMemberEvent);

        // then
        verify(fcmService).sendMessageSingleDevice(any(FcmSingleTokenRequest.class)); // sendMessageSingleDevice 호출 검증
    }

    @Test
    @DisplayName("스터디 가입 신청 승인/거부 리스너 테스트")
    void apply_approve_refuse_test() throws Exception {
        // given
        Long applyUserId = 1L;

        ApplyApproveRefuseMemberEvent applyApproveRefuseMemberEvent = StudyEventFixture.generateApplyApproveRefuseMemberEvent(applyUserId);

        FcmToken fcmToken = FcmFixture.generateDefaultFcmToken(applyUserId);

        when(fcmService.findFcmTokenByIdOrThrowException(applyUserId)).thenReturn(fcmToken);

        when(firebaseMessaging.send(any())).thenReturn("메시지 전송 완료");

        // when
        studyEventListener.applyApproveRefuseMemberListener(applyApproveRefuseMemberEvent);

        // then
        verify(fcmService).sendMessageSingleDevice(any(FcmSingleTokenRequest.class)); // sendMessageSingleDevice 호출 검증
    }
}
