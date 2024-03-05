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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
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

// 테스트 돌릴때마다 실제로 생성돼서 주석 처리 해뒀습니다.

//    @Test
//    void 깃허브_API_통신_통합_테스트() throws IOException {
//        // given
//        int expectedCommitCount = 1;
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
//        String path = githubApi.createFolder(repo, todo);
//
//        // 생성한 폴더의 커밋 리스트 조회
//        List<GHCommit> commits = githubApi.getCommitsForFolder(repo, path);
//
//        // then
//        assertEquals(expectedCommitCount, commits.size());
//        assertEquals("TODO(" + todo.getTitle() + ")가 생성되었습니다.", commits.get(0).getCommitShortInfo().getMessage());
//    }

}