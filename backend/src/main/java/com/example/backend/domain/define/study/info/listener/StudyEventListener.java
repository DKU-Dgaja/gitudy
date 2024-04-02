package com.example.backend.domain.define.study.info.listener;


import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.event.EventException;
import com.example.backend.domain.define.fcmToken.FcmToken;
import com.example.backend.domain.define.fcmToken.repository.FcmTokenRepository;
import com.example.backend.domain.define.study.info.listener.event.ApplyApproveRefuseMemberEvent;
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

        FcmToken fcmToken = fcmTokenRepository.findById(event.getStudyLeaderId())
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", event.getStudyLeaderId(), ExceptionMessage.FCM_DEVICE_NOT_FOUND);
                    return new EventException(ExceptionMessage.FCM_DEVICE_NOT_FOUND);
                });


        fcmService.sendMessageSingleDevice(FcmSingleTokenRequest.builder()
                .token(fcmToken.getFcmToken())
                .title("[" + event.getStudyTopic() + "] 스터디 신청")
                .message(event.getName() + "님이 스터디를 신청했습니다.\n" + "프로필과 메시지를 확인 후, 수락해주세요!")
                .build());
    }


    @Async
    @EventListener
    public void applyApproveRefuseMemberListener(ApplyApproveRefuseMemberEvent event) throws FirebaseMessagingException {

        FcmToken fcmToken = fcmTokenRepository.findById(event.getApplyUserId())
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", event.getApplyUserId(), ExceptionMessage.FCM_DEVICE_NOT_FOUND);
                    return new EventException(ExceptionMessage.FCM_DEVICE_NOT_FOUND);
                });

        String title;
        String message;

        if (event.isApprove()) {
            title = "[" + event.getStudyTopic() + "] 스터디 신청";
            message = String.format("축하합니다! '%s'님 가입이 승인되었습니다!", event.getName());

        } else {
            title = "[" + event.getStudyTopic() + "] 스터디 신청";
            message = String.format("안타깝게도 '%s'님은 가입이 거절되었습니다.", event.getName());
        }

        fcmService.sendMessageSingleDevice(FcmSingleTokenRequest.builder()
                .token(fcmToken.getFcmToken())
                .title(title)
                .message(message)
                .build());
    }
}
