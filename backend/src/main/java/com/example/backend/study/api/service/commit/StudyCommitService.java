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
import java.util.Objects;
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

            pageNumber++;

            // StudyCommit으로 저장되지 않은 것만 필터링
            List<GithubCommitResponse> unsavedCommits = studyCommitRepository.findUnsavedGithubCommits(commitPage);

            // 저장되지 않은 커밋이 없다면 이미 다 업데이트되었으므로 스탑
            if (unsavedCommits.isEmpty()) break;

            // 컨벤션 검증 후 통과한 커밋만 StudyCommit으로 저장
            StudyConventionResponse conventions = studyConventionRepository.findActiveConventionByStudyInId(study.getId());
            List<StudyCommit> commitList = unsavedCommits.stream()
                    .map(commit -> {    // user를 못 찾았거나, 찾았어도 활동중인 스터디원이 아닌 경우 무시하고 다음 커밋으로 넘어가도록 설정
                        try {
                            User findUser = userRepository.findByGithubId(commit.getAuthorName()).orElseThrow(() -> {
                                log.warn(">>>> User not found with GithubId: {}", commit.getAuthorName());
                                throw  new UserException(ExceptionMessage.USER_NOT_FOUND);
                            });

                            if (!studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(findUser.getId(), study.getId())) {
                                log.warn(">>>> StudyMember not found with : {}", findUser.getName());
                                throw new MemberException(ExceptionMessage.STUDY_NOT_MEMBER);
                            }

                            // 컨벤션 검증 통과 여부에 따른 커밋 상태 지정
                            boolean isValid = studyConventionService.checkConvention(conventions.getContent(), commit.getMessage());
                            CommitStatus status = isValid ? CommitStatus.COMMIT_APPROVAL : CommitStatus.COMMIT_INVALID;

                            return StudyCommit.of(findUser.getId(), todo, commit, status);

                        } catch (GitudyException e) {
                            log.error(">>>> 조회한 커밋을 저장하는 중 에러가 발생했습니다: {}", e.getMessage());
                            // 에러 발생 시 null 반환
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)       // null이 아닌 것만 필터링
                    .toList();

            studyCommitRepository.saveAll(commitList);
        }

        log.info(">>>> 원격 레포지토리로부터 커밋 업데이트를 성공하였습니다.");
    }
}
