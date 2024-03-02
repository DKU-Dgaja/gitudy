package com.example.backend.domain.define.study.convention.repository;

import com.example.backend.domain.define.study.convention.StudyConvention;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyConventionRepository extends JpaRepository<StudyConvention, Long> {

    StudyConvention findByStudyInfoId(Long studyInfoId);
}
