package com.example.backend.study.api.service.commit;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.common.exception.convention.ConventionException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.convention.StudyConvention;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.study.api.controller.convention.response.StudyConventionResponse;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.example.backend.study.api.service.convention.StudyConventionService;
import com.example.backend.study.api.service.github.GithubApiService;
import com.example.backend.study.api.service.github.response.GithubCommitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyCommitService {
    private final UserRepository userRepository;
    private final static Long MAX_LIMIT = 50L;

    private final StudyCommitRepository studyCommitRepository;
    private final GithubApiService githubApiService;
    private final StudyConventionService studyConventionService;
    private final StudyConventionRepository studyConventionRepository;

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

    // 커밋 업데이트
    @Transactional
    public void fetchRemoteCommitsAndSave(StudyInfo study, StudyTodo todo) {
        // 원격의 커밋들 중 저장되지 않은 커밋 불러오기
        List<GithubCommitResponse> unsavedCommits = githubApiService.pullUnsavedCommits(study.getRepositoryInfo(), todo);

        // 컨벤션 검증 후 통과한 커밋만 StudyCommit으로 저장
        StudyConventionResponse conventions = studyConventionRepository.findActiveConventionByStudyInId(study.getId());
        List<StudyCommit> commitList = unsavedCommits.stream()
                .filter(commit -> studyConventionService.checkConvention(conventions.getContent(), commit.getMessage()))
                .flatMap(commit -> {    // user를 못 찾은 경우 무시하고 다음 커밋으로 넘어가도록 설정
                    try {
                        User findUser = userRepository.findByGithubId(commit.getAuthorName()).orElseThrow(() -> {
                            log.warn(">>>> User not found with GithubId: {}", commit.getAuthorName());
                            return new UserException(ExceptionMessage.USER_NOT_FOUND);
                        });
                        return Stream.of(StudyCommit.of(findUser.getId(), todo, commit));
                    } catch (UserException e) {
                        // user를 찾을 수 없는 경우 처리
                        log.warn("User not found for commit: {}", commit.getMessage());
                        return Stream.empty();
                    }
                })
                .toList();

        studyCommitRepository.saveAll(commitList);

    }
}
