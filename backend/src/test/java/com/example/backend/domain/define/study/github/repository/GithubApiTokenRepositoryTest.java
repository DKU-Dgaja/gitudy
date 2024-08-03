package com.example.backend.domain.define.study.github.repository;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.github.GithubApiToken;
import com.example.backend.domain.define.study.github.GithubApiTokenFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GithubApiTokenRepositoryTest extends TestConfig {
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
    void 깃허브토큰_저장_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        String token = "userTestToken";

        githubApiTokenRepository.save(GithubApiTokenFixture.createToken(token, user.getId()));

        // when
        GithubApiToken savedToken = githubApiTokenRepository.findById(token).get();

        // then
        assertThat(user.getId()).isEqualTo(savedToken.userId());
        assertThat(token).isEqualTo(savedToken.githubApiToken());
    }

    @Test
    void 사용자_아이디로_깃허브토큰_조회_테스트() {
        // given
        String userAToken = "A";
        User userA = userRepository.save(UserFixture.generateAuthUserByPlatformId(userAToken));

        String userBToken = "B";
        User userB = userRepository.save(UserFixture.generateAuthUserByPlatformId(userBToken));

        githubApiTokenRepository.save(GithubApiTokenFixture.createToken(userAToken, userA.getId()));
        githubApiTokenRepository.save(GithubApiTokenFixture.createToken(userBToken, userB.getId()));

        // when
        var findToken = githubApiTokenRepository.findByUserId(userA.getId()).get();

        // then
        assertEquals(userA.getId(), findToken.userId());
        assertEquals(userAToken, findToken.githubApiToken());
    }

    @Test
    void 사용자_아이디로_깃허브토큰이_존재하는지_테스트() {
        // given
        User userA = userRepository.save(UserFixture.generateGoogleUser());
        String userAToken = "A";

        User userB = userRepository.save(UserFixture.generateKaKaoUser());
        String userBToken = "B";

        Long invalidId = Long.MAX_VALUE;

        githubApiTokenRepository.save(GithubApiTokenFixture.createToken(userAToken, userA.getId()));
        githubApiTokenRepository.save(GithubApiTokenFixture.createToken(userBToken, userB.getId()));

        // when & then
        assertTrue(githubApiTokenRepository.existsByUserId(userA.getId()));
        assertTrue(githubApiTokenRepository.existsByUserId(userB.getId()));
        assertFalse(githubApiTokenRepository.existsByUserId(invalidId));
    }

}