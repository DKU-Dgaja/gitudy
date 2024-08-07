package com.example.backend.domain.define.rank.listener;

import com.example.backend.auth.api.service.rank.RankingService;
import com.example.backend.auth.api.service.rank.event.StudyScoreSaveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudyScoreSaveListener {

    private final RankingService rankingService;

    @Async
    @EventListener
    public void studyScoreSaveListener(StudyScoreSaveEvent event) {

        // 레디스에 스터디 점수를 처음 추가
        rankingService.saveStudyScore(event.getStudyInfoId(), event.getScore());
    }
}
