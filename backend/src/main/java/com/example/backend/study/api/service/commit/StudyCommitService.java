package com.example.backend.study.api.service.commit;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.GitudyException;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.common.exception.convention.ConventionException;
import com.example.backend.common.exception.member.MemberException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.convention.StudyConvention;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyCommitService {
    private final StudyMemberRepository studyMemberRepository;
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
        int pageNumber = 0;
        int pageSize = 10;

        // 불러온 커밋 페이지에 저장되지 않은 커밋이 없을 때까지 반복
        while (true) {
            // 원격의 커밋 한 페이지 추출
            List<GithubCommitResponse> commitPage = githubApiService.fetchCommits(study.getRepositoryInfo(), pageNumber, pageSize, todo.getTodoCode());
            if (commitPage.isEmpty()) break;

            // 사용자 조회 로직을 최소화하기 위해 map에 미리 저장
            Map<String, Optional<User>> userCache = commitPage.stream()
                    .collect(Collectors.toMap(
                            // key
                            GithubCommitResponse::getAuthorName,
                            // value
                            commit -> userRepository.findByGithubId(commit.getAuthorName()),
                            // 동일한 키에 대해 충돌이 나면, 기존 값을 사용
                            (existing, replacement) -> existing
                    ));

            // StudyCommit으로 저장되지 않은 것만 필터링
            List<GithubCommitResponse> unsavedCommits = studyCommitRepository.findUnsavedGithubCommits(commitPage);

            // 컨벤션 검증 결과에 따라 상태를 다르게 저장
            StudyConventionResponse conventions = studyConventionRepository.findActiveConventionByStudyInId(study.getId());
            List<StudyCommit> commitList = unsavedCommits.stream()
                    // 사용자가 존재하는지, 존재한다면 스터디의 활동중인 멤버인지, 컨벤션을 통과하는지 검증
                    .filter(commit -> {
                        Optional<User> user = userCache.get(commit.getAuthorName());
                        return user.isPresent() &&
                                studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(user.get().getId(), study.getId()) &&
                                studyConventionService.checkConvention(conventions.getContent(), commit.getMessage());
                    })
                    .map(commit -> {
                        User findUser = userCache.get(commit.getAuthorName()).get();
                        return StudyCommit.of(findUser.getId(), todo, commit, CommitStatus.COMMIT_APPROVAL);
                    })
                    .toList();

            studyCommitRepository.saveAll(commitList);

            // 페이지 내 저장된 커밋이 하나라도 있으면 현재 페이지까지만 저장 로직 적용
            if (commitPage.size() > unsavedCommits.size()) break;

            // 다음 페이지로
            pageNumber++;
        }

        log.info(">>>> 원격 레포지토리로부터 커밋 업데이트를 성공하였습니다.");
    }
}
