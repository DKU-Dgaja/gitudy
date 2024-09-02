package com.example.backend.domain.define.fcm.listener;

import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.commit.event.CommitRegisterEvent;
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
public class CommitRegisterListener {

    private final FcmService fcmService;
    private final NoticeService noticeService;

    @Async
    @EventListener
    public void commitRegisterListener(CommitRegisterEvent event) throws FirebaseMessagingException {

        noticeService.StudyCommitRegisterNotice(event);

        if (event.isPushAlarmYn()) {
            FcmToken fcmToken = fcmService.findFcmTokenByIdOrThrowException(event.getUserId());

            fcmService.sendMessageSingleDevice(FcmSingleTokenRequest.builder()
                    .token(fcmToken.getFcmToken())
                    .title("[" + event.getStudyTopic() + "] 커밋 등록")
                    .message("TO-DO [" + event.getStudyTodoTopic() + "]에 대해 " + event.getName() + "님이 커밋하였습니다.\n커밋을 확인하고 리뷰를 작성해주세요!")
                    .build());
        }
    }
}
