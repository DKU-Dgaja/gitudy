package com.example.backend.study.api.service.github;

import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.github.GithubApiException;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.external.clients.github.GithubApiTokenClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GithubApiService {

    private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";
    private static final String API_VERSION = "2022-11-28";
    private static final String ACCEPT_HEADER = "application/vnd.github+json";

    @Value("${github.api.webhookURL}")
    private String webhookUrl;

    @Value("${oauth2.client.github.client-id}")
    private String clientId;

    @Value("${oauth2.client.github.client-secret}")
    private String clientSecret;

    private final GithubApiTokenService githubApiTokenService;

    private final AuthService authService;
    private final GithubApiTokenClient githubApiTokenClient;
    private final ObjectMapper objectMapper;

    // 깃허브 통신을 위한 커넥션 생성
    public GitHub connectGithub(String githubApiToken, String githubId) {
        try {
            return tryConnectGithub(githubApiToken);

        } catch (IOException e) {
            if (isTokenExpired(e)) {

                log.warn(">>>> 유효하지 않은 토큰입니다. 재발급을 시도합니다...");

                // 재발급을 위한 유저 조회
                Long userId = authService.findUserIdByGithubIdOrElseThrowException(githubId);

                // 토큰 재발급
                String newToken = resetGithubToken(githubApiToken, userId);

                // 깃허브 api 연결 재시도
                try {
                    return tryConnectGithub(newToken);
                } catch (IOException re) {
                    log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_RESET_TOKEN_RETRY_FAIL.getText(), re.getMessage());
                    throw new GithubApiException(ExceptionMessage.GITHUB_API_RESET_TOKEN_RETRY_FAIL);
                }
            } else {
                // 다른 원인의 에러일 경우
                log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_CONNECTION_ERROR.getText(), e.getMessage());
                throw new GithubApiException(ExceptionMessage.GITHUB_API_CONNECTION_ERROR);
            }
        }
    }

    @Transactional
    public String resetGithubToken(String oldToken, Long userId) {
        String authorizationHeader = AUTHORIZATION_HEADER_PREFIX + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        String requestBody = "{\"access_token\":\"" + oldToken + "\"}";

        try {
            String response = githubApiTokenClient.resetGithubApiToken(
                    clientId,
                    authorizationHeader,
                    API_VERSION,
                    MediaType.APPLICATION_JSON_VALUE,
                    ACCEPT_HEADER,
                    requestBody
            );

            String newToken = parseTokenFromResponse(response);

            // 재발급 받은 토큰으로 업데이트
            log.info("재발급 받은 토큰을 새로 저장하는 중.. (userId: {})", userId);
            githubApiTokenService.saveToken(newToken, userId);
            log.info("재발급 받은 토큰 저장 완료 (userId: {})", userId);


            return newToken;
        } catch (RuntimeException e) {
            log.error(">>>> [ {} ] <<<<", ExceptionMessage.GITHUB_API_RESET_TOKEN_FAIL.getText());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_RESET_TOKEN_FAIL);
        }
    }

    public String parseTokenFromResponse(String jsonResponse) {
        // JSON 파싱 필요 - "token" 항목 추출
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode tokenNode = rootNode.path("token");
            if (tokenNode.isMissingNode()) {
                throw new GithubApiException(ExceptionMessage.GITHUB_API_RESET_TOKEN_FAIL);
            }
            return tokenNode.asText();
        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_RESET_TOKEN_FAIL.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_RESET_TOKEN_FAIL);
        }
    }

    @NonNull
    private static GitHub tryConnectGithub(String githubApiToken) throws IOException {
        GitHub github = new GitHubBuilder().withOAuthToken(githubApiToken).build();

        github.checkApiUrlValidity();
        log.info(">>>> [ 깃허브 api 연결에 성공하였습니다. ] <<<<");

        return github;
    }

    private boolean isTokenExpired(IOException e) {
        Throwable cause = e.getCause();

        // 깃허브 api 라이브러리의 HttpException 예외인지 확인
        if (cause instanceof HttpException httpException) {

            // 401 권한없음 에러인지 확인 -> 토큰 만료 or 권한 없는 경우 발생
            return httpException.getResponseCode() == 401;
        }

        return false;
    }

    // 깃허브 레포지토리 생성 메서드
    @Transactional
    public GHRepository createRepository(String githubApiToken, RepositoryInfo repoInfo, String description) {
        GitHub gitHub = connectGithub(githubApiToken, repoInfo.getOwner());

        try {
            GHCreateRepositoryBuilder repoBuilder = gitHub.createRepository(repoInfo.getName())
                    .description(description)
                    .private_(false)
                    .autoInit(true);
            GHRepository repository = repoBuilder.create();

            addWebHook(repository, webhookUrl);

            log.info(">>>> [ {} 레포지토리가 생성되었습니다. ] <<<<", repoInfo.getName());

            return repository;
        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_CREATE_REPOSITORY_ERROR.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_CREATE_REPOSITORY_ERROR);
        }
    }


    // 레포지토리에 Collaborator 추가
    @Transactional
    public void addCollaborator(String githubApiToken, RepositoryInfo repo, String githubId) {
        GitHub gitHub = connectGithub(githubApiToken, repo.getOwner());

        try {
            GHRepository repository = gitHub.getRepository(repo.getOwner() + "/" + repo.getName());
            GHUser user = gitHub.getUser(githubId);
            repository.addCollaborators(GHOrganization.Permission.PUSH, user);
            log.info(">>>> [ {} 사용자를 {} 레포지토리의 Collaborator로 추가했습니다. ] <<<<", githubId, repo.getName());
        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_ADD_COLLABORATOR_ERROR.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_ADD_COLLABORATOR_ERROR);
        }
    }

    @Transactional
    public void acceptInvitation(String githubApiToken, String githubId) {
        GitHub gitHub = connectGithub(githubApiToken, githubId);

        try {
            List<GHInvitation> invitations = new ArrayList<>(gitHub.getMyInvitations());

            if (invitations.isEmpty()) {
                log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_NO_INVITATIONS_FOUND.getText(), "No invitations found");
                throw new GithubApiException(ExceptionMessage.GITHUB_API_NO_INVITATIONS_FOUND);
            } else {
                // 가장 최근 초대를 찾기 위해 invitations 리스트를 날짜 기준으로 정렬
                sortInvitations(invitations);
                GHInvitation latestInvitation = invitations.get(0);
                log.info(">>>> [ Latest Invitation ID: {} ] <<<<", latestInvitation.getId());
                latestInvitation.accept();
                log.info(">>>> [ Invitation accepted. ] <<<<");
            }
        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_ACCEPT_INVITATION_ERROR.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_ACCEPT_INVITATION_ERROR);
        }
    }

    private static void sortInvitations(List<GHInvitation> invitations) {
        invitations.sort(Comparator.comparing(GithubApiService::getCreatedAtSafe).reversed());
    }

    private static Date getCreatedAtSafe(GHInvitation invitation) {
        try {
            return invitation.getCreatedAt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addWebHook(GHRepository repository, String webhookUrl) throws IOException {
        // 웹훅 추가
        repository.createHook(
                "web",
                Map.of(
                        "url", webhookUrl,
                        "content_type", "json"
                ),
                Collections.emptyList(),
                true
        );
    }

    public boolean repositoryExists(String token, String owner, String repoName) {
        GitHub gitHub = connectGithub(token, owner);

        try {
            GHRepository repository = gitHub.getRepository(owner + "/" + repoName);
            return repository != null;
        } catch (IOException e) {
            return false;  // 레포지토리를 찾을 수 없는 경우 예외가 발생하며, 이 경우 레포지토리가 존재하지 않음을 의미
        }
    }

    public void createTodoFolder(String githubApiToken, StudyTodo todo, RepositoryInfo repo) {
        GitHub gitHub = connectGithub(githubApiToken, repo.getOwner());

        try {
            GHRepository repository = gitHub.getRepository(repo.getOwner() + "/" + repo.getName());

            createTodoInfo(todo, repository);
            log.info(">>>> [ Todo 폴더 생성이 완료되었습니다. : {} ] <<<<", todo.getTodoFolderName());

        } catch (IOException e) {
            log.warn(">>>> [ {} ] <<<<", ExceptionMessage.GITHUB_API_CREATE_TODO_FOLDER_FAIL.getText());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_CREATE_TODO_FOLDER_FAIL);
        }

    }

    private void createTodoInfo(StudyTodo todo, GHRepository repository) throws IOException {
        // 파일 내용 정의
        String filePath = todo.getTodoFolderName() + "/" + todo.getTitle() + ".md";
        String commitMessage = "Create todo folder " + todo.getTodoFolderName();
        String fileContent = generateFileContent(todo);

        // 폴더 및 파일 생성
        repository.createContent()
                .path(filePath)
                .message(commitMessage)
                .content(fileContent)
                .commit();
    }

    private String generateFileContent(StudyTodo todo) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(todo.getTitle()).append("\n\n");
        sb.append("**").append(todo.getDetail()).append("**").append("\n\n");
        sb.append("**문제 링크:** [").append(todo.getTodoLink()).append("](").append(todo.getTodoLink()).append(")\n\n");
        sb.append("**마감일:** ").append(todo.getTodoDate().toString()).append("\n");
        return sb.toString();
    }

}
