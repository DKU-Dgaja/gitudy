package com.example.backend.common.utils;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.github.GithubApiException;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;

import java.io.IOException;
import java.util.List;

@Slf4j
public class GithubApi {

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

    // 레포지토리에 폴더 + Info 파일 생성
    public String createFolder(GHRepository repo, StudyTodo todo) {
        String folderPath = todo.getTitle();
        String fileName = todo.getTitle() + ": " + todo.getTodoDate() + ".md";
        String commitMessage = "TODO(" + todo.getTitle() + ")가 생성되었습니다.";
        String content = "## 이름: " + todo.getTitle() + "\n"
                + "### 설명: " + todo.getDetail() + "\n"
                + "### 참고 링크: " + todo.getTodoLink() + "\n"
                + "### 마감일: " + todo.getTodoDate() + "\n";

        try {
            repo.createContent()
                    .path(folderPath + "/" + fileName)
                    .content(content)
                    .message(commitMessage)
                    .commit();

            log.info(">>>> [ '{}' 생성이 완료되었습니다. ] <<<<", folderPath + "/" + fileName);

            return folderPath;

        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_CREATE_TODO_INFO.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_CREATE_TODO_INFO);
        }
    }

    // 레포지토리 특정 폴더의 커밋 리스트 불러오기
    public List<GHCommit> getCommitsForFolder(GHRepository repo, String folderPath) {
        // 특정 폴더의 커밋 리스트를 가져오는 로직 추가
        List<GHCommit> commits = repo.queryCommits().path(folderPath).list().asList();
        log.info(">>>> [ '{}' 폴더의 커밋 리스트를 성공적으로 불러왔습니다. ] <<<<", folderPath);

        return commits;
    }


        // 레포지토리에 이슈 생성
    public GHIssue createIssue(GHRepository repo, String title, String description) {
        try {
            return repo.createIssue(title)
                    .body(description)
                    .create();
        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_CREATE_ISSUE_ERROR.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_CREATE_ISSUE_ERROR);
        }
    }

}
