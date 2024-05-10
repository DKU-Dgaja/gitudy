package com.example.backend.domain.define.fcm.listener;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.member.MemberEventFixture;
import com.example.backend.domain.define.study.member.event.NotifyLeaderEvent;
import com.example.backend.domain.define.study.member.event.NotifyMemberEvent;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.example.backend.study.api.event.service.NoticeService;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
public class NotifyLeaderListenerTest extends TestConfig {

    @InjectMocks
    private NotifyLeaderListener notifyLeaderListener;

    @Mock
    private FcmService fcmService;

    @Mock
    private NoticeService noticeService;


    @Test
    @DisplayName("스터디 멤버가 팀장에게 알림 리스너 테스트")
    void notify_Leader_test() throws FirebaseMessagingException {

        // given
        Long notifyLeaderId = 1L;

        NotifyLeaderEvent notifyLeaderEvent = MemberEventFixture.generateNotifyLeaderEvent(notifyLeaderId);

        FcmToken fcmToken = FcmFixture.generateDefaultFcmToken(notifyLeaderId);

        when(fcmService.findFcmTokenByIdOrThrowException(notifyLeaderId)).thenReturn(fcmToken);

        doNothing().when(noticeService).NotifyMemberNotice(any(NotifyMemberEvent.class));

        // when
        notifyLeaderListener.notifyLeaderListener(notifyLeaderEvent);

        // then
        verify(fcmService).sendMessageSingleDevice(any(FcmSingleTokenRequest.class)); // sendMessageSingleDevice 호출 검증
    }
}
