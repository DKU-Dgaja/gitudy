package com.example.backend.domain.define.study.commit.repository;

import com.example.backend.domain.define.study.commit.StudyCommit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudyCommitRepositoryCustom {
    // 오프셋 기반 마이 커밋 페이지네이션
    Page<StudyCommit> findStudyCommitListByUserId_OffsetPaging(Pageable pageable, Long userId);

    // 커서 기반 마이 커밋 페이지네이션
    Page<StudyCommit> findStudyCommitListByUserId_CursorPaging(Pageable pageable, Long userId, Long idx);
}
