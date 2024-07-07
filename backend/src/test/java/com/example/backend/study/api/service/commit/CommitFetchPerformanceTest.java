package com.example.backend.study.api.service.commit;

import com.example.backend.MockTestConfig;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.convention.StudyConvention;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.service.github.GithubApiService;
import com.example.backend.study.api.service.github.response.GithubCommitResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;
import static com.example.backend.domain.define.account.user.constant.UserRole.USER;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CommitFetchPerformanceTest extends MockTestConfig {

    private final String REPOSITORY_OWNER = "jusung-c";
    private final String REPOSITORY_NAME = "Github-Api-Test";

    @Autowired
    private StudyCommitService studyCommitService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyCommitRepository studyCommitRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyTodoRepository studyTodoRepository;

    @Autowired
    private StudyTodoMappingRepository studyTodoMappingRepository;

    @Autowired
    private StudyConventionRepository studyConventionRepository;

    @Autowired
    private StudyMemberRepository studyMemberRepository;

    @MockBean
    GithubApiService githubApiService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyCommitRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
        studyTodoMappingRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("기존 동기 로직과 비동기 로직의 성능 비교 테스트")
    public void testFetchRemoteCommitsAndSavePerformance() throws ExecutionException, InterruptedException {
        // given
        // 유저 저장
        User user = userRepository.save(User.builder()
                .platformId("1")
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId(REPOSITORY_OWNER)
                .profileImageUrl("프로필이미지")
                .build());

        // 스터디 저장
        StudyInfo study = studyInfoRepository.save(StudyInfo.builder()
                .userId(user.getId())
                .topic("topic")
                .status(StudyStatus.STUDY_PUBLIC)
                .repositoryInfo(RepositoryInfo.builder()
                        .owner(REPOSITORY_OWNER)
                        .name(REPOSITORY_NAME)
                        .branchName("main")
                        .build())
                .build());

        // 스터디원 저장
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));

        // 투두 저장
        String todoCode = "aBc123";
        StudyTodo todo = StudyTodoFixture.createStudyTodo(study.getId());
        todo.updateTodoCode(todoCode);
        studyTodoRepository.save(todo);

        // 기본 컨벤션 저장
        String conventionName = "default convention";
        String convention = "^[a-zA-Z0-9]{6} .*";

        // 컨벤션 등록
        studyConventionRepository.save(StudyConvention.builder()
                .studyInfoId(study.getId())
                .name(conventionName)
                .content(convention)
                .isActive(true)
                .build());

        // 커밋 저장
        StudyCommit savedCommit = studyCommitRepository.save(StudyCommit.builder()
                .studyInfoId(study.getId())
                .studyTodoId(todo.getId())
                .userId(user.getId())
                .message("aBc123 [jusung-c] 백준: 컨벤션 수칙 지키기")
                .commitDate(LocalDate.now())
                .status(CommitStatus.COMMIT_APPROVAL)
                .commitSHA("sha")
                .build());

        String A = "aBc123 [jusung-c] 백준: 컨벤션 지키기";

        // 첫 페이지: 이미 저장된 커밋, 새로운 커밋
        List<GithubCommitResponse> firstPage = List.of(
                GithubCommitResponse.builder().authorName(user.getGithubId()).message(A).commitDate(LocalDate.now()).sha("sha1").build(),
                GithubCommitResponse.builder().authorName(user.getGithubId()).message(savedCommit.getMessage()).commitDate(LocalDate.now()).sha("sha").build()
        );

        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(0), anyString()))
                .thenReturn(firstPage);
        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(1), anyString()))
                .thenReturn(Collections.emptyList());

        // when
        long startTime = System.currentTimeMillis();
        studyCommitService.fetchRemoteCommitsAndSave(study, todo, 5);
        long endTime = System.currentTimeMillis();

        // when
        long asyncStartTime = System.currentTimeMillis();
        CompletableFuture<Void> future = studyCommitService.fetchRemoteCommitsAndSaveAsync(study, todo, 5);
        future.get(); // 비동기 작업이 완료될 때까지 기다림
        long asyncEndTime = System.currentTimeMillis();

        // then
        System.out.println("기존 로직 실행 시간: " + (endTime - startTime) + "ms");
        System.out.println("비동기 로직 실행 시간: " + (asyncEndTime - asyncStartTime) + "ms");

        // then
        assertTrue(asyncEndTime - asyncStartTime < 100); // 100ms 이하로 즉시 반환되었는지 확인

        future.get(); // 비동기 작업이 완료될 때까지 기다림
        assertTrue(future.isDone()); // 비동기 작업이 완료되었는지 확인
        System.out.println("future.isDone() = " + future.isDone());
    }

}