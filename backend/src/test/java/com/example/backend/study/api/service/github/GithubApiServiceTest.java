package com.example.backend.study.api.service.github;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.study.api.service.github.response.GithubCommitResponse;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class GithubApiServiceTest extends TestConfig {
    private final String REPOSITORY_OWNER = "jusung-c";
    private final String REPOSITORY_NAME = "Github-Api-Test";

    @Autowired
    private GithubApiService githubApiService;

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
    void 깃허브_레포지토리의_커밋_리스트_조회_테스트A() {
        // given
        int pageNumber = 2;
        int pageSize = 5;
        String todoCode = "qwe321";

        RepositoryInfo repo = RepositoryInfo.builder()
                .owner(REPOSITORY_OWNER)
                .name(REPOSITORY_NAME)
                .branchName("main")
                .build();

        int expectedSize = 2;

        // when
        List<GithubCommitResponse> commits = githubApiService.fetchCommits(repo, pageNumber, pageSize, todoCode);

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
        int pageNumber = 1;
        int pageSize = 10;
        String todoCode = "qwe321";

        RepositoryInfo repo = RepositoryInfo.builder()
                .owner(REPOSITORY_OWNER)
                .name(REPOSITORY_NAME)
                .branchName("main")
                .build();

        int expectedSize = 4;

        // when
        List<GithubCommitResponse> commits = githubApiService.fetchCommits(repo, pageNumber, pageSize, todoCode);

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
        int pageNumber = 1;
        int pageSize = 3;
        String todoCode = "aBc123";

        RepositoryInfo repo = RepositoryInfo.builder()
                .owner(REPOSITORY_OWNER)
                .name(REPOSITORY_NAME)
                .branchName("main")
                .build();

        int expectedSize = 1;

        // when
        List<GithubCommitResponse> commits = githubApiService.fetchCommits(repo, pageNumber, pageSize, todoCode);

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

}