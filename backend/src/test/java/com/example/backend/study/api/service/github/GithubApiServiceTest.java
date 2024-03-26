package com.example.backend.study.api.service.github;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.service.github.response.GithubCommitResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class GithubApiServiceTest extends TestConfig {
    @Value("${github.api.token}")
    private String token;

    private final String REPOSITORY_OWNER = "jusung-c";
    private final String REPOSITORY_NAME = "Github-Api-Test";

    @Autowired
    private GithubApiService githubApiService;

    @Test
    void 깃허브_레포지토리_조회_테스트() {
        // given
        GitHub gitHub = githubApiService.connectGithub(token);

        // when
        GHRepository repository = githubApiService.getRepository(gitHub, REPOSITORY_OWNER, REPOSITORY_NAME);

        // then
        assertAll(
                () -> assertEquals(repository.getOwnerName(), REPOSITORY_OWNER),
                () -> assertEquals(repository.getName(), REPOSITORY_NAME)
        );
    }

    @Test
    void 깃허브_레포지토리의_커밋_리스트_조회_테스트() {
        // given
        GitHub gitHub = githubApiService.connectGithub(token);
        String folderPath = "TODO_NAME";

        int expectedSize = 1;
        String expectedName = "이주성";
        String expectedMessage = "CREATE_NEW_FOLDER";

        // when
        GHRepository repository = githubApiService.getRepository(gitHub, REPOSITORY_OWNER, REPOSITORY_NAME);
        List<GithubCommitResponse> commits = githubApiService.pullCommits(repository, folderPath);

        // then
        assertAll(
                () -> assertEquals(commits.size(), expectedSize),
                () -> assertEquals(commits.get(0).getAuthorName(), expectedName),
                () -> assertEquals(commits.get(0).getMessage(), expectedMessage)
        );
    }

}