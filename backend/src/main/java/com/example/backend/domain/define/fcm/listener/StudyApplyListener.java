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
public class StudyApplyListener {

    private final FcmService fcmService;

    private final NoticeService noticeService;


    @Async
    @EventListener
    public void studyApplyListener(ApplyMemberEvent event) throws FirebaseMessagingException {

        noticeService.ApplyMemberNotice(event);

        if (event.isPushAlarmYn()) {

            FcmToken fcmToken = fcmService.findFcmTokenByIdOrThrowException(event.getStudyLeaderId());

            fcmService.sendMessageSingleDevice(FcmSingleTokenRequest.builder()
                    .token(fcmToken.getFcmToken())
                    .title("[" + event.getStudyTopic() + "] 스터디 가입 신청")
                    .message("새로운 스터디 가입 신청자가 있습니다.\n가입 목록 확인 후, 수락해주세요!")
                    .build());
        }

    }
}
