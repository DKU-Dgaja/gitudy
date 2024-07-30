package com.example.backend.study.api.service.github;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.github.GithubApiException;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

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
        try {
            GitHub gitHub = connectGithub(githubApiToken);
            GHRepository repository = getRepository(githubApiToken, repo);
            GHUser user = gitHub.getUser(githubId);
            repository.addCollaborators(GHOrganization.Permission.PUSH, user);
            log.info(">>>> [ {} 사용자를 {} 레포지토리의 Collaborator로 추가했습니다. ] <<<<", githubId, repo.getName());
        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_ADD_COLLABORATOR_ERROR.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_ADD_COLLABORATOR_ERROR);
        }
    }

    @Transactional
    public void acceptInvitation(String githubApiToken) {
        try {
            GitHub gitHub = connectGithub(githubApiToken);
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
