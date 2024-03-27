package com.example.backend.domain.define.study.commit.repository;

import com.example.backend.domain.define.study.commit.StudyCommit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyCommitRepository extends JpaRepository<StudyCommit, Long>, StudyCommitRepositoryCustom {
    List<StudyCommit> findByStudyTodoId(long todoId);
}
