package com.example.backend.domain.define.study.convention.repository;

import com.example.backend.domain.define.study.convention.StudyConvention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyConventionRepository extends JpaRepository<StudyConvention, Long>, StudyConventionRepositoryCustom {

    StudyConvention findByStudyInfoId(Long studyInfoId);

    Optional<StudyConvention> findByStudyInfoIdAndContent(Long studyInfoId, String content);
}
