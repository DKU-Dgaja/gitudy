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
import com.example.backend.domain.define.study.github.repository.GithubApiTokenRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
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
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class GithubApiServiceTest extends TestConfig {
    private final String REPOSITORY_OWNER = "jusung-c";
    private final String REPOSITORY_COLLABORATOR = "rndudals";
    private final String REPOSITORY_NAME = "Github-Api-Test";
    private final String REPOSITORY_DESCRIBE = "[gitudy] Github API test repository description";
    private final String BRANCH_NAME = "main";

    @Value("${github.api.webhookURL}")
    private String webhookUrl;

    @Value("${github.api.token}")
    private String githubApiToken;

    @Value("${github.api.token-collaborator}")
    private String githubApiTokenCollaborator;

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
    private GithubApiTokenRepository githubApiTokenRepository;

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
        githubApiTokenRepository.deleteAll();
    }

    // 웹에서 테스트 해야 합니다.
    // @Test
    void 깃허브_레포지토리_생성_테스트() throws IOException {
        // given
        RepositoryInfo repoInfo = RepositoryInfo.builder()
                .owner(REPOSITORY_OWNER)
                .name(REPOSITORY_NAME)
                .branchName(BRANCH_NAME)
                .build();

        // when
        GHRepository createdRepository = githubApiService.createRepository(githubApiToken, repoInfo, REPOSITORY_DESCRIBE);

        // then
        assertNotNull(createdRepository);
        assertEquals(REPOSITORY_NAME, createdRepository.getName());
        assertEquals(REPOSITORY_DESCRIBE, createdRepository.getDescription());
        assertFalse(createdRepository.isPrivate());

        // 웹훅 확인
        boolean webhookRegistered = isWebhookRegistered(createdRepository, webhookUrl);
        assertTrue(webhookRegistered, "웹훅이 등록되지 않았습니다.");
    }

    // 웹에서 테스트 해야 합니다.
    // @Test
    void 깃허브_레포지토리_생성_중복_예외_테스트() {
        // given
        RepositoryInfo repoInfo = RepositoryInfo.builder()
                .owner(REPOSITORY_OWNER)
                .name(REPOSITORY_NAME)
                .branchName(BRANCH_NAME)
                .build();

        // 먼저 동일한 이름의 레포지토리를 생성하여 중복 상태를 만듭니다.
        githubApiService.createRepository(githubApiToken, repoInfo, REPOSITORY_DESCRIBE);

        // when, then
        GithubApiException exception = assertThrows(GithubApiException.class, () -> {
            githubApiService.createRepository(githubApiToken, repoInfo, REPOSITORY_DESCRIBE);
        });

        assertEquals(ExceptionMessage.GITHUB_API_REPOSITORY_ALREADY_EXISTS.getText(), exception.getMessage());
    }

    // 웹에서 테스트 해야 합니다.
    // @Test
    public void Collaborator_초대요청_및_수락_성공_테스트() throws Exception {
        // 실제로 존재하는 GitHub 레포지토리와 사용자 정보로 변경
        RepositoryInfo repoInfo = new RepositoryInfo(REPOSITORY_OWNER, REPOSITORY_NAME, BRANCH_NAME);

        // 실제 GitHub API를 사용하여 Collaborator 추가
        githubApiService.addCollaborator(githubApiToken, repoInfo, REPOSITORY_COLLABORATOR);


        // 실제 GitHub API를 사용하여 초대 수락
        githubApiService.acceptInvitation(githubApiTokenCollaborator, REPOSITORY_COLLABORATOR);

        // 결과 확인은 GitHub 웹사이트에서 직접 확인
    }

    // 웹에서 테스트 해야 합니다.
    // @Test
    void 초대_목록_없음_예외_테스트() {
        GithubApiException exception = assertThrows(GithubApiException.class, () -> {
            githubApiService.acceptInvitation(githubApiToken, REPOSITORY_COLLABORATOR);
        });

        assertEquals(ExceptionMessage.GITHUB_API_NO_INVITATIONS_FOUND.getText(), exception.getMessage());
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

    //    @Test
    void 투두_폴더_생성_실제_테스트() {
        // given
        String testTitle = "[백준] 2557번 Hello World";
        String testDetail = "기본적인 입출력 문제입니다! 가볍게 풀어보세요.";
        String testTodoLink = "https://www.acmicpc.net/problem/2557";
        LocalDate testTodoDate = LocalDate.of(2024, 8, 20);
        String testRepoName = "TTTEST";

        RepositoryInfo repoInfo = RepositoryInfo.builder()
                .owner(REPOSITORY_OWNER)
                .name(testRepoName)
                .branchName(BRANCH_NAME)
                .build();

        User user = userRepository.save(UserFixture.generateAuthJusung());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createStudyInfoByRepositoryInfo(user.getId(), repoInfo));
        StudyTodo todo = studyTodoRepository.save(StudyTodo.builder()
                .studyInfoId(study.getId())
                .title(testTitle)
                .detail(testDetail)
                .todoLink(testTodoLink)
                .todoDate(testTodoDate)
                .build());

        // when
        githubApiService.createTodoFolder(githubApiToken, todo, repoInfo);
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