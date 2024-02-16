package com.example.backend.study.api.service.category.info.repository;

import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.member.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyCategoryRepository extends JpaRepository<StudyCategory, Long> {
}