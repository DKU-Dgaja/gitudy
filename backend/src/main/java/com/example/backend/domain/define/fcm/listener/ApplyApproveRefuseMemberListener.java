package com.example.backend.domain.define.fcm.listener;


import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.info.event.ApplyApproveRefuseMemberEvent;
import com.example.backend.study.api.event.FcmSingleTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.example.backend.study.api.event.service.NoticeService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplyApproveRefuseMemberListener {

    private final FcmService fcmService;

    private final NoticeService noticeService;


    @Async
    @EventListener
    public void applyApproveRefuseMemberListener(ApplyApproveRefuseMemberEvent event) throws FirebaseMessagingException {

        noticeService.ApplyApproveRefuseMemberNotice(event);

        if (event.isPushAlarmYn()) {
            FcmToken fcmToken = fcmService.findFcmTokenByIdOrThrowException(event.getApplyUserId());

            String title;
            String message;

            if (event.isApprove()) {
                title = "[" + event.getStudyTopic() + " ] 스터디 신청";
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
}
