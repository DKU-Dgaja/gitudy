package com.example.backend.domain.define.user.repository;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends TestConfig {
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("platformId와 platformType을 이용해 해당 User를 조회할 수 있다.")
    void findByPlatformIdAndPlatformTypeTest() {
        // given
        User savedUser = userRepository.save(generateUser());
        String subject = savedUser.getUsername();

        // when
        User findUser = userRepository.findByPlatformIdAndPlatformType(savedUser.getPlatformId(), savedUser.getPlatformType()).get();

        // then
        assertThat(findUser).isNotNull();
        assertThat(subject).isEqualTo(findUser.getUsername());

    }

}