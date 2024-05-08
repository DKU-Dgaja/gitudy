package com.example.backend.domain.define.fcm.listener;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.member.MemberEventFixture;
import com.example.backend.domain.define.study.member.event.WithdrawalMemberEvent;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.example.backend.study.api.event.service.NoticeService;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
class WithdrawalMemberListenerTest extends TestConfig {

    @InjectMocks
    private WithdrawalMemberListener withdrawalMemberListener;

    @Mock
    private FcmService fcmService;

    @Mock
    private NoticeService noticeService;


    @Test
    void 스터디_탈퇴_알림_리스너_테스트() throws FirebaseMessagingException {
        // given
        Long leaderId = 2L;

        WithdrawalMemberEvent mockEvent = MemberEventFixture.generateWithdrawalMemberEvent(leaderId);
        FcmToken mockFcmTokenObj = FcmFixture.generateDefaultFcmToken(leaderId);

        when(fcmService.findFcmTokenByIdOrThrowException(leaderId)).thenReturn(mockFcmTokenObj);

        doNothing().when(noticeService).WithdrawalMemberNotice(any(WithdrawalMemberEvent.class));

        // when
        withdrawalMemberListener.withdrawalMemberListener(mockEvent);

        // then
        verify(fcmService).sendMessageSingleDevice(any(FcmSingleTokenRequest.class));
    }
}