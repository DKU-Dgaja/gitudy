package com.example.backend.domain.define.fcm.listener;


import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.member.event.NotifyMemberEvent;
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
public class NotifyMemberListener {

    private final FcmService fcmService;

    @Async
    @EventListener
    public void notifyMemberListener(NotifyMemberEvent event) throws FirebaseMessagingException {

        FcmToken fcmToken = fcmService.findFcmTokenByIdOrThrowException(event.getNotifyUserId());

        fcmService.sendMessageSingleDevice(FcmSingleTokenRequest.builder()
                .token(fcmToken.getFcmToken())
                .title("[" + event.getStudyTopic() + "] 스터디 알림")
                .message(event.getMessage())
                .build());

    }
}
