package com.example.backend.study.api.service.commit;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.common.exception.convention.ConventionException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.convention.StudyConvention;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.example.backend.study.api.service.convention.StudyConventionService;
import com.example.backend.study.api.service.github.GithubApiService;
import com.example.backend.study.api.service.github.response.GithubCommitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyCommitService {
    private final StudyTodoMappingRepository studyTodoMappingRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final UserRepository userRepository;
    private final static Long MAX_LIMIT = 50L;
    private final static String DEFAULT_CONTENT = "^[a-zA-Z0-9]{6} .*";

    private final StudyCommitRepository studyCommitRepository;
    private final GithubApiService githubApiService;
    private final StudyConventionService studyConventionService;
    private final StudyConventionRepository studyConventionRepository;
    private final StudyInfoRepository studyInfoRepository;
    private final StudyTodoRepository studyTodoRepository;

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

    @Async
    @Transactional
    public CompletableFuture<Void> fetchRemoteCommitsAndSaveAsync(StudyInfo study, StudyTodo todo, int pageSize) {
        fetchRemoteCommitsAndSave(study, todo, pageSize);
        return CompletableFuture.completedFuture(null);
    }

    // 스케줄링 전용 메서드
    @Async("commitFetchExecutor")
    @Transactional
    public CompletableFuture<Void> fetchRemoteCommitsForAllStudiesAsync() {
        int pageSize = 10;
        LocalDate today = LocalDate.now();
        LocalDate threeDaysAgo = today.minusDays(3);

        List<StudyInfo> studies = studyInfoRepository.findAll();

        for (StudyInfo study : studies) {
            List<StudyTodo> todos = studyTodoRepository.findByStudyInfoIdAndTodoDateAfter(
                    study.getId(), threeDaysAgo);
            for (StudyTodo todo : todos) {
                fetchRemoteCommitsAndSave(study, todo, pageSize);
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    // 커밋 업데이트
    @Transactional
    public void fetchRemoteCommitsAndSave(StudyInfo study, StudyTodo todo, int pageSize) {
        // 스터디의 활동중인 모든 멤버의 Id를 미리 조회해 Map에 저장
        Map<Long, StudyMember> studyMemberMap = getActiveStudyMembers(study.getId());

        // 원격의 커밋을 한 번에 모두 추출
        List<GithubCommitResponse> commitPage = githubApiService.fetchCommits(study.getRepositoryInfo(), pageSize, todo.getTodoCode());

        // 빈 페이지의 경우 탈출
        if (commitPage.isEmpty()) return;

        // 사용자 조회 로직을 최소화하기 위해 조회한 페이지에 관여된 사용자의 githubId를 키로 map에 미리 저장
        Map<String, User> userMap = getUserMap(commitPage);
        // 스터디 투두 매핑 조회 로직 최소화를 위해 map에 미리 저장
        Map<Long, StudyTodoMapping> todoMappingMap = getTodoMappingMap(todo, userMap);

        // StudyCommit으로 저장되지 않은 것만 필터링
        List<GithubCommitResponse> unsavedCommits = studyCommitRepository.findUnsavedGithubCommits(commitPage);

        // 검증할 컨벤션 조회
        StudyConvention conventions = studyConventionRepository.findByStudyInfoIdAndContent(study.getId(), DEFAULT_CONTENT).orElseThrow(() -> {
            log.error(">>>> {} : {} <<<<", DEFAULT_CONTENT, ExceptionMessage.CONVENTION_NOT_FOUND.getText());
            throw new ConventionException(ExceptionMessage.CONVENTION_NOT_FOUND);
        });

        // 저장할 커밋 필터링
        List<StudyCommit> commitList = filterCommit(todo, unsavedCommits, userMap, studyMemberMap, conventions, todoMappingMap);

        studyCommitRepository.saveAll(commitList);

        log.info(">>>> [ 원격 레포지토리로부터 {}개의 커밋 업데이트를 성공하였습니다.] <<<<", commitList.size());
    }

    private Map<Long, StudyMember> getActiveStudyMembers(Long studyId) {
        return studyMemberRepository.findActiveMembersByStudyInfoId(studyId).stream()
                .collect(Collectors.toMap(StudyMember::getUserId, Function.identity()));
    }

    private List<StudyCommit> filterCommit(StudyTodo todo, List<GithubCommitResponse> unsavedCommits,
                                           Map<String, User> userMap, Map<Long, StudyMember> studyMemberMap,
                                           StudyConvention conventions, Map<Long, StudyTodoMapping> todoMappingMap) {

        return unsavedCommits.stream()
                .filter(commit -> {
                    User user = userMap.get(commit.getAuthorName());

                    // 활동 중인 스터디 멤버인지 확인
                    if (user == null) {
                        log.info(">>>> 앱 가입자가 아니므로 저장되지 않았습니다. 커밋 작성자: {}", commit.getAuthorName());
                        return false;
                    }

                    if (!studyMemberMap.containsKey(user.getId())) {
                        log.info(">>>> 활동중인 스터디원이 아니므로 저장되지 않았습니다. 커밋 작성자: {}", commit.getAuthorName());
                        return false;
                    }

                    // 컨벤션을 지켰는지 확인
                    if (!studyConventionService.checkConvention(conventions.getContent(), commit.getMessage())) {
                        log.info(">>>> 컨벤션을 지키지 않은 커밋이므로 저장되지 않았습니다. 커밋 sha: {}", commit.getSha());
                        return false;
                    }

                    // 커밋 중복 체크
                    if (studyCommitRepository.existsByCommitSHA(commit.getSha())) {
                        log.info(">>>> 중복된 커밋이므로 저장되지 않았습니다. 커밋 sha: {}", commit.getSha());
                        return false;
                    }

                    return true;
                })
                .map(commit -> {
                    User findUser = userMap.get(commit.getAuthorName());
                    StudyTodoMapping todoMapping = todoMappingMap.get(findUser.getId());

                    // 마감일이 지났는지 확인
                    if (todoMapping != null) {
                        if (commit.getCommitDate().isAfter(todo.getTodoDate())) {
                            log.info(">>>> 마감일이 지났으므로 지각 처리되었습니다. 커밋 sha: {}", commit.getSha());

                            // 마감일이 지났다면 상태를 TODO_OVERDUE로 변경
                            todoMapping.updateTodoMappingStatus(StudyTodoStatus.TODO_OVERDUE);
                        } else {
                            // 마감일이 지나지 않았다면 상태를 TODO_COMPLETE로 변경
                            todoMapping.updateTodoMappingStatus(StudyTodoStatus.TODO_COMPLETE);
                        }
                    }

                    return StudyCommit.of(findUser.getId(), todo, commit, CommitStatus.COMMIT_APPROVAL);
                })
                .toList();

    }

    private Map<String, User> getUserMap(List<GithubCommitResponse> commitPage) {
        List<String> githubIds = commitPage.stream()
                .map(GithubCommitResponse::getAuthorName)
                .distinct() // 중복 제거
                .toList();

        List<User> users = userRepository.findByGithubIdIn(githubIds);
        return users.stream()
                .collect(Collectors.toMap(User::getGithubId, Function.identity()));
    }

    private Map<Long, StudyTodoMapping> getTodoMappingMap(StudyTodo todo, Map<String, User> userMap) {
        List<Long> userIds = userMap.values().stream()
                .map(User::getId)
                .toList();

        List<StudyTodoMapping> todoMappings = studyTodoMappingRepository.findByTodoIdAndUserIds(todo.getId(), userIds);
        return todoMappings.stream()
                .collect(Collectors.toMap(StudyTodoMapping::getUserId, Function.identity()));
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
