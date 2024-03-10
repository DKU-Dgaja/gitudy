package com.example.backend.domain.define.study.commit.repository;

import com.example.backend.domain.define.study.commit.StudyCommit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCommitRepository extends JpaRepository<StudyCommit, Long>, StudyCommitRepositoryCustom {

}
