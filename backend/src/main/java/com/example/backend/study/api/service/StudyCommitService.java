package com.example.backend.study.api.service;

import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyCommitService {
    private final static Long SELECT_COMMIT_LIST_LIMIT = 10L;

    private final StudyCommitRepository studyCommitRepository;

    public List<CommitInfoResponse> selectUserCommitList(Long userId, Long cursorIdx) {

        return studyCommitRepository.findStudyCommitListByUserId_CursorPaging(userId, cursorIdx, SELECT_COMMIT_LIST_LIMIT);
    }
}
