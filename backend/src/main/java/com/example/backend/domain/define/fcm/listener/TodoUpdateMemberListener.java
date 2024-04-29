package com.example.backend.domain.define.fcm.listener;

import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.todo.event.TodoRegisterMemberEvent;
import com.example.backend.domain.define.study.todo.event.TodoUpdateMemberEvent;
import com.example.backend.study.api.event.FcmMultiTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
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
public class TodoUpdateMemberListener {
    private final FcmService fcmService;

    @Async
    @EventListener
    public void todoUpdateMemberListener(TodoUpdateMemberEvent event) throws FirebaseMessagingException {

        List<FcmToken> fcmTokens = fcmService.findFcmTokensByIdsOrThrowException(event.getUserIds());

        // 토큰 문자열 리스트 반환
        List<String> tokens = fcmTokens.stream()
                .map(FcmToken::getFcmToken)
                .toList();


        fcmService.sendMessageMultiDevice(FcmMultiTokenRequest.builder()
                .tokens(tokens)
                .title("[" + event.getStudyTopic() + "] 스터디의 Todo [" + event.getTodoTitle() + "]가 변경 되었습니다.")
                .message("메세지 추후 변경 예정")
                .build());
    }
}
