package com.example.backend.study.api.service.category.mapping.repository;

import com.example.backend.domain.define.study.category.mapping.StudyCategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCategoryMappingRepository extends JpaRepository<StudyCategoryMapping, Long> {
}
