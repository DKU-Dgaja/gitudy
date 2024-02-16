package com.example.backend.domain.define.study.comment.commit.repository;

import com.example.backend.domain.define.study.comment.commit.CommitComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitCommentRepository extends JpaRepository<CommitComment, Long>, CommitCommentRepositoryCustom {

}
