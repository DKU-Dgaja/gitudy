package com.example.backend.domain.define.fcm.listener;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.study.todo.TodoEventFixture;
import com.example.backend.domain.define.study.todo.event.TodoRegisterMemberEvent;
import com.example.backend.study.api.event.FcmMultiTokenRequest;
import com.example.backend.study.api.event.service.FcmService;
import com.google.firebase.messaging.FirebaseMessaging;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SuppressWarnings("NonAsciiCharacters")
public class TodoRegisterMemberListenerTest extends TestConfig {

    @InjectMocks
    private TodoRegisterMemberListener todoRegisterMemberListener;

    @Mock
    private FcmService fcmService;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Test
    @DisplayName("Todo 등록시 알림 리스너 테스트")
    void Todo_register_notify_listener_test() throws Exception {
        // given
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);

        TodoRegisterMemberEvent todoRegisterMemberEvent = TodoEventFixture.generateTodoRegisterEvent(userIds);

        List<FcmToken> fcmTokens = FcmFixture.generateFcmTokens(userIds);

        when(fcmService.findFcmTokensByIdsOrThrowException(userIds)).thenReturn(fcmTokens);

        when(firebaseMessaging.send(any())).thenReturn("메시지 전송 완료");

        // when
        todoRegisterMemberListener.todoRegisterMemberListener(todoRegisterMemberEvent);

        // then
        verify(fcmService).sendMessageMultiDevice(any(FcmMultiTokenRequest.class)); // sendMessageMultiDevice 호출 검증
    }
}