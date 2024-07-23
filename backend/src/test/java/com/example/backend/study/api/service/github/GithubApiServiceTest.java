package com.example.backend.study.api.service.github;

import com.example.backend.TestConfig;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.github.GithubApiException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
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
import com.example.backend.study.api.service.github.response.GithubCommitResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHHook;
import org.kohsuke.github.GHRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;
import static com.example.backend.domain.define.account.user.constant.UserRole.USER;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class GithubApiServiceTest extends TestConfig {
    private final String REPOSITORY_OWNER = "jusung-c";
    private final String REPOSITORY_NAME = "Github-Api-Test";

    @Value("${github.api.webhookURL}")
    private String webhookUrl;

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
        GHRepository createdRepository = githubApiService.createRepository(repoInfo, description);

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
        githubApiService.createRepository(repoInfo, description);

        // when, then
        GithubApiException exception = assertThrows(GithubApiException.class, () -> {
            githubApiService.createRepository(repoInfo, description);
        });

        assertEquals(ExceptionMessage.GITHUB_API_REPOSITORY_ALREADY_EXISTS.getText(), exception.getMessage());
    }

    private boolean isWebhookRegistered(GHRepository repository, String webhookUrl) throws IOException {
        List<GHHook> hooks = repository.getHooks();
        return hooks.stream()
                .anyMatch(hook -> webhookUrl.equals(hook.getConfig().get("url")));
    }
}