package com.example.backend.study.api.service.github;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.github.GithubApiException;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.external.clients.github.GithubApiTokenClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GithubApiService {

    @Value("${github.api.webhookURL}")
    private String webhookUrl;

    @Value("${oauth2.client.github.client-id}")
    private String clientId;

    @Value("${oauth2.client.github.client-secret}")
    private String clientSecret;

    private final GithubApiTokenService githubApiTokenService;

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


                // 토큰 재발급
                String newToken = resetGithubToken(githubApiToken, githubId);

                System.out.println("newToken = " + newToken);

                // 깃허브 api 연결 재시도
                try {
                    return tryConnectGithub(newToken);
                } catch (IOException re) {

                    // 재시도 후에도 실패한 경우 토큰 삭제
//                    githubApiTokenService.deleteToken(userId);

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
    public String resetGithubToken(String oldToken, String githubId) {
        String authorizationHeader = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        String requestBody = "{\"access_token\":\"" + oldToken + "\"}";
        String apiVersion = "2022-11-28";
        String accept = "application/vnd.github+json";

        try {
            String response = githubApiTokenClient.resetGithubApiToken(
                    clientId,
                    authorizationHeader,
                    apiVersion,
                    MediaType.APPLICATION_JSON_VALUE,
                    accept,
                    requestBody
            );

            String newToken = parseTokenFromResponse(response);

            System.out.println("newToken = " + newToken);

            // 재발급 받은 토큰으로 업데이트
            // githubApiTokenService.saveToken(newToken, githubId); // 예시로 주석 처리
            return newToken;
        } catch (RuntimeException e) {

            System.out.println("e.getMessage() = " + e.getMessage());

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

            // 레포지토리 이름 중복 확인
            if (repositoryExists(gitHub, repoInfo.getOwner() + "/" + repoInfo.getName())) {
                log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_REPOSITORY_ALREADY_EXISTS.getText(), repoInfo.getName());
                throw new GithubApiException(ExceptionMessage.GITHUB_API_REPOSITORY_ALREADY_EXISTS);
            }

            GHCreateRepositoryBuilder repoBuilder = gitHub.createRepository(repoInfo.getName())
                    .description(description)
                    .private_(false)  // 공개 레포지토리로 설정
                    .autoInit(true);  // 기본 README 파일 추가

            GHRepository repository = repoBuilder.create();

            addWebHook(repository, webhookUrl);

            log.info(">>>> [ {} 레포지토리가 생성되었습니다. ] <<<<", repoInfo.getName());

            return repository;
        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_CREATE_REPOSITORY_ERROR.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_CREATE_REPOSITORY_ERROR);
        }
    }

    private static void addWebHook(GHRepository repository, String webhookUrl) throws IOException {
        // 웹훅 추가
        repository.createHook(
                "web",
                Map.of(
                        "url", webhookUrl,
                        "content_type", "application/json"
                ),
                Collections.emptyList(),
                true
        );
    }

    private boolean repositoryExists(GitHub gitHub, String repoName) {
        try {
            GHRepository repository = gitHub.getRepository(repoName);
            return repository != null;
        } catch (IOException e) {
            return false;  // 레포지토리를 찾을 수 없는 경우 예외가 발생하며, 이 경우 레포지토리가 존재하지 않음을 의미
        }
    }
}
