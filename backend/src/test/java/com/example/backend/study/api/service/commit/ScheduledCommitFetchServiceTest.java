package com.example.backend.study.api.service.commit;

import com.example.backend.MockTestConfig;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ScheduledCommitFetchServiceTest extends MockTestConfig {
    @MockBean
    private StudyCommitService studyCommitService;

    @MockBean
    private StudyInfoRepository studyInfoRepository;

    @MockBean
    private StudyTodoRepository studyTodoRepository;

    @MockBean
    private StudyCommitAsyncService studyCommitAsyncService;

    @Autowired
    private ScheduledCommitFetchService scheduledCommitFetchService;

    @BeforeEach
    void setup() {
        when(studyCommitAsyncService.fetchRemoteCommitsForAllStudiesAsync())
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void 커밋_패치_스케줄링_실행_테스트() {
        // given
        StudyInfo study = StudyInfoFixture.createDefaultPublicStudyInfo(1L);
        StudyTodo todo = StudyTodoFixture.createStudyTodo(1L);

        when(studyInfoRepository.findAll()).thenReturn(Arrays.asList(study));
        when(studyTodoRepository.findByStudyInfoId(anyLong())).thenReturn(Arrays.asList(todo));

        // when
//        scheduledCommitFetchService.fetchCommitsForAllStudies();  // 직접 호출

        // then
        verify(studyCommitAsyncService, times(1)).fetchRemoteCommitsForAllStudiesAsync();
    }

    @Test
    void 커밋_패치_스케줄링이_주기적으로_작동하는지_테스트() {
        // given
        StudyInfo study = StudyInfoFixture.createDefaultPublicStudyInfo(1L);
        StudyTodo todo = StudyTodoFixture.createStudyTodo(1L);

        when(studyInfoRepository.findAll()).thenReturn(Arrays.asList(study));
        when(studyTodoRepository.findByStudyInfoId(anyLong())).thenReturn(Arrays.asList(todo));


        // when
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS) // 5초 대기
                .untilAsserted(() -> {
                    // 주기적으로 실행되었는지 확인
                    verify(studyCommitAsyncService, atLeast(2)).fetchRemoteCommitsForAllStudiesAsync();
                });
    }
}