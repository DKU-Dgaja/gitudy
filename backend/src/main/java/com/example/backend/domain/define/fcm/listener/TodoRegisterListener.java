package com.example.backend.domain.define.fcm.listener;


import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.todo.event.TodoRegisterMemberEvent;
import com.example.backend.study.api.event.FcmMultiTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.example.backend.study.api.event.service.NoticeService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TodoRegisterMemberListener {

    private final FcmService fcmService;

    private final NoticeService noticeService;

    @Async
    @EventListener
    public void todoRegisterMemberListener(TodoRegisterMemberEvent event) throws FirebaseMessagingException {

        noticeService.TodoRegisterMemberNotice(event);

        if (!event.getPushAlarmYMemberIds().isEmpty()) {
            List<FcmToken> fcmTokens = fcmService.findFcmTokensByIdsOrThrowException(event.getPushAlarmYMemberIds());

            // 토큰 문자열 리스트 반환
            List<String> tokens = fcmTokens.stream()
                    .map(FcmToken::getFcmToken)
                    .toList();


            fcmService.sendMessageMultiDevice(FcmMultiTokenRequest.builder()
                    .tokens(tokens)
                    .title("[" + event.getStudyTopic() + "] 새로운 Todo")
                    .message("메세지 추후 변경 예정")
                    .build());
        }
    }
}
