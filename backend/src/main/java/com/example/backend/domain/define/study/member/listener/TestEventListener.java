package com.example.backend.domain.define.study.member.listener;

import com.example.backend.study.api.event.TestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestEventListener {

    @Async
    @EventListener
    public void testListener(TestEvent testEvent) {
        log.info("Received TestEvent: {}", testEvent);
        // TODO : FCM서버에 알람 요청 로직 구현





    }
}
