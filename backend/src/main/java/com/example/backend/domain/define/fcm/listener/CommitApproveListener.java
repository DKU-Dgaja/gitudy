package com.example.backend.domain.define.fcm.listener;


import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.commit.event.CommitApproveEvent;
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
public class CommitApproveListener {

    private final FcmService fcmService;
    private final NoticeService noticeService;


    @Async
    @EventListener
    public void commitApproveListener(CommitApproveEvent event) throws FirebaseMessagingException {

        noticeService.StudyCommitApproveNotice(event);

        if(event.isPushAlarmYn()){
            FcmToken fcmToken = fcmService.findFcmTokenByIdOrThrowException(event.getUserId());

            fcmService.sendMessageSingleDevice(FcmSingleTokenRequest.builder()
                    .token(fcmToken.getFcmToken())
                    .title("[" + event.getStudyTopic() + "] 커밋 승인")
                    .message("TO-DO [" + event.getStudyTodoTopic() + "]에 대한 커밋이 승인되었습니다.\n팀장의 커밋 리뷰를 확인해보세요!")
                    .build());
        }
    }
}
