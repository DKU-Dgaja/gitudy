package com.example.backend.domain.define.rank.listener;


import com.example.backend.auth.api.service.rank.RankingService;
import com.example.backend.auth.api.service.rank.event.UserScoreUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserScoreUpdateListener {

    private final RankingService rankingService;

    @Async
    @EventListener
    public void userScoreUpdateListener(UserScoreUpdateEvent event) {

        // 레디스 유저 점수 업데이트
        rankingService.updateUserScore(event.getUserid(), event.getScore());
    }
}
