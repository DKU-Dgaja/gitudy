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
import java.util.Set;

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

    // 지정한 레포지토리의 커밋 리스트를 불러오기
    public List<GithubCommitResponse> fetchCommits(RepositoryInfo repo, int pageSize, String todoCode) {
        GHRepository getRepo = getRepository(repo);
        List<GithubCommitResponse> filteredCommits = new ArrayList<>();
        PagedIterator<GHCommit> commitsIterator = getRepo.listCommits().withPageSize(pageSize).iterator();

        // 특정 투두의 이미 저장된 커밋 SHA 목록 조회
        Set<String> existingCommitSHAs = studyCommitRepository.findStudyCommitShaListByStudyTodoCode(todoCode);

        int pageNumber = 1;
        while (commitsIterator.hasNext()) {
            List<GHCommit> currentPageCommits = commitsIterator.nextPage();
            log.info(">>>> [ '{}'의 {} 페이지 커밋 리스트를 성공적으로 불러왔습니다. ] <<<<", repo.getName(), pageNumber);

            // 필터링된 커밋을 리스트로 저장
            for (GHCommit commit : currentPageCommits) {
                try {
                    // 투두에 해당하는 커밋인지 투두 코드로 확인
                    if (commit.getCommitShortInfo().getMessage().startsWith(todoCode)) {

                        // 커밋 SHA를 확인하여 이미 저장된 커밋인지 확인
                        if (existingCommitSHAs.contains(commit.getSHA1())) {

                            log.info(">>>> [ 이미 저장된 커밋 발견: {} ] <<<<", commit.getSHA1());
                            return filteredCommits; // 이미 저장된 커밋 발견 시 조회 중단
                        }

                        filteredCommits.add(GithubCommitResponse.of(commit));
                    }
                } catch (IOException e) {
                    log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_GET_COMMIT_ERROR, e.getMessage());
                    throw new GithubApiException(ExceptionMessage.GITHUB_API_GET_COMMIT_ERROR);
                }
            }

            pageNumber++;
        }

        return filteredCommits;
    }
}
