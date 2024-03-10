package com.example.backend.study.api.service.commit;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.common.utils.GithubApi;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.example.backend.study.api.service.commit.response.GithubCommitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyCommitService {

    @Value("${github.api.token}")
    private String gitToken;

    private final static Long MAX_LIMIT = 50L;

    private final StudyCommitRepository studyCommitRepository;
    private final GithubApi githubApi;

    public CommitInfoResponse getCommitDetailsById(Long commitId) {
        StudyCommit commit = studyCommitRepository.findById(commitId).orElseThrow(() -> {
            log.error(">>>> {} : {} <<<<", commitId, ExceptionMessage.COMMIT_NOT_FOUND.getText());
            throw new CommitException(ExceptionMessage.COMMIT_NOT_FOUND);
        });

        return CommitInfoResponse.of(commit);
    }

    public List<CommitInfoResponse> selectUserCommitList(Long userId, Long studyId, Long cursorIdx, Long limit) {

        limit = Math.min(limit, MAX_LIMIT);

        return studyCommitRepository.findStudyCommitListByUserId_CursorPaging(userId, studyId, cursorIdx, limit);
    }

    @Transactional
    public List<GithubCommitResponse> pullCommitsFromGitHub(Long studyInfoId) {


        return null;
    }
}
