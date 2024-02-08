package com.example.backend.domain.define.study.info.repository;

import com.example.backend.domain.define.study.info.StudyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudyInfoRepository extends JpaRepository<StudyInfo, Long> {
    Optional<StudyInfo> findById(Long id);

    Optional<StudyInfo> findByUserId(Long userid);

    Optional<StudyInfo> findStudyInfo(Long studyInfoId);
}