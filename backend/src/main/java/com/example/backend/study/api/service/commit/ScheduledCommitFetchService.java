package com.example.backend.study.api.service.commit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledCommitFetchService {
    private final StudyCommitAsyncService studyCommitAsyncService;

    @Value("${scheduling.fixedRate}")
    private long fixedRate;

    @Scheduled(fixedRateString = "${scheduling.fixedRate}")
    @Transactional
    public void fetchCommitsForAllStudies() {
        log.info(">>>> 스케줄링 시작 <<<<");

        studyCommitAsyncService.fetchRemoteCommitsForAllStudiesAsync()
                .exceptionally(ex -> {
                    log.error(">>>> Error fetching commits for all studies : {} <<<<", ex.getMessage());
                    return null;
                });

        log.info(">>>> 스케줄링 종료 <<<<");
    }
}
