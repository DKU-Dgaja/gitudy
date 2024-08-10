package com.example.backend.domain.define.fcm.listener;


import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.info.event.ApplyMemberEvent;
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
public class ApplyMemberListener {

    private final FcmService fcmService;

    private final NoticeService noticeService;


    @Async
    @EventListener
    public void applyMemberListener(ApplyMemberEvent event) throws FirebaseMessagingException {

        noticeService.ApplyMemberNotice(event);

        if (event.isPushAlarmYn()) {

            FcmToken fcmToken = fcmService.findFcmTokenByIdOrThrowException(event.getStudyLeaderId());

            fcmService.sendMessageSingleDevice(FcmSingleTokenRequest.builder()
                    .token(fcmToken.getFcmToken())
                    .title("[" + event.getStudyTopic() + "] 스터디 신청")
                    .message(event.getName() + "님이 스터디를 신청했습니다.\n" + "프로필과 메시지를 확인 후, 수락해주세요!")
                    .build());
        }

    }
}
