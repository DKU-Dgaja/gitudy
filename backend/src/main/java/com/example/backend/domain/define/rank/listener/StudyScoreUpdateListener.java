package com.example.backend.domain.define.rank.listener;


import com.example.backend.auth.api.service.rank.RankingService;
import com.example.backend.auth.api.service.rank.event.StudyScoreUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudyScoreUpdateListener {

    private final RankingService rankingService;

    @Async
    @EventListener
    public void studyScoreUpdateListener(StudyScoreUpdateEvent event) {

        // 레디스 스터디 점수 업데이트
        rankingService.updateStudyScore(event.getStudyInfoId(), event.getScore());
    }
}
