package com.example.backend.study.api.service.github;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.github.GithubApiException;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHCreateRepositoryBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GithubApiService {

    @Value("${github.api.webhookURL}")
    private String webhookUrl;

    // 깃허브 통신을 위한 커넥션 생성
    public GitHub connectGithub(String githubApiToken) {
        try {
            GitHub github = new GitHubBuilder().withOAuthToken(githubApiToken).build();
            github.checkApiUrlValidity();
            log.info(">>>> [ 깃허브 api 연결에 성공하였습니다. ] <<<<");

            return github;

        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_CONNECTION_ERROR.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_CONNECTION_ERROR);
        }
    }

    // 레포지토리 정보 가져오기
    public GHRepository getRepository(String githubApiToken, RepositoryInfo studyInfo) {
        try {
            GitHub gitHub = connectGithub(githubApiToken);
            return gitHub.getRepository(studyInfo.getOwner() + "/" + studyInfo.getName());
        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_GET_REPOSITORY_ERROR.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_GET_REPOSITORY_ERROR);
        }
    }

    // 깃허브 레포지토리 생성 메서드
    @Transactional
    public GHRepository createRepository(String githubApiToken, RepositoryInfo repoInfo, String description) {
        try {
            GitHub gitHub = connectGithub(githubApiToken);

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
