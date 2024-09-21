package com.example.backend.domain.define.fcm.listener;

import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.member.event.WithdrawalMemberEvent;
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
public class WithdrawalMemberListener {

    private final FcmService fcmService;

    private final NoticeService noticeService;

    @Async
    @EventListener
    public void withdrawalMemberListener(WithdrawalMemberEvent event) throws FirebaseMessagingException {

        noticeService.WithdrawalMemberNotice(event);

        if (event.isPushAlarmYn()) {
            FcmToken fcmToken = fcmService.findFcmTokenByIdOrThrowException(event.getStudyLeaderId());

            fcmService.sendMessageSingleDevice(FcmSingleTokenRequest.builder()
                    .token(fcmToken.getFcmToken())
                    .title("[" + event.getStudyInfoTopic() + "] 팀원 스터디 탈퇴")
                    .message(event.getWithdrawalMemberName() + "님이 탈퇴하였습니다.🥲\n앞으로의 스터디도 화이팅!")
                    .build());
        }

    }
}
