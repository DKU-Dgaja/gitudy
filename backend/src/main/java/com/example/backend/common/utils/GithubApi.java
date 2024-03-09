package com.example.backend.common.utils;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.github.GithubApiException;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.service.commit.response.GithubCommitResponse;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
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
    public String createTodoFolder(GHRepository repo, StudyTodo todo) {
        String folderPath = todo.getId() + ") " + todo.getTitle();
        String fileName = todo.getTitle() + ": " + todo.getTodoDate() + ".md";
        String filePath = folderPath + "/" + fileName;
        String commitMessage = "TODO(" + todo.getTitle() + ") 전용 폴더입니다.";
        String content = "## 이름: " + todo.getTitle() + "\n"
                + "### 설명: " + todo.getDetail() + "\n"
                + "### 참고 링크: " + todo.getTodoLink() + "\n"
                + "### 마감일: " + todo.getTodoDate() + "\n";

        try {
            repo.createContent()
                    .path(filePath)
                    .content(content)
                    .message(commitMessage)
                    .commit();

            log.info(">>>> [ '{}' 생성이 완료되었습니다. ] <<<<", folderPath + "/" + fileName);

            return filePath;

        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_CREATE_TODO_INFO.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_CREATE_TODO_INFO);
        }
    }

    // 레포지토리의 폴더(파일) 삭제
    public String deleteTodoFolder(GHRepository repo, StudyTodo todo) {
        String folderPath = todo.getId() + ") " + todo.getTitle();
        String fileName = todo.getTitle() + ": " + todo.getTodoDate() + ".md";
        String filePath = folderPath + "/" + fileName;
        String commitMessage = "TODO(" + todo.getTitle() + ")폴더가 삭제되었습니다.";

        try {
            repo.getFileContent(filePath)
                    .delete(commitMessage);

            log.info(">>>> [ '{}' 삭제가 완료되었습니다. ] <<<<", filePath);

            return filePath;

        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_DELETE_FILE_ERROR.getText(), e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_DELETE_FILE_ERROR);
        }
    }


    // 레포지토리 특정 폴더의 커밋 리스트 불러오기
    public List<GithubCommitResponse> getCommitsForFolder(GHRepository repo, String folderPath) {
        // 특정 폴더의 커밋 리스트를 가져오는 로직 추가
        List<GHCommit> commits = repo.queryCommits().path(folderPath).list().asList();
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

    public void updateTodoFolder(GHRepository repo, StudyTodo prevTodo, StudyTodoUpdateRequest updateTodo) {

        // prevTodo의 제목과 updateTodo의 제목이 같은 경우는 파일의 내용만 변경해주면 됨
        // 제목이 변경된 경우는 기존의 폴더의 파일들을 새로 옮겨줘야 함

        String folderPath = prevTodo.getId() + ") " + prevTodo.getTitle();

        String updateName = prevTodo.getTitle() + ": " + updateTodo.getTodoDate() + ".md";
        String deleteName = prevTodo.getTitle() + ": " + prevTodo.getTodoDate() + ".md";

        String updatePath = folderPath + "/" + updateName;
        String deletePath = folderPath + "/" + deleteName;

        String updateCommitMessage = "TODO(" + updateTodo.getTitle() + ")가 수정되었습니다.";
        String deleteCommitMessage = "TODO(" + prevTodo.getTitle() + ")가 삭제되었습니다.";

        String updateContent = "## 이름: " + updateTodo.getTitle() + "\n"
                + "### 설명: " + updateTodo.getDetail() + "\n"
                + "### 참고 링크: " + updateTodo.getTodoLink() + "\n"
                + "### 마감일: " + updateTodo.getTodoDate() + "\n";

        try {
            repo.createContent()
                    .path(updatePath)
                    .content(updateContent)
                    .message(updateCommitMessage)
                    .commit();

            repo.getFileContent(deletePath)
                    .delete(deleteCommitMessage);

            log.info(">>>> [ '{}' 수정이 완료되었습니다. ] <<<<", updatePath);

        } catch (IOException e) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_UPDATE_ERROR, e.getMessage());
            throw new GithubApiException(ExceptionMessage.GITHUB_API_UPDATE_ERROR);
        }
    }
}
