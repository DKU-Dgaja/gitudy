package com.example.backend.study.api.service.github;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.convention.StudyConvention;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.service.github.response.GithubCommitResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;
import static com.example.backend.domain.define.account.user.constant.UserRole.USER;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class GithubApiServiceTest extends TestConfig {
    private final String REPOSITORY_OWNER = "jusung-c";
    private final String REPOSITORY_NAME = "Github-Api-Test";

    @Autowired
    private GithubApiService githubApiService;

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
    void 깃허브_레포지토리_조회_테스트() {
        // given
        RepositoryInfo repo = RepositoryInfo.builder()
                .owner(REPOSITORY_OWNER)
                .name(REPOSITORY_NAME)
                .branchName("main")
                .build();

        // when
        GHRepository repository = githubApiService.getRepository(repo);

        // then
        assertAll(
                () -> assertEquals(repository.getOwnerName(), REPOSITORY_OWNER),
                () -> assertEquals(repository.getName(), REPOSITORY_NAME)
        );
    }

    @Test
    void 깃허브_레포지토리의_커밋_리스트_조회_테스트() {
        // given
        int pageSize = 5;
        String todoCode = "6PHP1b";

        RepositoryInfo repo = RepositoryInfo.builder()
                .owner("DKU-Dgaja")
                .name("gitudy-study-jusung")
                .branchName("main")
                .build();

        int expectedSize = 1;

        // when
        List<GithubCommitResponse> commits = githubApiService.fetchCommits(repo, pageSize, todoCode);

        System.out.println("commits.size() = " + commits.size());
        for (var c : commits) {
            System.out.println("c.getAuthorName() = " + c.getAuthorName());
            System.out.println("c.getMessage() = " + c.getMessage());
            System.out.println("c.getSha() = " + c.getSha());
        }

        // then
        assertEquals(expectedSize, commits.size());
        for (var c : commits) {
            assertTrue(c.getMessage().startsWith(todoCode));
        }
    }

    @Test
    void 깃허브_레포지토리의_커밋_리스트_조회_테스트A() {
        // given
        int pageSize = 5;
        String todoCode = "qwe321";

        RepositoryInfo repo = RepositoryInfo.builder()
                .owner(REPOSITORY_OWNER)
                .name(REPOSITORY_NAME)
                .branchName("main")
                .build();

        int expectedSize = 4;

        // when
        List<GithubCommitResponse> commits = githubApiService.fetchCommits(repo, pageSize, todoCode);

//        System.out.println("commits.size() = " + commits.size());
//        for (var c : commits) {
//            System.out.println("c.getAuthorName() = " + c.getAuthorName());
//            System.out.println("c.getMessage() = " + c.getMessage());
//            System.out.println("c.getSha() = " + c.getSha());
//        }

        // then
        assertEquals(expectedSize, commits.size());
        for (var c : commits) {
            assertTrue(c.getMessage().startsWith(todoCode));
        }
    }

    @Test
    void 깃허브_레포지토리의_커밋_리스트_조회_테스트B() {
        // given
        int pageSize = 10;
        String todoCode = "qwe321";

        RepositoryInfo repo = RepositoryInfo.builder()
                .owner(REPOSITORY_OWNER)
                .name(REPOSITORY_NAME)
                .branchName("main")
                .build();

        int expectedSize = 4;

        // when
        List<GithubCommitResponse> commits = githubApiService.fetchCommits(repo, pageSize, todoCode);

//        System.out.println("commits.size() = " + commits.size());
//        for (var c : commits) {
//            System.out.println("c.getAuthorName() = " + c.getAuthorName());
//            System.out.println("c.getMessage() = " + c.getMessage());
//            System.out.println("c.getSha() = " + c.getSha());
//        }

        // then
        assertEquals(expectedSize, commits.size());
        for (var c : commits) {
            assertTrue(c.getMessage().startsWith(todoCode));
        }
    }

    @Test
    void 깃허브_레포지토리의_커밋_리스트_조회_테스트C() {
        // given
        int pageSize = 1;
        String todoCode = "aBc123";

        RepositoryInfo repo = RepositoryInfo.builder()
                .owner(REPOSITORY_OWNER)
                .name(REPOSITORY_NAME)
                .branchName("main")
                .build();

        int expectedSize = 5;

        // when
        List<GithubCommitResponse> commits = githubApiService.fetchCommits(repo, pageSize, todoCode);

//        System.out.println("commits.size() = " + commits.size());
//        for (var c : commits) {
//            System.out.println("c.getAuthorName() = " + c.getAuthorName());
//            System.out.println("c.getMessage() = " + c.getMessage());
//            System.out.println("c.getSha() = " + c.getSha());
//        }

        // then
        assertEquals(expectedSize, commits.size());
        for (var c : commits) {
            assertTrue(c.getMessage().startsWith(todoCode));
        }
    }

    @Test
    void 저장된_커밋_발견_시_중단() {
        // given
        int pageSize = 3;
        String todoCode = "qwe321";

        RepositoryInfo repo = RepositoryInfo.builder()
                .owner(REPOSITORY_OWNER)
                .name(REPOSITORY_NAME)
                .branchName("main")
                .build();

        User userA = userRepository.save(User.builder()
                .platformId("1")
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId(REPOSITORY_OWNER)
                .profileImageUrl("프로필이미지")
                .build());

        StudyInfo study = studyInfoRepository.save(StudyInfo.builder()
                .userId(userA.getId())
                .topic("topic")
                .status(StudyStatus.STUDY_PUBLIC)
                .repositoryInfo(RepositoryInfo.builder()
                        .owner(REPOSITORY_OWNER)
                        .name(REPOSITORY_NAME)
                        .branchName("main")
                        .build())
                .build());

        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(userA.getId(), study.getId()));

        StudyTodo todo = StudyTodoFixture.createStudyTodo(study.getId());
        todo.updateTodoCode(todoCode);
        studyTodoRepository.save(todo);

        // 이미 저장된 커밋 저장
        studyCommitRepository.save(StudyCommit.builder()
                .studyTodoId(todo.getId())
                .studyInfoId(study.getId())
                .userId(userA.getId())
                .commitSHA("42d69944ae127c14b3fb6d0c2184a58ad7dd534b")
                .message("qwe321 [jusung-c] 백준: 컨벤션 지키기")
                .commitDate(LocalDate.now().minusDays(1))
                .build());

        studyCommitRepository.save(StudyCommit.builder()
                .studyTodoId(todo.getId())
                .studyInfoId(study.getId())
                .userId(userA.getId())
                .commitSHA("1f3c6d6f96f8b31b1a1d46333f0b52ee84209125")
                .message("qwe321 [jusung-c] 프로그래머스: 컨벤션 지키기")
                .commitDate(LocalDate.now().minusDays(1))
                .build());

        // when
        List<GithubCommitResponse> commits = githubApiService.fetchCommits(repo, pageSize, todoCode);

        // then
        assertEquals(1, commits.size());
        assertEquals("qwe321 [jjjjssssuuunngg] 프로그래머스: 컨벤션 지키기", commits.get(0).getMessage());
    }

    @Test
    void 페이지_크기가_작은_경우() {
        // given
        int pageSize = 1;
        String todoCode = "qwe321";

        RepositoryInfo repo = RepositoryInfo.builder()
                .owner(REPOSITORY_OWNER)
                .name(REPOSITORY_NAME)
                .branchName("main")
                .build();

        User userA = userRepository.save(User.builder()
                .platformId("1")
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId(REPOSITORY_OWNER)
                .profileImageUrl("프로필이미지")
                .build());

        StudyInfo study = studyInfoRepository.save(StudyInfo.builder()
                .userId(userA.getId())
                .topic("topic")
                .status(StudyStatus.STUDY_PUBLIC)
                .repositoryInfo(RepositoryInfo.builder()
                        .owner(REPOSITORY_OWNER)
                        .name(REPOSITORY_NAME)
                        .branchName("main")
                        .build())
                .build());

        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(userA.getId(), study.getId()));

        StudyTodo todo = StudyTodoFixture.createStudyTodo(study.getId());
        todo.updateTodoCode(todoCode);
        studyTodoRepository.save(todo);

        // 이미 저장된 커밋 저장
        studyCommitRepository.save(StudyCommit.builder()
                .studyTodoId(todo.getId())
                .studyInfoId(study.getId())
                .userId(userA.getId())
                .commitSHA("42d69944ae127c14b3fb6d0c2184a58ad7dd534b")
                .message("qwe321 [jusung-c] 백준: 컨벤션 지키기")
                .commitDate(LocalDate.now().minusDays(1))
                .build());

        studyCommitRepository.save(StudyCommit.builder()
                .studyTodoId(todo.getId())
                .studyInfoId(study.getId())
                .userId(userA.getId())
                .commitSHA("1f3c6d6f96f8b31b1a1d46333f0b52ee84209125")
                .message("qwe321 [jusung-c] 프로그래머스: 컨벤션 지키기")
                .commitDate(LocalDate.now().minusDays(1))
                .build());

        // when
        List<GithubCommitResponse> commits = githubApiService.fetchCommits(repo, pageSize, todoCode);

        // then
        assertEquals(1, commits.size());
        assertEquals("qwe321 [jjjjssssuuunngg] 프로그래머스: 컨벤션 지키기", commits.get(0).getMessage());
    }

    @Test
    void 모든_커밋이_이미_저장된_경우() {
        // given
        int pageSize = 10;
        String todoCode = "qwe321";

        RepositoryInfo repo = RepositoryInfo.builder()
                .owner(REPOSITORY_OWNER)
                .name(REPOSITORY_NAME)
                .branchName("main")
                .build();

        User userA = userRepository.save(User.builder()
                .platformId("1")
                .platformType(GITHUB)
                .role(USER)
                .name("이름")
                .githubId(REPOSITORY_OWNER)
                .profileImageUrl("프로필이미지")
                .build());

        StudyInfo study = studyInfoRepository.save(StudyInfo.builder()
                .userId(userA.getId())
                .topic("topic")
                .status(StudyStatus.STUDY_PUBLIC)
                .repositoryInfo(RepositoryInfo.builder()
                        .owner(REPOSITORY_OWNER)
                        .name(REPOSITORY_NAME)
                        .branchName("main")
                        .build())
                .build());

        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(userA.getId(), study.getId()));

        StudyTodo todo = StudyTodoFixture.createStudyTodo(study.getId());
        todo.updateTodoCode(todoCode);
        studyTodoRepository.save(todo);

        // 이미 저장된 커밋 저장
        studyCommitRepository.save(StudyCommit.builder()
                .studyTodoId(todo.getId())
                .studyInfoId(study.getId())
                .userId(userA.getId())
                .commitSHA("42d69944ae127c14b3fb6d0c2184a58ad7dd534b")
                .message("qwe321 [jusung-c] 백준: 컨벤션 지키기")
                .commitDate(LocalDate.now().minusDays(1))
                .build());

        studyCommitRepository.save(StudyCommit.builder()
                .studyTodoId(todo.getId())
                .studyInfoId(study.getId())
                .userId(userA.getId())
                .commitSHA("1f3c6d6f96f8b31b1a1d46333f0b52ee84209125")
                .message("qwe321 [jusung-c] 프로그래머스: 컨벤션 지키기")
                .commitDate(LocalDate.now().minusDays(1))
                .build());

        studyCommitRepository.save(StudyCommit.builder()
                .studyTodoId(todo.getId())
                .studyInfoId(study.getId())
                .userId(userA.getId())
                .commitSHA("4c24b59917bf45877ef15bc0307fe25fbd480b28")
                .message("qwe321 컨벤션 무시하기")
                .commitDate(LocalDate.now().minusDays(1))
                .build());

        studyCommitRepository.save(StudyCommit.builder()
                .studyTodoId(todo.getId())
                .studyInfoId(study.getId())
                .userId(userA.getId())
                .commitSHA("f3231cd5b60fbcd8271f0e56feabbf93b530b2b4")
                .message("qwe321 [jjjjssssuuunngg] 프로그래머스: 컨벤션 지키기")
                .commitDate(LocalDate.now().minusDays(1))
                .build());

        // when
        List<GithubCommitResponse> commits = githubApiService.fetchCommits(repo, pageSize, todoCode);

        // then
        assertEquals(0, commits.size());
    }

    @Test
    void 특정_투두_코드에_해당하는_커밋이_없는_경우() {
        // given
        int pageSize = 3;
        String todoCode = "nonexistentCode";

        RepositoryInfo repo = RepositoryInfo.builder()
                .owner(REPOSITORY_OWNER)
                .name(REPOSITORY_NAME)
                .branchName("main")
                .build();

        // when
        List<GithubCommitResponse> commits = githubApiService.fetchCommits(repo, pageSize, todoCode);

        // then
        assertEquals(0, commits.size());

    }

}