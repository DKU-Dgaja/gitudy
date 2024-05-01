package com.example.backend.study.api.service.commit;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.commit.CommitException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
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
    private final StudyCommitRepository studyCommitRepository;
    private final GithubApiService githubApiService;
    private final StudyConventionService studyConventionService;
    private final StudyConventionRepository studyConventionRepository;

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

    // 커밋 업데이트
    @Transactional
    public void fetchRemoteCommitsAndSave(StudyInfo study, StudyTodo todo) {
        int pageNumber = 0;
        int pageSize = 10;

        // 스터디의 활동중인 모든 멤버의 Id를 미리 조회
        Map<Long, StudyMember> studyMemberMap = studyMemberRepository.findActiveMembersByStudyInfoId(study.getId()).stream()
                .collect(Collectors.toMap(StudyMember::getUserId, Function.identity()));

        // 불러온 커밋 페이지에 저장되지 않은 커밋이 없을 때까지 반복
        while (true) {
            // 원격의 커밋 한 페이지 추출
            List<GithubCommitResponse> commitPage = githubApiService.fetchCommits(study.getRepositoryInfo(), pageNumber, pageSize, todo.getTodoCode());
            if (commitPage.isEmpty()) break;

            // 사용자 조회 로직을 최소화하기 위해 map에 미리 저장
            List<String> githubIds = commitPage.stream()
                    .map(GithubCommitResponse::getAuthorName)
                    .distinct() // 중복 제거
                    .toList();

            List<User> users = userRepository.findByGithubIdIn(githubIds);
            Map<String, User> userMap = users.stream()
                    .collect(Collectors.toMap(User::getGithubId, Function.identity()));

            // 스터디 투두 매핑 조회 로직 최소화를 위해 map에 미리 저장
            List<Long> userIds = users.stream()
                    .map(User::getId)
                    .toList();

            List<StudyTodoMapping> todoMappings = studyTodoMappingRepository.findByTodoIdAndUserIds(todo.getId(), userIds);
            Map<Long, StudyTodoMapping> todoMappingMap = todoMappings.stream()
                    .collect(Collectors.toMap(StudyTodoMapping::getUserId, Function.identity()));

            // StudyCommit으로 저장되지 않은 것만 필터링
            List<GithubCommitResponse> unsavedCommits = studyCommitRepository.findUnsavedGithubCommits(commitPage);

            // 검증할 컨벤션 조회
            StudyConventionResponse conventions = studyConventionRepository.findActiveConventionByStudyInId(study.getId());

            List<StudyCommit> commitList = unsavedCommits.stream()
                    .filter(commit -> {
                        User user = userMap.get(commit.getAuthorName());

                        return user != null &&
                                // 활동중인 스터디 멤버인지
                                studyMemberMap.containsKey(user.getId()) &&
                                // 컨벤션을 지켰는지
                                studyConventionService.checkConvention(conventions.getContent(), commit.getMessage());
                    })
                    .map(commit -> {
                        User findUser = userMap.get(commit.getAuthorName());
                        StudyTodoMapping todoMapping = todoMappingMap.get(findUser.getId());

                        // 마감일이 지났는지 확인
                        if (todoMapping != null && commit.getCommitDate().isAfter(todo.getTodoDate())) {
                            // 마감일이 지났다면 상태를 TODO_OVERDUE로 변경
                            todoMapping.updateTodoMappingStatus(StudyTodoStatus.TODO_OVERDUE);
                        } else {
                            // 마감일이 지나지 않았다면 상태를 TODO_COMPLETE로 변경
                            if (todoMapping != null) {
                                todoMapping.updateTodoMappingStatus(StudyTodoStatus.TODO_COMPLETE);
                            }
                        }

                        return StudyCommit.of(findUser.getId(), todo, commit, CommitStatus.COMMIT_APPROVAL);
                    })
                    .toList();

            studyCommitRepository.saveAll(commitList);

            // 페이지 내 저장된 커밋이 하나라도 있으면 현재 페이지까지만 저장 로직 적용
            if (commitPage.size() > unsavedCommits.size()) break;

            // 다음 페이지로
            pageNumber++;
        }

        log.info(">>>> [ 원격 레포지토리로부터 커밋 업데이트를 성공하였습니다. ] <<<<");
    }
}
