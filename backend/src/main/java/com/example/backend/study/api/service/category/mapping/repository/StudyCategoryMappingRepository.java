package com.example.backend.study.api.service.category.mapping.repository;

import com.example.backend.domain.define.study.category.mapping.StudyCategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyCategoryMappingRepository extends JpaRepository<StudyCategoryMapping, Long>{
    Optional<StudyCategoryMapping> deleteByStudyInfoId(Long studyInfoId);
}
