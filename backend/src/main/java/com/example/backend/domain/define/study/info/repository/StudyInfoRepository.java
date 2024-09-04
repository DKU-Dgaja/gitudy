package com.example.backend.domain.define.study.info.repository;

import com.example.backend.domain.define.study.info.StudyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyInfoRepository extends JpaRepository<StudyInfo, Long>, StudyInfoRepositoryCustom {
    List<StudyInfo> findAllByUserId(Long userId);
}
