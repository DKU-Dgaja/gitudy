package com.example.backend.study.api.service.github;

import com.example.backend.common.utils.GithubApi;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.service.commit.response.GithubCommitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GithubApiService {
    private final GithubApi githubApi;

    @Value("${github.api.token}")
    private String token;

    // 깃허브 api 통신 연결
    public GitHub connectGithubApi() {
        return githubApi.connectGithub(token);
    }

    // 스터디 레포지토리에 폴더(파일) 생성
    public void createTodoFolder(RepositoryInfo repo, StudyTodo todo) {
        GitHub gitHub = connectGithubApi();
        GHRepository getRepo = githubApi.getRepository(gitHub, repo.getOwner(), repo.getName());

        githubApi.createTodoFolder(getRepo, todo);
    }

    // 레포지토리의 폴더(파일) 삭제
    public void deleteTodoFolder(RepositoryInfo repo, StudyTodo todo) {
        GitHub gitHub = connectGithubApi();
        GHRepository getRepo = githubApi.getRepository(gitHub, repo.getOwner(), repo.getName());

        githubApi.deleteTodoFolder(getRepo, todo);
    }

    // 레포지토리의 폴더(파일) 수정
    public void updateTodoFolder(RepositoryInfo repo, StudyTodo prevTodo, StudyTodoUpdateRequest updateTodo) {
        GitHub gitHub = connectGithubApi();
        GHRepository getRepo = githubApi.getRepository(gitHub, repo.getOwner(), repo.getName());

        githubApi.updateTodoFolder(getRepo, prevTodo, updateTodo);
    }

    // 지정한 폴더의 커밋 리스트를 불러오기
    public List<GithubCommitResponse> pullCommits(RepositoryInfo repo, StudyTodo todo) {
        GitHub gitHub = connectGithubApi();
        GHRepository getRepo = githubApi.getRepository(gitHub, repo.getOwner(), repo.getName());

        return githubApi.getCommitsForFolder(getRepo, todo.getTitle());
    }

}
