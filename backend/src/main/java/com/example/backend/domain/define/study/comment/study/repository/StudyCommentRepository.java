package com.example.backend.domain.define.study.comment.study.repository;

import com.example.backend.domain.define.study.comment.study.StudyComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCommentRepository extends JpaRepository<StudyComment, Long> {
}
