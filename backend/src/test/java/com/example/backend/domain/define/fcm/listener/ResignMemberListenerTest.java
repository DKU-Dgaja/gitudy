package com.example.backend.domain.define.fcm.listener;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.fcm.repository.FcmTokenRepository;
import com.example.backend.domain.define.study.member.MemberEventFixture;
import com.example.backend.domain.define.study.member.event.ResignMemberEvent;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.example.backend.study.api.event.service.NoticeService;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
class ResignMemberListenerTest extends TestConfig {

    @Mock
    private FcmTokenRepository fcmTokenRepository;

    @InjectMocks
    private ResignMemberListener resignMemberListener;

    @Mock
    private NoticeService noticeService;

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

        doNothing().when(noticeService).ResignMemberNotice(any(ResignMemberEvent.class));

        // when
        resignMemberListener.resignMemberListener(mockEvent);

        // then
        verify(fcmService).sendMessageSingleDevice(any(FcmSingleTokenRequest.class)); // sendMessageSingleDevice 호출 검증
    }
}