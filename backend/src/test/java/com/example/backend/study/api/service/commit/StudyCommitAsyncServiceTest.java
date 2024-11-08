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
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.service.github.GithubApiService;
import com.example.backend.study.api.service.github.response.GithubCommitResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;
import static com.example.backend.domain.define.account.user.constant.UserRole.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class StudyCommitAsyncServiceTest extends MockTestConfig {

    private final String REPOSITORY_OWNER = "jusung-c";
    private final String REPOSITORY_NAME = "Github-Api-Test";

    @MockBean
    private GithubApiService githubApiService;

    @Autowired
    private StudyCommitService studyCommitService;

    @Autowired
    private StudyCommitAsyncService studyCommitAsyncService;

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

    @Autowired
    @Qualifier("customExecutor")
    private Executor customExecutor;

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
    public void 동기_작업_성능_테스트() throws ExecutionException, InterruptedException {
        // given: 테스트를 위한 유저와 연관 데이터 설정
        User user = userRepository.save(User.builder()
                .platformId("1")
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId(REPOSITORY_OWNER)
                .profileImageUrl("프로필이미지")
                .build());

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
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));

        String todoCode = "aBc123";
        StudyTodo todo = StudyTodoFixture.createStudyTodo(study.getId());
        todo.updateTodoCode(todoCode);
        studyTodoRepository.save(todo);

        studyTodoMappingRepository.save(StudyTodoMapping.builder()
                .todoId(todo.getId())
                .userId(user.getId())
                .status(StudyTodoStatus.TODO_INCOMPLETE)
                .build());

        String convention = "^[A-Za-z0-9]{6} \\[[A-Za-z가-힣0-9\\W]+\\] [A-Za-z가-힣]+: .+\\n?\\n?.*";
        studyConventionRepository.save(StudyConvention.builder()
                .studyInfoId(study.getId())
                .name("커밋 메세지 규칙")
                .content(convention)
                .isActive(true)
                .build());

        // Github API 호출 시간을 대신하는 지연 시간 설정 - 다수의 커밋
        List<GithubCommitResponse> commitsA = List.of(
                GithubCommitResponse.builder().authorName(user.getGithubId()).message(todoCode + " [jusung-c] 백준: 문제1").commitDate(LocalDate.now()).sha("sha1").build(),
                GithubCommitResponse.builder().authorName(user.getGithubId()).message(todoCode + " [jusung-c] 백준: 문제2").commitDate(LocalDate.now()).sha("sha2").build(),
                GithubCommitResponse.builder().authorName(user.getGithubId()).message(todoCode + " [jusung-c] 백준: 문제3").commitDate(LocalDate.now()).sha("sha3").build()
        );

        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(0), anyInt(), anyString()))
                .thenAnswer(t -> {
                    Thread.sleep(2000); // Mock 처리로 2초 지연
                    return commitsA;
                });
        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(1), anyInt(), anyString()))
                .thenReturn(Collections.emptyList());

        // 동기 작업 실행
        long syncStartTime = System.currentTimeMillis();
        studyCommitService.fetchRemoteCommitsAndSave(study, todo);
        long syncEndTime = System.currentTimeMillis();

        // then
        System.out.println("동기 로직 실행 시간: " + (syncEndTime - syncStartTime) + "ms");
    }

    @Test
    public void 비동기_작업_성능_테스트() throws ExecutionException, InterruptedException {
        // given: 테스트를 위한 유저와 연관 데이터 설정
        User user = userRepository.save(User.builder()
                .platformId("1")
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId(REPOSITORY_OWNER)
                .profileImageUrl("프로필이미지")
                .build());

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
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));

        String todoCode = "aBc123";
        StudyTodo todo = StudyTodoFixture.createStudyTodo(study.getId());
        todo.updateTodoCode(todoCode);
        studyTodoRepository.save(todo);

        studyTodoMappingRepository.save(StudyTodoMapping.builder()
                .todoId(todo.getId())
                .userId(user.getId())
                .status(StudyTodoStatus.TODO_INCOMPLETE)
                .build());

        String convention = "^[A-Za-z0-9]{6} \\[[A-Za-z가-힣0-9\\W]+\\] [A-Za-z가-힣]+: .+\\n?\\n?.*";
        studyConventionRepository.save(StudyConvention.builder()
                .studyInfoId(study.getId())
                .name("커밋 메세지 규칙")
                .content(convention)
                .isActive(true)
                .build());

        // Github API 호출 시간을 대신하는 지연 시간 설정 - 다수의 커밋
        List<GithubCommitResponse> commitsA = List.of(
                GithubCommitResponse.builder().authorName(user.getGithubId()).message(todoCode + " [jusung-c] 백준: 문제1").commitDate(LocalDate.now()).sha("sha1").build(),
                GithubCommitResponse.builder().authorName(user.getGithubId()).message(todoCode + " [jusung-c] 백준: 문제2").commitDate(LocalDate.now()).sha("sha2").build(),
                GithubCommitResponse.builder().authorName(user.getGithubId()).message(todoCode + " [jusung-c] 백준: 문제3").commitDate(LocalDate.now()).sha("sha3").build()
        );

        when(githubApiService.fetchAllCommits(any(RepositoryInfo.class), eq(0), anyInt()))
                .thenAnswer(t -> {
                    Thread.sleep(2000); // Mock 처리로 2초 지연
                    return commitsA;
                });
        when(githubApiService.fetchCommits(any(RepositoryInfo.class), eq(1), anyInt(), anyString()))
                .thenReturn(Collections.emptyList());

        // 비동기 작업 실행 및 완료 대기
        long asyncStartTime = System.currentTimeMillis();
        CompletableFuture<Void> future = studyCommitAsyncService.fetchRemoteCommitsForStudyAndTodoAsync(study, todo);
        long responseEndTime = System.currentTimeMillis();
        future.get(); // 비동기 작업이 완료될 때까지 기다림
        long asyncEndTime = System.currentTimeMillis();

        // then
        System.out.println("비동기 적용 후 UI 응답 시간: " + (responseEndTime - asyncStartTime) + "\n비동기 로직 완료까지의 실제 시간: " + (asyncEndTime - asyncStartTime) + "ms");

        assertTrue(future.isDone()); // 비동기 작업이 완료되었는지 확인
    }

    @Test
    public void 비동기_작업_병렬_성능_테스트() throws ExecutionException, InterruptedException {
        // given: 유저와 여러 스터디 및 투두 생성
        User user = userRepository.save(User.builder()
                .platformId("1")
                .platformType(GITHUB)
                .role(USER)
                .name("테스트 유저")
                .githubId(REPOSITORY_OWNER)
                .profileImageUrl("프로필 이미지")
                .build());

        // 10개의 스터디와 각 투두를 생성
        for (int i = 0; i < 10; i++) {
            StudyInfo study = studyInfoRepository.save(StudyInfo.builder()
                    .userId(user.getId())
                    .topic("Topic " + i)
                    .status(StudyStatus.STUDY_PUBLIC)
                    .repositoryInfo(RepositoryInfo.builder()
                            .owner(REPOSITORY_OWNER)
                            .name(REPOSITORY_NAME)
                            .branchName("main")
                            .build())
                    .build());
            studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));

            StudyTodo todo = StudyTodoFixture.createStudyTodo(study.getId());
            studyTodoRepository.save(todo);

            studyTodoMappingRepository.save(StudyTodoMapping.builder()
                    .todoId(todo.getId())
                    .userId(user.getId())
                    .status(StudyTodoStatus.TODO_INCOMPLETE)
                    .build());
        }

        // 각 fetch가 2초 소요되도록 설정
        when(githubApiService.fetchAllCommits(any(RepositoryInfo.class), anyInt(), anyInt()))
                .thenAnswer(invocation -> {
                    int pageNumber = invocation.getArgument(1);
                    Thread.sleep(200); // 200ms 지연

                    // 0번 페이지만 커밋 데이터를 반환하고, 이후 페이지는 빈 리스트 반환
                    if (pageNumber == 0) {
                        return List.of(GithubCommitResponse.builder()
                                .authorName(user.getGithubId())
                                .message("test commit")
                                .commitDate(LocalDate.now())
                                .sha("sha")
                                .build());
                    } else {
                        return Collections.emptyList();
                    }
                });

        // 비동기 스케줄링 메서드 실행 및 성능 측정
        long startTime = System.currentTimeMillis();
        CompletableFuture<Void> future = studyCommitAsyncService.fetchRemoteCommitsForAllStudiesAsync();
        long responseTime = System.currentTimeMillis();

        future.get(); // 모든 병렬 작업이 완료될 때까지 대기
        long endTime = System.currentTimeMillis();

        System.out.println("UI 응답 시간: " + (responseTime - startTime) + "ms");
        System.out.println("병렬 스케줄링 작업 전체 완료 시간: " + (endTime - startTime) + "ms");

        // then: 병렬로 실행되었는지 확인 (동기식의 최소값보다 빨라야함)
        assertTrue((endTime - startTime) < 2000, "병렬 작업이 예상보다 오래 걸렸습니다.");
    }

    @Test
    public void 순차적_비동기_작업_성능_테스트() throws ExecutionException, InterruptedException {
        // given: 유저와 여러 스터디 및 투두 생성
        User user = userRepository.save(User.builder()
                .platformId("1")
                .platformType(GITHUB)
                .role(USER)
                .name("테스트 유저")
                .githubId(REPOSITORY_OWNER)
                .profileImageUrl("프로필 이미지")
                .build());

        // 10개의 스터디와 각 투두를 생성
        for (int i = 0; i < 10; i++) {
            StudyInfo study = studyInfoRepository.save(StudyInfo.builder()
                    .userId(user.getId())
                    .topic("Topic " + i)
                    .status(StudyStatus.STUDY_PUBLIC)
                    .repositoryInfo(RepositoryInfo.builder()
                            .owner(REPOSITORY_OWNER)
                            .name(REPOSITORY_NAME)
                            .branchName("main")
                            .build())
                    .build());
            studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));

            StudyTodo todo = StudyTodoFixture.createStudyTodo(study.getId());
            studyTodoRepository.save(todo);

            studyTodoMappingRepository.save(StudyTodoMapping.builder()
                    .todoId(todo.getId())
                    .userId(user.getId())
                    .status(StudyTodoStatus.TODO_INCOMPLETE)
                    .build());
        }

        // 각 fetch가 200ms 지연되도록 설정
        when(githubApiService.fetchAllCommits(any(RepositoryInfo.class), anyInt(), anyInt()))
                .thenAnswer(invocation -> {
                    int pageNumber = invocation.getArgument(1);
                    Thread.sleep(200); // 200ms 지연

                    // 0번 페이지만 커밋 데이터를 반환하고, 이후 페이지는 빈 리스트 반환
                    if (pageNumber == 0) {
                        return List.of(GithubCommitResponse.builder()
                                .authorName(user.getGithubId())
                                .message("test commit")
                                .commitDate(LocalDate.now())
                                .sha("sha")
                                .build());
                    } else {
                        return Collections.emptyList();
                    }
                });

        // 순차적으로 스터디별로 비동기 메서드를 호출하고 성능 측정
        long startTime = System.currentTimeMillis();
        List<StudyInfo> studies = studyInfoRepository.findAll();
        for (StudyInfo study : studies) {
            StudyTodo todo = studyTodoRepository.findByStudyInfoId(study.getId()).get(0);
            CompletableFuture<Void> future = studyCommitAsyncService.fetchRemoteCommitsForStudyAndTodoAsync(study, todo);
            future.get();
        }
        long endTime = System.currentTimeMillis();

        System.out.println("순차적 비동기 스케줄링 작업 전체 완료 시간: " + (endTime - startTime) + "ms");

        // 예상대로 순차적으로 수행되었는지 확인
        assertTrue((endTime - startTime) > 2000, "순차 작업이 예상보다 빠르게 완료되었습니다.");
    }
}