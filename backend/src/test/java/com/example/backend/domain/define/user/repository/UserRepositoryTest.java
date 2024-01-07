package com.example.backend.domain.define.user.repository;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest extends TestConfig {
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("email 정보를 이용해 해당 User를 조회할 수 있다.")
    void findByEmailSuccess() {
        // given
        User savedUser = userRepository.save(generateUser());

        // when
        String email = userRepository.findByEmail(savedUser.getEmail()).get().getEmail();

        // then
        assertThat(savedUser.getEmail()).isEqualTo(email);

    }

}