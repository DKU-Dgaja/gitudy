package com.example.backend.domain.define.study.github.repository;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.github.GithubApiToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("github api token을 저장할 수 있다.")
    void saveGithubApiToken() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        GithubApiToken githubApiToken = githubApiTokenRepository.save(GithubApiToken.builder()
                .userId(user.getId())
                .githubApiToken("token")
                .build());

        // when
        GithubApiToken savedGithubApiToken = githubApiTokenRepository.findById(githubApiToken.getUserId()).get();

        // then
        assertThat(githubApiToken.getUserId()).isEqualTo(savedGithubApiToken.getUserId());
        assertThat(githubApiToken.getGithubApiToken()).isEqualTo(savedGithubApiToken.getGithubApiToken());
    }
}