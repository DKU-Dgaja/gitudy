package com.example.backend.domain.define.fcm.listener;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.member.MemberEventFixture;
import com.example.backend.domain.define.study.member.event.NotifyMemberEvent;
import com.example.backend.domain.define.study.member.event.ResignMemberEvent;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.example.backend.study.api.event.service.NoticeService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
public class NotifyMemberListenerTest extends TestConfig {


    @InjectMocks
    private NotifyMemberListener notifyMemberListener;

    @Mock
    private FcmService fcmService;

    @Mock
    private NoticeService noticeService;


    @Test
    @DisplayName("스터디 멤버에게 알림 리스너 테스트")
    void notify_member_test() throws FirebaseMessagingException {

        // given
        Long notifyUserId = 1L;

        NotifyMemberEvent notifyMemberEvent = MemberEventFixture.generateNotifyMemberEvent(notifyUserId);

        FcmToken fcmToken = FcmFixture.generateDefaultFcmToken(notifyUserId);

        when(fcmService.findFcmTokenByIdOrThrowException(notifyUserId)).thenReturn(fcmToken);

        doNothing().when(noticeService).NotifyMemberNotice(any(NotifyMemberEvent.class));

        // when
        notifyMemberListener.notifyMemberListener(notifyMemberEvent);

        // then
        verify(fcmService).sendMessageSingleDevice(any(FcmSingleTokenRequest.class)); // sendMessageSingleDevice 호출 검증
    }
}
