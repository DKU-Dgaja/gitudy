package com.example.backend.study.api.scheduler.service;

import com.example.backend.MockTestConfig;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.service.commit.StudyCommitService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ScheduledCommitFetchServiceTest extends MockTestConfig {
    @MockBean
    private StudyCommitService studyCommitService;

    @MockBean
    private StudyInfoRepository studyInfoRepository;

    @MockBean
    private StudyTodoRepository studyTodoRepository;

    @Autowired
    private ScheduledCommitFetchService scheduledCommitFetchService;

    @BeforeEach
    void setup() {
        when(studyCommitService.fetchRemoteCommitsForAllStudiesAsync())
                .thenReturn(CompletableFuture.completedFuture(null));
    }
//
//    @Test
//    void 커밋_패치_스케줄링_실행_테스트() {
//        // given
//        StudyInfo study = StudyInfoFixture.createDefaultPublicStudyInfo(1L);
//        StudyTodo todo = StudyTodoFixture.createStudyTodo(1L);
//
//        when(studyInfoRepository.findAll()).thenReturn(Arrays.asList(study));
//        when(studyTodoRepository.findByStudyInfoIdAndTodoDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
//                .thenReturn(Arrays.asList(todo));
//
//        // when
//        scheduledCommitFetchService.fetchCommitsForAllStudies();
//
//        // then
//        verify(studyCommitService, times(1)).fetchRemoteCommitsForAllStudiesAsync();
//    }
//
//    @Test
//    void 커밋_패치_스케줄링이_비동기적으로_작동하는지_테스트() {
//        // given
//        StudyInfo study = StudyInfoFixture.createDefaultPublicStudyInfo(1L);
//        StudyTodo todo = StudyTodoFixture.createStudyTodo(1L);
//
//        when(studyInfoRepository.findAll()).thenReturn(Arrays.asList(study));
//        when(studyTodoRepository.findByStudyInfoIdAndTodoDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
//                .thenReturn(Arrays.asList(todo));
//
//        CompletableFuture<Void> future = studyCommitService.fetchRemoteCommitsForAllStudiesAsync();
//
//        // when
//        Awaitility.await()
//                .atMost(3, TimeUnit.SECONDS) // 3초 대기
//                .untilAsserted(() -> {
//                    // 비동기 작업이 완료되었는지 확인
//                    assertThat(future).isCompleted();
//                });
//    }
//
//    @Test
//    void 커밋_패치_스케줄링이_주기적으로_작동하는지_테스트() {
//        // given
//        StudyInfo study = StudyInfoFixture.createDefaultPublicStudyInfo(1L);
//        StudyTodo todo = StudyTodoFixture.createStudyTodo(1L);
//
//        when(studyInfoRepository.findAll()).thenReturn(Arrays.asList(study));
//        when(studyTodoRepository.findByStudyInfoIdAndTodoDateBetween(anyLong(), any(LocalDate.class), any(LocalDate.class)))
//                .thenReturn(Arrays.asList(todo));
//
//        // when
//        Awaitility.await()
//                .atMost(4, TimeUnit.SECONDS) // 최대 5초 대기
//                .untilAsserted(() -> {
//                    // 주기적으로 실행되었는지 확인
//                    verify(studyCommitService, atLeast(2)).fetchRemoteCommitsForAllStudiesAsync();
//                });
//    }
}