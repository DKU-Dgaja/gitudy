package com.example.backend.study.api.service.github;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.github.GithubApiException;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
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

    private final StudyCommitRepository studyCommitRepository;

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
    public GHRepository getRepository(RepositoryInfo studyInfo) {
        try {
            GitHub gitHub = connectGithub(token);
            return gitHub.getRepository(studyInfo.getOwner() + "/" + studyInfo.getName());
        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_GET_REPOSITORY_ERROR.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_GET_REPOSITORY_ERROR);
        }
    }

    // 지정한 폴더의 커밋 리스트를 불러오기
    public List<GithubCommitResponse> pullUnsavedCommits(RepositoryInfo repo, StudyTodo todo) {
        GHRepository getRepo = getRepository(repo);
        String todoPath = todo.getTitle();

        // 특정 폴더의 커밋 리스트를 가져오는 로직 추가
        List<GHCommit> commits = null;
        try {
            commits = getRepo.queryCommits().path(todoPath).list().toList();

            log.info(">>>> [ '{}' 폴더의 커밋 리스트를 성공적으로 불러왔습니다. ] <<<<", todoPath);

            // GithubCommitResponse 리스트로 변환
            List<GithubCommitResponse> commitList = commits.stream()
                    .map(commit -> {
                        try {
                            return GithubCommitResponse.of(commit);
                        } catch (IOException e) {
                            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_GET_COMMIT_ERROR, e.getMessage());
                            throw new GithubApiException(ExceptionMessage.GITHUB_API_GET_COMMIT_ERROR);
                        }
                    })
                    .toList();

            // 저장되지 않은 커밋의 경우만 필터링
            return studyCommitRepository.findUnsavedGithubCommits(commitList);

        } catch (IOException e) {
            throw new GithubApiException(ExceptionMessage.GITHUB_API_GET_COMMITS_ERROR);
        }

    }

}
