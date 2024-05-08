package com.example.backend.domain.define.fcm.listener;

import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.member.event.ResignMemberEvent;
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
public class ResignMemberListener {

    private final FcmService fcmService;

    private final NoticeService noticeService;

    @Async
    @EventListener
    public void resignMemberListener(ResignMemberEvent event) throws FirebaseMessagingException {

        noticeService.ResignMemberNotice(event);

        if (event.isPushAlarmYn()) {
            FcmToken fcmToken = fcmService.findFcmTokenByIdOrThrowException(event.getResignMemberId());

            fcmService.sendMessageSingleDevice(FcmSingleTokenRequest.builder()
                    .token(fcmToken.getFcmToken())
                    .title("알림")
                    .message(event.getStudyInfoTopic() + " 스터디에서 강퇴 되었습니다.")
                    .build());
        }
    }
}

