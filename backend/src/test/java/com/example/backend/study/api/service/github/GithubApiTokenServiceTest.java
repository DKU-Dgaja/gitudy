package com.example.backend.study.api.service.github;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.github.GithubApiTokenException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.github.repository.GithubApiTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class GithubApiTokenServiceTest extends TestConfig {
    @Autowired
    private GithubApiTokenService githubApiTokenService;

    @Autowired
    private GithubApiTokenRepository githubApiTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        githubApiTokenRepository.deleteAll();
        userRepository.deleteAllInBatch();
    }

    @Test
    void 깃허브토큰_조회_실패_테스트() {
        User user = userRepository.save(UserFixture.generateGoogleUser());

        var e = assertThrows(GithubApiTokenException.class, () -> {
            githubApiTokenService.getToken(user.getId());
        });

        assertEquals(ExceptionMessage.GITHUB_API_TOKEN_NOT_EXIST.getText(), e.getMessage());
    }

    @Test
    void 토큰_삭제_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateGoogleUser());
        String userToken = "token";

        githubApiTokenService.saveToken(userToken, user.getId());

        // when
        githubApiTokenService.deleteToken(user.getId());

        // then
        assertFalse(githubApiTokenRepository.findByUserId(user.getId()).isPresent());
    }

    @Test
    void 기존_토큰이_없는_사용자의_토큰을_저장한다() {
        // given
        User user = userRepository.save(UserFixture.generateGoogleUser());
        String userToken = "token";

        // when
        githubApiTokenService.saveToken(userToken, user.getId());
        var findToken = githubApiTokenRepository.findById(userToken).get();

        // then

        assertEquals(userToken, findToken.githubApiToken());
        assertEquals(user.getId(), findToken.userId());
    }

    @Test
    void 기존_토큰이_있는_사용자의_토큰을_저장할_경우_기존_토큰을_삭제한_후_저장한다() {
        // given
        User user = userRepository.save(UserFixture.generateGoogleUser());
        String oldToken = "token";
        String newToken = "newToken";

        // when
        githubApiTokenService.saveToken(oldToken, user.getId());
        githubApiTokenService.saveToken(newToken, user.getId());

        var findToken = githubApiTokenRepository.findByUserId(user.getId()).get();

        // then
        assertFalse(githubApiTokenRepository.findById(oldToken).isPresent());
        assertEquals(newToken, findToken.githubApiToken());
        assertEquals(user.getId(), findToken.userId());
    }

}
