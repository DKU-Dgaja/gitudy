package com.example.backend.domain.define.fcm.listener;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.member.event.WithdrawalMemberEvent;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("NonAsciiCharacters")
class WithdrawalMemberListenerTest extends TestConfig {

    @InjectMocks
    private WithdrawalMemberListener withdrawalMemberListener;

    @Mock
    private FcmService fcmService;


    @Test
    void 스터디_탈퇴_알림_리스너_테스트() throws FirebaseMessagingException {
        // given
        Long resignMemberId = 2L;

        WithdrawalMemberEvent mockEvent = WithdrawalMemberEvent.builder()
                .studyLeaderId(resignMemberId)
                .withdrawalMemberName("name")
                .studyInfoTopic("Topic")
                .build();

        FcmToken mockFcmTokenObj = FcmFixture.generateDefaultFcmToken(resignMemberId);

        when(fcmService.findFcmTokenByIdOrThrowException(resignMemberId)).thenReturn(mockFcmTokenObj);

        // when
        withdrawalMemberListener.withdrawalMemberListener(mockEvent);

        // then
        verify(fcmService).sendMessageSingleDevice(any(FcmSingleTokenRequest.class));
    }
}