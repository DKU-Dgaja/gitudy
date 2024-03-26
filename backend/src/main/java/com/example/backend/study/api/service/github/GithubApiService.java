package com.example.backend.study.api.service.github;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.github.GithubApiException;
import com.example.backend.study.api.service.github.response.GithubCommitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GithubApiService {
    @Value("${github.api.token}")
    private String token;

    // 깃허브 api 통신 연결
    public GitHub connectGithubApi() {
        return connectGithub(token);
    }

    // 깃허브 통신을 위한 커넥션 생성
    public GitHub connectGithub(String token) {
        try {
            GitHub github = new GitHubBuilder().withOAuthToken(token).build();
            github.checkApiUrlValidity();
            log.info(">>>> [ 깃허브 api 연결에 성공하였습니다. ] <<<<");

            return github;

        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_CONNECTION_ERROR.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_CONNECTION_ERROR);
        }
    }

    // 레포지토리 정보 가져오기
    public GHRepository getRepository(GitHub gitHub, String owner, String repository) {
        try {
            return gitHub.getRepository(owner + "/" + repository);
        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_GET_REPOSITORY_ERROR.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_GET_REPOSITORY_ERROR);
        }
    }

    // 지정한 폴더의 커밋 리스트를 불러오기
    public List<GithubCommitResponse> pullCommits(GHRepository repo, String folderPath) {
        GitHub gitHub = connectGithubApi();
        GHRepository getRepo = getRepository(gitHub, repo.getOwnerName(), repo.getName());

        // 특정 폴더의 커밋 리스트를 가져오는 로직 추가
        List<GHCommit> commits = getRepo.queryCommits().path(folderPath).list().asList();
        log.info(">>>> [ '{}' 폴더의 커밋 리스트를 성공적으로 불러왔습니다. ] <<<<", folderPath);

        return commits.stream()
                .map(commit -> {
                    try {
                        return GithubCommitResponse.of(commit);
                    } catch (IOException e) {
                        log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_GET_COMMIT_ERROR, e.getMessage());
                        throw new GithubApiException(ExceptionMessage.GITHUB_API_GET_COMMIT_ERROR);
                    }
                })
                .toList();
    }

}
