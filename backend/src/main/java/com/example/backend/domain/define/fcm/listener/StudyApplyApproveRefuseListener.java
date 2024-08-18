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
public class StudyApplyApproveRefuseListener {

    private final FcmService fcmService;

    private final NoticeService noticeService;


    @Async
    @EventListener
    public void studyApplyApproveRefuseListener(ApplyApproveRefuseMemberEvent event) throws FirebaseMessagingException {

        noticeService.ApplyApproveRefuseMemberNotice(event);

        if (event.isPushAlarmYn()) {
            FcmToken fcmToken = fcmService.findFcmTokenByIdOrThrowException(event.getApplyUserId());

            String title;
            String message;

            if (event.isApprove()) {
                title = "[ " + event.getStudyTopic() + " ] 스터디 가입 완료";
                message = "스터디 가입이 완료되었습니다.\n바로 스터디 활동을 시작해보세요!";

            } else {
                title = "[" + event.getStudyTopic() + "] 스터디 가입 실패";
                message = "스터디 가입이 거절되었습니다🥲\n더 좋은 스터디를 찾아보세요!";
            }

            fcmService.sendMessageSingleDevice(FcmSingleTokenRequest.builder()
                    .token(fcmToken.getFcmToken())
                    .title(title)
                    .message(message)
                    .build());
        }
    }
}
