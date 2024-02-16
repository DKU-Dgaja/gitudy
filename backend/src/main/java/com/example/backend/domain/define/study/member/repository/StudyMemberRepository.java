package com.example.backend.domain.define.study.member.repository;

import com.example.backend.domain.define.study.member.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyMemberRepository  extends JpaRepository<StudyMember, Long>, StudyMemberRepositoryCustom {
}