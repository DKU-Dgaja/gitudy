package com.example.backend.domain.define.study.commit.repository;

import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudyCommitRepositoryCustom {

    // 커서 기반 마이 커밋 페이지네이션
    List<CommitInfoResponse> findStudyCommitListByUserId_CursorPaging(Long userId, Long idx, Long limit);
}
