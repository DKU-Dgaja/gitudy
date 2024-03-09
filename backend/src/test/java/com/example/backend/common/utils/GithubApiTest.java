package com.example.backend.common.utils;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.service.commit.response.GithubCommitResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("NonAsciiCharacters")
class GithubApiTest extends TestConfig {

    @Value("${github.api.token}")
    String token;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyInfoRepository studyInfoRepository;

    @Autowired
    StudyTodoRepository studyTodoRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
    }

//    테스트 돌릴때마다 실제로 생성돼서 주석 처리 해뒀습니다.

//    @Test
//    void 깃허브_API_통신_통합_테스트() throws IOException {
//        // given
//
//        User user = userRepository.save(UserFixture.generateAuthUser());
//        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
//        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));
//
//        String owner = "jusung-c";
//        String repository = "Github-Api-Test";
//
//        // 깃허브 api 통신 연결
//        GithubApi githubApi = new GithubApi();
//        GitHub gitHub = githubApi.connectGithub(token);
//
//        // 레포지토리 조회
//        GHRepository repo = githubApi.getRepository(gitHub, owner, repository);
//
//        // 투두에 대한 폴더/파일 생성
//        String path = githubApi.createTodoFolder(repo, todo);
//
//        // 생성한 폴더의 커밋 리스트 조회
//        List<GHCommit> commits = githubApi.getCommitsForFolder(repo, path);
//
//        // then
//        assertEquals("TODO(" + todo.getTitle() + ")가 생성되었습니다.", commits.get(0).getCommitShortInfo().getMessage());
//    }

//    @Test
//    void 깃허브_API_파일_삭제_테스트() throws IOException {
//        // given
//        StudyTodo todo = StudyTodo.builder()
//                .title("삭제 테스트")
//                .todoLink("삭제 테스트 링크")
//                .detail("삭제 테스트 디테일")
//                .todoDate(LocalDate.now())
//                .build();
//
//        String owner = "jusung-c";
//        String repository = "Github-Api-Test";
//
//        // 깃허브 api 통신 연결
//        GithubApi githubApi = new GithubApi();
//        GitHub gitHub = githubApi.connectGithub(token);
//
//        // 레포지토리 조회
//        GHRepository repo = githubApi.getRepository(gitHub, owner, repository);
//
//        // 투두에 대한 폴더/파일 생성
//        String path = githubApi.createTodoFolder(repo, todo);
//        String deletePath = githubApi.deleteTodoFolder(repo, todo);
//
//        // 생성한 폴더의 커밋 리스트 조회
//        List<GithubCommitResponse> commits = githubApi.getCommitsForFolder(repo, deletePath);
//
//        for (var commit : commits) {
//            System.out.println("===============");
//            System.out.println("commit.getMessage() = " + commit.getMessage());
//            System.out.println("commit.getAuthorName() = " + commit.getAuthorName());
//            System.out.println("commit.getSha() = " + commit.getSha());
//            System.out.println("commit.getCommitDate() = " + commit.getCommitDate());
//            System.out.println("===============");
//
//        }
//    }

//    @Test
//    void 깃허브_API_파일_수정_테스트() throws IOException {
//        // given
//        User user = userRepository.save(UserFixture.generateAuthUser());
//        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
//        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));
//
//        StudyTodoUpdateRequest request = StudyTodoUpdateRequest.builder()
//                .title("백준 1234번 풀기")
//                .todoLink("테스트 링크 수정")
//                .detail("테스트 디테일 수정")
//                .todoDate(LocalDate.now().plusDays(5))
//                .build();
//
//        String owner = "jusung-c";
//        String repository = "Github-Api-Test";
//
//        // 깃허브 api 통신 연결
//        GithubApi githubApi = new GithubApi();
//        GitHub gitHub = githubApi.connectGithub(token);
//
//        // 레포지토리 조회
//        GHRepository repo = githubApi.getRepository(gitHub, owner, repository);
//
//        // 생성
//        githubApi.createTodoFolder(repo, todo);
//
//        // 수정
//        githubApi.updateTodoFolder(repo, todo, request);
//    }

}