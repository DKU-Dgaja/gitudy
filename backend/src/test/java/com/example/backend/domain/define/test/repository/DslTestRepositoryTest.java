package com.example.backend.domain.define.test.repository;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.test.DslTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.assertj.core.api.Assertions.assertThat;

class DslTestRepositoryTest extends TestConfig {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DslTestRepository dslTestRepository;

    @AfterEach
    void tearDown() {
        dslTestRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("userId를 이용해 DslTest를 조회하고 User 정보까지 함께 가져온다.")
    void dslTest() {
        // given
        User savedUser = userRepository.save(generateAuthUser());
        DslTest savedDsl = dslTestRepository.save(DslTest.builder()
                .description("test")
                .userId(savedUser.getId()).build());

        // when
        DslTest dslTest = dslTestRepository.findDslTestByUserId(savedUser.getId()).get();

        // then
        assertThat(dslTest.getUserId()).isEqualTo(savedUser.getId());
    }
}