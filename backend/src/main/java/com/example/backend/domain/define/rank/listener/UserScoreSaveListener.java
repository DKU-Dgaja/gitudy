package com.example.backend.domain.define.rank.listener;


import com.example.backend.auth.api.service.rank.RankingService;
import com.example.backend.auth.api.service.rank.event.UserScoreSaveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserScoreSaveListener {

    private final RankingService rankingService;

    @Async
    @EventListener
    public void userScoreSaveListener(UserScoreSaveEvent event) {

        // 레디스에 유저 점수를 처음 추가
        rankingService.saveUserScore(event.getUserid(), event.getScore());

    }
}
