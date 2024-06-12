package com.example.backend.domain.define.study.commit.repository;

import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyCommitRepository extends JpaRepository<StudyCommit, Long>, StudyCommitRepositoryCustom {
    List<StudyCommit> findByStudyTodoId(long todoId);

    List<StudyCommit> findByStudyTodoIdOrderByCommitDateDesc(Long todoId);

    List<StudyCommit> findStudyCommitListByStudyInfoIdAndStatus(Long studyInfoId, CommitStatus status);

    boolean existsByCommitSHA(String commitSha);
}
