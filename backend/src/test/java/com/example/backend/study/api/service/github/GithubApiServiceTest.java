package com.example.backend.study.api.service.github;

import com.example.backend.TestConfig;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.github.GithubApiException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.github.GithubApiToken;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.external.clients.github.GithubApiTokenClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHHook;
import org.kohsuke.github.GHRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class GithubApiServiceTest extends TestConfig {
    private static final String NEW_TOKEN = "new_token";

    @Value("${github.api.webhookURL}")
    private String webhookUrl;

    @Value("${github.api.token}")
    private String githubApiToken;

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
    private StudyMemberRepository studyMemberRepository;

    @Autowired
    private GithubApiTokenService githubApiTokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

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

    // 웹에서 테스트 해야 합니다.
    //@Test
    void 깃허브_레포지토리_생성_테스트() throws IOException {
        // given
        String repoName = "test-repo2";
        String description = "[gitudy] Github API test repository description";
        RepositoryInfo repoInfo = RepositoryInfo.builder()
                .owner("rndudals")
                .name(repoName)
                .branchName("main")
                .build();

        // when
        GHRepository createdRepository = githubApiService.createRepository(githubApiToken, repoInfo, description);

        // then
        assertNotNull(createdRepository);
        assertEquals(repoName, createdRepository.getName());
        assertEquals(description, createdRepository.getDescription());
        assertFalse(createdRepository.isPrivate());

        // 웹훅 확인
        boolean webhookRegistered = isWebhookRegistered(createdRepository, webhookUrl);
        assertTrue(webhookRegistered, "웹훅이 등록되지 않았습니다.");
    }

    // 웹에서 테스트 해야 합니다.
    // @Test
    void 깃허브_레포지토리_생성_중복_예외_테스트() {
        // given
        String repoName = "test-repo-duplicate";
        String description = "[gitudy] Github API duplicate test repository description";
        RepositoryInfo repoInfo = RepositoryInfo.builder()
                .owner("rndudals")
                .name(repoName)
                .branchName("main")
                .build();

        // 먼저 동일한 이름의 레포지토리를 생성하여 중복 상태를 만듭니다.
        githubApiService.createRepository(githubApiToken, repoInfo, description);

        // when, then
        GithubApiException exception = assertThrows(GithubApiException.class, () -> {
            githubApiService.createRepository(githubApiToken, repoInfo, description);
        });

        assertEquals(ExceptionMessage.GITHUB_API_REPOSITORY_ALREADY_EXISTS.getText(), exception.getMessage());
    }

    private boolean isWebhookRegistered(GHRepository repository, String webhookUrl) throws IOException {
        List<GHHook> hooks = repository.getHooks();
        return hooks.stream()
                .anyMatch(hook -> webhookUrl.equals(hook.getConfig().get("url")));
    }

    @Test
    void 해당_레포지토리가_이미_존재하는_경우_true를_반환한다() {
        // given
        String owner = "jusung-c";
        String repo = "Algo";

        // when
        assertTrue(githubApiService.repositoryExists(githubApiToken, owner, repo));
    }

    @Test
    void 해당_레포지토리가_존재하지_않는_경우_false를_반환한다() {
        // given
        String owner = "jusung-c";
        String repo = "non-exist-repo";

        // when
        assertFalse(githubApiService.repositoryExists(githubApiToken, owner, repo));
    }

    @Test
    void 깃허브_토큰_재발급_성공_테스트_Mock() {
        // given
        String oldToken = "old_token";
        String expectedNewToken = "new_token";

        User user = userRepository.save(UserFixture.generateAuthJusung());
        githubApiTokenService.saveToken(oldToken, user.getId());

        MockGithubApiTokenClients mockGithubApiTokenClients = new MockGithubApiTokenClients(expectedNewToken);
        GithubApiService githubApiService = new GithubApiService(githubApiTokenService, authService, mockGithubApiTokenClients, objectMapper);

        // when
        String newToken = githubApiService.resetGithubToken(oldToken, user.getId());
        GithubApiToken savedToken = githubApiTokenService.getToken(user.getId());

        assertEquals(expectedNewToken, newToken);
        assertEquals(expectedNewToken, savedToken.githubApiToken());
    }

    @Test
    void 잘못된_깃허브_토큰으로_재발급을_시도하면_실패한다_Mock() {
        // given
        String invalid = "invalid";
        String expectedNewToken = "new_token";

        User user = userRepository.save(UserFixture.generateAuthJusung());
        githubApiTokenService.saveToken(invalid, user.getId());

        MockGithubApiTokenClients mockGithubApiTokenClients = new MockGithubApiTokenClients(expectedNewToken);
        mockGithubApiTokenClients.setException(true);
        GithubApiService githubApiService = new GithubApiService(githubApiTokenService, authService, mockGithubApiTokenClients, objectMapper);

        // when
        GithubApiException exception = assertThrows(GithubApiException.class, () -> githubApiService.resetGithubToken(invalid, user.getId()));

        // then
        assertEquals(ExceptionMessage.GITHUB_API_RESET_TOKEN_FAIL.getText(), exception.getMessage());
    }

    @Test
    void 깃허브_토큰_재발급_응답_추출_테스트() {
        // given
        String response = "{\n" +
                "  \"id\": 1,\n" +
                "  \"url\": \"https://HOSTNAME/authorizations/1\",\n" +
                "  \"scopes\": [\n" +
                "    \"public_repo\"\n" +
                "  ],\n" +
                "  \"token\": \"test\",\n" +
                "  \"token_last_eight\": \"test\",\n" +
                "  \"hashed_token\": \"test\",\n" +
                "  \"app\": {\n" +
                "    \"url\": \"http://my-github-app.com\",\n" +
                "    \"name\": \"my github app\",\n" +
                "    \"client_id\": \"test\"\n" +
                "  },\n" +
                "  \"note\": \"optional note\",\n" +
                "  \"note_url\": \"http://optional/note/url\",\n" +
                "  \"updated_at\": \"2011-09-06T20:39:23Z\",\n" +
                "  \"created_at\": \"2011-09-06T17:26:27Z\",\n" +
                "  \"expires_at\": \"2011-10-06T17:26:27Z\",\n" +
                "  \"fingerprint\": \"test\"\n" +
                "}";

        String expectedToken = "test";

        // when
        String token = githubApiService.parseTokenFromResponse(response);

        // then
        assertEquals(expectedToken, token);
    }


    static class MockGithubApiTokenClients implements GithubApiTokenClient {
        String newToken;
        private boolean exception;

        public MockGithubApiTokenClients(String newToken) {
            this.newToken = newToken;
        }

        public void setException(boolean exception) {
            this.exception = exception;
        }

        @Override
        public String resetGithubApiToken(String clientId, String authorizationHeader, String apiVersion, String contentType, String accept, String requestBody) {
            if (exception) {
                throw new RuntimeException("Token reset failed");
            }

            return "{\"token\":\"" + newToken + "\"}";
        }
    }
}