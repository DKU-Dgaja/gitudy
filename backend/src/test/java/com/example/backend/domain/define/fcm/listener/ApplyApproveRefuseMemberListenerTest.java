package com.example.backend.domain.define.fcm.listener;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.info.StudyEventFixture;
import com.example.backend.domain.define.study.info.event.ApplyApproveRefuseMemberEvent;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.example.backend.study.api.event.service.NoticeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
public class ApplyApproveRefuseMemberListenerTest extends TestConfig {

    @InjectMocks
    private ApplyApproveRefuseMemberListener applyApproveRefuseMemberListener;

    @Mock
    private FcmService fcmService;

    @Mock
    private NoticeService noticeService;

    @Test
    @DisplayName("스터디 가입 신청 승인/거부 리스너 테스트")
    void apply_approve_refuse_test() throws Exception {
        // given
        Long applyUserId = 1L;

        ApplyApproveRefuseMemberEvent applyApproveRefuseMemberEvent = StudyEventFixture.generateApplyApproveRefuseMemberEvent(applyUserId);

        FcmToken fcmToken = FcmFixture.generateDefaultFcmToken(applyUserId);

        when(fcmService.findFcmTokenByIdOrThrowException(applyUserId)).thenReturn(fcmToken);

        doNothing().when(noticeService).ApplyApproveRefuseMemberNotice(any(ApplyApproveRefuseMemberEvent.class));

        // when
        applyApproveRefuseMemberListener.applyApproveRefuseMemberListener(applyApproveRefuseMemberEvent);

        // then
        verify(fcmService).sendMessageSingleDevice(any(FcmSingleTokenRequest.class)); // sendMessageSingleDevice 호출 검증
    }
}
