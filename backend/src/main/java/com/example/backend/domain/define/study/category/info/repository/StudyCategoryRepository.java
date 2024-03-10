package com.example.backend.domain.define.study.category.info.repository;

import com.example.backend.domain.define.study.category.info.StudyCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCategoryRepository extends JpaRepository<StudyCategory, Long>, StudyCategoryRepositoryCustom {
}