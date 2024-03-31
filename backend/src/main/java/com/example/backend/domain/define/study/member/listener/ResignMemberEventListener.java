package com.example.backend.domain.define.study.member.listener;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.event.EventException;
import com.example.backend.domain.define.fcmToken.FcmToken;
import com.example.backend.domain.define.fcmToken.repository.FcmTokenRepository;
import com.example.backend.domain.define.study.member.listener.event.ResignMemberEvent;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResignMemberEventListener {

    private final FcmService fcmService;
    private final FcmTokenRepository fcmTokenRepository;

    @Async
    @EventListener
    public void resignMemberListener(ResignMemberEvent event) throws FirebaseMessagingException {
        FcmToken fcmToken = fcmTokenRepository.findById(event.getResignMemberId()).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", event.getResignMemberId(), ExceptionMessage.FCM_DEVICE_NOT_FOUND);
            return new EventException(ExceptionMessage.FCM_DEVICE_NOT_FOUND);
        });
        System.out.println("resignMemberListener");
        fcmService.sendMessageSingleDevice(FcmSingleTokenRequest.builder()
                .token(fcmToken.getFcmToken())
                .title("알림")
                .message(event.getStudyInfoTopic() + " 스터디에서 강퇴 되었습니다.")
                .build());
    }
}

