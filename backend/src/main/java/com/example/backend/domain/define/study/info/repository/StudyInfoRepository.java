package com.example.backend.domain.define.study.info.repository;

import com.example.backend.domain.define.study.info.StudyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyInfoRepository extends JpaRepository<StudyInfo, Long>, StudyInfoRepositoryCustom {
}
