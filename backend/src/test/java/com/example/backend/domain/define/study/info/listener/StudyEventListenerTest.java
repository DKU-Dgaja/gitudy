package com.example.backend.domain.define.study.info.listener;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.domain.define.fcmToken.FcmToken;
import com.example.backend.domain.define.fcmToken.repository.FcmTokenRepository;
import com.example.backend.domain.define.study.info.StudyEventFixture;
import com.example.backend.domain.define.study.info.listener.event.ApplyMemberEvent;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.Mockito.*;

@SuppressWarnings("NonAsciiCharacters")
public class StudyEventListenerTest extends TestConfig {
    @Mock
    private FcmTokenRepository fcmTokenRepository;

    @InjectMocks
    private StudyEventListener studyEventListener;

    @Mock
    private FcmService fcmService;


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

        when(fcmTokenRepository.findById(any(Long.class))).thenReturn(Optional.of(fcmToken));

        // when
        studyEventListener.applyMemberListener(applyMemberEvent);

        // then
        verify(fcmService).sendMessageSingleDevice(any(FcmSingleTokenRequest.class)); // sendMessageSingleDevice 호출 검증
    }
}
