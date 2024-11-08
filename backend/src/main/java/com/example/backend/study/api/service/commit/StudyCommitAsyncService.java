package com.example.backend.study.api.service.commit;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.service.github.GithubApiService;
import com.example.backend.study.api.service.github.response.GithubCommitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyCommitAsyncService {
    private final StudyTodoMappingRepository studyTodoMappingRepository;
    private final UserRepository userRepository;
    private final StudyInfoRepository studyInfoRepository;
    private final StudyCommitRepository studyCommitRepository;
    private final GithubApiService githubApiService;
    private final StudyTodoRepository studyTodoRepository;
    private final StudyMemberRepository studyMemberRepository;
    @Qualifier("customExecutor") private final Executor customExecutor;

    // 사용자 요청 시 사용할 메서드 (특정 스터디, 특정 투두 대상)
    public CompletableFuture<Void> fetchRemoteCommitsForStudyAndTodoAsync(StudyInfo study, StudyTodo todo) {
        return CompletableFuture.supplyAsync(() -> fetchDataFromGithubForStudy(study))
                .thenApplyAsync(commits -> filterAndValidateData(commits, todo))
                .thenAcceptAsync(filteredCommit -> saveDataToDatabase(filteredCommit))
                .exceptionally(ex -> {
                    log.error("비동기 후속 작업 중 예외 발생 - 스터디 ID: " + study.getId() + ", 투두 ID: " + todo.getId(), ex);
                    return null;
                });
    }

    // 스케줄링 전용 메서드 (전체 스터디, 전체 투두 대상)
    public CompletableFuture<Void> fetchRemoteCommitsForAllStudiesAsync() {
        List<StudyInfo> allStudy = studyInfoRepository.findAll();

        // 각 스터디별로 비동기 작업들을 병렬로 수행
        List<CompletableFuture<List<GithubCommitResponse>>> futures = allStudy.stream()
                .map(study -> CompletableFuture.supplyAsync(() -> fetchDataFromGithubForStudy(study), customExecutor) // 각 스터디별로 커밋 데이터 비동기 호출
                        .exceptionally(ex -> {
                            log.warn("스터디 데이터 가져오기 실패: " + study.getId(), ex);
                            return null; // 실패한 경우 null 반환
                        }))
                .collect(Collectors.toList());


        // 모든 비동기 작업이 완료되면 후속 작업 수행
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v ->
                        futures.stream()
                                .map(CompletableFuture::join)
                                .filter(Objects::nonNull)
                                .flatMap(List::stream) // List<List<GitHubCommitResponse>> -> List<GitHubCommitResponse>
                                .collect(Collectors.toList()))
                .thenApply(allCommit ->
                        allStudy.stream()
                                .map(study -> filterAndValidateData(allCommit, study.getId()))  // 각 StudyInfo 별로 필터링 및 유효성 검사를 수행한 Map 반환
                                .flatMap(todoMap -> todoMap.entrySet().stream())                // 결과를 병합하기 위해 스트림 변환
                                .collect(Collectors.groupingBy(
                                        Map.Entry::getKey,                                      // key는 StudyTodo
                                        Collectors.flatMapping(entry -> entry.getValue().stream(), Collectors.toList()) // 모든 커밋을 병합
                                ))
                )
                .thenAccept(filteredCommit -> saveDataToDatabase(filteredCommit))   // 저장 작업
                .exceptionally(ex -> {
                    log.error("병렬 작업 중 예외 발생: ", ex);
                    return null;
                });
    }

    @Transactional
    public void saveDataToDatabase(Map<StudyTodo, List<GithubCommitResponse>> filteredData) {
        filteredData.forEach((todo, commits) -> {
            if (commits == null || commits.isEmpty()) return;

            // GitHub ID로 유저 정보 조회해 map으로 저장
            List<String> githubIds = commits.stream()
                    .map(GithubCommitResponse::getAuthorName)
                    .distinct()
                    .collect(Collectors.toList());

            Map<String, User> userMap = userRepository.findByGithubIdIn(githubIds).stream()
                    .collect(Collectors.toMap(User::getGithubId, Function.identity()));

            // 유저별로 투두매핑 정보 map으로 저장
            List<Long> userIds = userMap.values().stream()
                    .map(User::getId)
                    .collect(Collectors.toList());

            Map<Long, StudyTodoMapping> todoMappingMap = studyTodoMappingRepository.findByTodoIdAndUserIds(todo.getId(), userIds)
                    .stream()
                    .collect(Collectors.toMap(StudyTodoMapping::getUserId, Function.identity()));

            // 저장을 위해 StudyCommit 엔티티로 변환
            List<StudyCommit> studyCommits = commits.stream()
                    .map(commit -> {
                        User commitAuthor = userMap.get(commit.getAuthorName());
                        StudyTodoMapping todoMapping = todoMappingMap.get(commitAuthor.getId());

                        todoMapping.updateStatusByDueDate(commit.getCommitDate(), todo.getTodoDate());

                        return StudyCommit.of(commitAuthor.getId(), todo, commit, CommitStatus.COMMIT_APPROVAL);
                    })
                    .collect(Collectors.toList());

            studyCommitRepository.saveAll(studyCommits);
        });
    }

    private Map<StudyTodo, List<GithubCommitResponse>> filterAndValidateData(List<GithubCommitResponse> allCommits, Long studyId) {
        // 해당하는 스터디의 투두 리스트 조회
        List<StudyTodo> todos = studyTodoRepository.findByStudyInfoId(studyId);

        // 스터디의 활동중인 모든 멤버의 Id를 미리 조회
        Map<Long, StudyMember> studyMemberMap = studyMemberRepository.findActiveMembersByStudyInfoId(studyId).stream()
                .collect(Collectors.toMap(StudyMember::getUserId, Function.identity()));

        // 각 투두 항목에 대해 필터링
        return todos.stream()
                .collect(Collectors.toMap(
                        todo -> todo,
                        todo -> filterCommitsForTodo(todo, allCommits, studyMemberMap)
                ));
    }

    private Map<StudyTodo, List<GithubCommitResponse>> filterAndValidateData(List<GithubCommitResponse> commits, StudyTodo todo) {
        // 스터디의 활동중인 모든 멤버의 Id를 미리 조회
        Map<Long, StudyMember> studyMemberMap = studyMemberRepository.findActiveMembersByStudyInfoId(todo.getStudyInfoId()).stream()
                .collect(Collectors.toMap(StudyMember::getUserId, Function.identity()));

        List<GithubCommitResponse> validCommits = commits.stream()
                .filter(commit -> isCommitByStudyMember(commit, studyMemberMap)) // 활동중인 스터디원인가?
                .filter(commit -> isCommitForTodoCode(commit, todo))    // 투두 코드에 해당하는 커밋 필터링
                .filter(commit -> isCommitWithInDeadline(commit, todo)) // 마감일을 지켰는지 확인
                .collect(Collectors.toList());

        return Map.of(todo, validCommits);
    }


        private List<GithubCommitResponse> filterCommitsForTodo(StudyTodo todo, List<GithubCommitResponse> allCommits, Map<Long, StudyMember> studyMemberMap) {
        return allCommits.stream()
                .filter(commit -> isCommitByStudyMember(commit, studyMemberMap)) // 활동중인 스터디원인가?
                .filter(commit -> isCommitForTodoCode(commit, todo))    // 투두 코드에 해당하는 커밋 필터링
                .filter(commit -> isCommitWithInDeadline(commit, todo)) // 마감일을 지켰는지 확인
                .collect(Collectors.toList());
    }

    private boolean isCommitWithInDeadline(GithubCommitResponse commit, StudyTodo todo) {
        return !commit.getCommitDate().isAfter(todo.getTodoDate());
    }

    private boolean isCommitForTodoCode(GithubCommitResponse commit, StudyTodo todo) {
        return commit.getMessage().startsWith(todo.getTodoCode());
    }

    private boolean isCommitByStudyMember(GithubCommitResponse commit, Map<Long, StudyMember> studyMemberMap) {
        // 앱 사용자이면서 해당 스터디의 활동중인 스터디원인 경우만 필터 통과
        return userRepository.findByGithubId(commit.getAuthorName())
                .map(user -> studyMemberMap.containsKey(user.getId()))
                .orElse(false);
    }

    private List<GithubCommitResponse> fetchDataFromGithubForStudy(StudyInfo study) {
        int pageNumber = 0;
        int pageSize = 10;
        List<GithubCommitResponse> allCommits = new ArrayList<>();

        while (true) {
            // 해당 스터디의 페이지 단위 커밋 조회
            List<GithubCommitResponse> commitPage = githubApiService.fetchAllCommits(
                    study.getRepositoryInfo(),
                    pageNumber,
                    pageSize);

            if (commitPage.isEmpty()) break;

            // 저장되지 않은 커밋만 필터링 후 추가
            List<GithubCommitResponse> unsavedCommits = studyCommitRepository.findUnsavedGithubCommits(commitPage);
            allCommits.addAll(unsavedCommits);

            // 현재 페이지에 저장된 커밋이 하나라도 있으면 탈출
            if (commitPage.size() > unsavedCommits.size()) break;

            // 다음 페이지 이동
            pageNumber++;
        }

        return allCommits;
    }
}
