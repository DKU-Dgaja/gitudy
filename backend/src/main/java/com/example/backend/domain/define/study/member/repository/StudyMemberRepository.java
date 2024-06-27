package com.example.backend.domain.define.study.member.repository;

import com.example.backend.domain.define.study.member.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long>, StudyMemberRepositoryCustom {
    List<StudyMember> findByStudyInfoId(Long studyInfoId);

    Optional<StudyMember> findByStudyInfoIdAndUserId(Long studyInfoId, Long userId);
}