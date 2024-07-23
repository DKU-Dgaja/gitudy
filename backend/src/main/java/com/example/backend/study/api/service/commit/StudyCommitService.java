package com.example.backend.study.api.service.commit;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
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
    private final static Long MAX_LIMIT = 50L;

    private final StudyCommitRepository studyCommitRepository;

    public CommitInfoResponse getCommitDetailsById(Long commitId) {
        // 커밋 조회 예외처리
        StudyCommit commit = findStudyCommitByIdOrThrowException(commitId);

        return CommitInfoResponse.of(commit);
    }

    public List<CommitInfoResponse> selectUserCommitList(Long userId, Long studyId, Long cursorIdx, Long limit) {

        limit = Math.min(limit, MAX_LIMIT);

        return studyCommitRepository.findStudyCommitListByUserId_CursorPaging(userId, studyId, cursorIdx, limit);
    }

    public StudyCommit findStudyCommitByIdOrThrowException(Long commitId) {
        return studyCommitRepository.findById(commitId)
                .orElseThrow(() -> {
                    log.error(">>>> {} : {} <<<<", commitId, ExceptionMessage.COMMIT_NOT_FOUND.getText());
                    return new CommitException(ExceptionMessage.COMMIT_NOT_FOUND);
                });
    }

    @Transactional
    public void approveCommit(Long commitId) {
        StudyCommit commit = findStudyCommitByIdOrThrowException(commitId);

        commit.approveCommit();
    }

    @Transactional
    public void rejectCommit(Long commitId, String rejectionReason) {
        StudyCommit commit = findStudyCommitByIdOrThrowException(commitId);

        commit.rejectCommit(rejectionReason);
    }

    public List<CommitInfoResponse> selectWaitingCommit(Long studyInfoId) {
        return studyCommitRepository.findStudyCommitListByStudyInfoIdAndStatus(studyInfoId, CommitStatus.COMMIT_WAITING)
                .stream()
                .map(CommitInfoResponse::of)
                .toList();
    }
}
