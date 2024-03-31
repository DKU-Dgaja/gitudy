package com.example.backend.domain.define.study.info.listener;


import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.event.EventException;
import com.example.backend.domain.define.fcmToken.FcmToken;
import com.example.backend.domain.define.fcmToken.repository.FcmTokenRepository;
import com.example.backend.domain.define.study.info.listener.event.ApplyMemberEvent;
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
public class StudyEventListener {

    private final FcmService fcmService;
    private final FcmTokenRepository fcmTokenRepository;


    @Async
    @EventListener
    public void applyMemberListener(ApplyMemberEvent event) throws FirebaseMessagingException {

        FcmToken fcmToken = fcmTokenRepository.findById(event.getStudyLeaderId()).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", event.getStudyLeaderId(), ExceptionMessage.FCM_DEVICE_NOT_FOUND);
            return new EventException(ExceptionMessage.FCM_DEVICE_NOT_FOUND);
        });


        fcmService.sendMessageSingleDevice(FcmSingleTokenRequest.builder()
                .token(fcmToken.getFcmToken())
                .title(event.getTitle())
                .message(event.getMessage())
                .build());
    }
}
