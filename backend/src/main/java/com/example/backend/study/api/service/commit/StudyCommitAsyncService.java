package com.example.backend.study.api.service.commit;

import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyCommitAsyncService {
    private final StudyCommitService studyCommitService;
    private final StudyInfoRepository studyInfoRepository;
    private final StudyTodoRepository studyTodoRepository;

    @Async
    @Transactional
    public void fetchRemoteCommitsAndSaveAsync(StudyInfo study, StudyTodo todo) {
        studyCommitService.fetchRemoteCommitsAndSave(study, todo);
    }

    // 스케줄링 전용 메서드
    @Async
    @Transactional
    public CompletableFuture<Void> fetchRemoteCommitsForAllStudiesAsync() {
        List<StudyInfo> studies = studyInfoRepository.findAll();

        for (StudyInfo study : studies) {
            List<StudyTodo> todos = studyTodoRepository.findByStudyInfoId(study.getId());

            for (StudyTodo todo : todos) {
                studyCommitService.fetchRemoteCommitsAndSave(study, todo);
            }
        }

        return CompletableFuture.completedFuture(null);
    }
}
