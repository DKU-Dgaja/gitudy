package com.example.backend.study.api.service.github;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.github.GithubApiException;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.study.api.service.github.response.GithubCommitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
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
    public GHRepository getRepository(RepositoryInfo studyInfo) {
        try {
            GitHub gitHub = connectGithub(token);
            return gitHub.getRepository(studyInfo.getOwner() + "/" + studyInfo.getName());
        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_GET_REPOSITORY_ERROR.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_GET_REPOSITORY_ERROR);
        }
    }

    // 지정한 레포지토리의 커밋 리스트를 불러오기
    public List<GithubCommitResponse> fetchCommits(RepositoryInfo repo, int pageNumber, int pageSize, String todoCode) {
        GHRepository getRepo = getRepository(repo);
        List<GHCommit> filteredCommits = new ArrayList<>();

        // 페이지네이션 기준 pageSize 지정
        PagedIterator<GHCommit> commitsIterator = getRepo.listCommits().withPageSize(pageSize).iterator();

        // 조회를 희망하는 페이지로 이동
        for (int i = 0; i < pageNumber - 1; i++) {
            if (commitsIterator.hasNext()) {
                commitsIterator.nextPage();
            } else {
                throw new IllegalArgumentException("Requested page does not exist");
            }
        }

        // 현재 페이지의 데이터만 가져온다.
        List<GHCommit> currentPageCommits = commitsIterator.nextPage();

        // 필터링된 커밋을 리스트로 저장
        for (GHCommit commit : currentPageCommits) {
            try {
                if (commit.getCommitShortInfo().getMessage().startsWith(todoCode)) {
                    filteredCommits.add(commit);
                }
            } catch (IOException e) {
                log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_GET_COMMIT_ERROR, e.getMessage());
                throw new GithubApiException(ExceptionMessage.GITHUB_API_GET_COMMIT_ERROR);
            }
        }

        log.info(">>>> [ '{}'의 {} 페이지 커밋 리스트를 성공적으로 불러왔습니다. ] <<<<", repo.getName(), pageNumber);

        // 가져온 커밋들을 GithubCommitResponse로 변환하여 반환
        return filteredCommits.stream()
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
