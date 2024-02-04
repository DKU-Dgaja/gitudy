package com.example.backend.study.api.service;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyCommitService {
    private final StudyCommitRepository studyCommitRepository;

    public Page<CommitInfoResponse> selectUserCommitList(Long userId, PageRequest pageable, Long cursorIdx) {

        return studyCommitRepository.findStudyCommitListByUserId_CursorPaging(pageable, userId, cursorIdx);
    }
}
