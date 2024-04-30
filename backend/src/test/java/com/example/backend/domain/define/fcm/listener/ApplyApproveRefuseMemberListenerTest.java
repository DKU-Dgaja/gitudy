package com.example.backend.domain.define.fcm.listener;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.fcm.repository.FcmTokenRepository;
import com.example.backend.domain.define.study.info.StudyEventFixture;
import com.example.backend.domain.define.study.info.event.ApplyApproveRefuseMemberEvent;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.google.firebase.messaging.FirebaseMessaging;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
public class ApplyApproveRefuseMemberListenerTest extends TestConfig {

    @Autowired
    private ApplyApproveRefuseMemberListener applyApproveRefuseMemberListener;


    @MockBean
    private FcmService fcmService;

    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;


    @Test
    @DisplayName("스터디 가입 신청 승인/거부 리스너 테스트")
    void apply_approve_refuse_test() throws Exception {
        // given
        Long applyUserId = 1L;

        ApplyApproveRefuseMemberEvent applyApproveRefuseMemberEvent = StudyEventFixture.generateApplyApproveRefuseMemberEvent(applyUserId);

        FcmToken fcmToken = FcmFixture.generateDefaultFcmToken(applyUserId);

        when(fcmService.findFcmTokenByIdOrThrowException(any(Long.class))).thenReturn(fcmToken);

        applicationEventPublisher.publishEvent(applyApproveRefuseMemberEvent);

        // when
        applyApproveRefuseMemberListener.applyApproveRefuseMemberListener(applyApproveRefuseMemberEvent);

        // then

        /*
        timeout 을 사용하여 비동기 메서드 호출검증
        3초내에 sendMessageSingleDevice 가 호출되었는지 검증
         */
        verify(fcmService, timeout(3000)).sendMessageSingleDevice(any(FcmSingleTokenRequest.class)); // sendMessageSingleDevice 호출 검증
    }
}
