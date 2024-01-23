package com.example.backend.domain.define.user.repository;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserPlatformType;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static com.example.backend.domain.define.account.user.constant.UserPlatformType.GITHUB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    @DisplayName("플랫폼 ID와 플랫폼이 같으면 데이터 정합성이 깨져 에러가 발생한다.")
    void constraintExceptionTest() {
        // given
        User A = userRepository.save(generateUser());
        String A_Id = A.getPlatformId();
        UserPlatformType A_Type = A.getPlatformType();

        // when & then
        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.save(User.builder()
                        .platformId(A_Id)
                        .platformType(A_Type)
                        .build()));
    }

    @Test
    @DisplayName("플랫폼 ID가 같더라도 플랫폼이 다르면 데이터 정합성이 깨지지 않는다.")
    void constraintTest() {
        // given
        User A = userRepository.save(generateUser());
        String A_Id = A.getPlatformId();
        UserPlatformType A_Type = A.getPlatformType();

        User B = userRepository.save(User.builder()
                .platformId(A_Id)
                .platformType(GITHUB)
                .build());
        String B_Id = B.getPlatformId();
        UserPlatformType B_Type = B.getPlatformType();

        // when
        User findA = userRepository.findByPlatformIdAndPlatformType(A_Id, A_Type).get();
        User findB = userRepository.findByPlatformIdAndPlatformType(B_Id, B_Type).get();

        // then
        assertThat(findA).isNotEqualTo(findB);
        assertThat(findA.getPlatformId()).isEqualTo(findB.getPlatformId());
        assertThat(findA.getPlatformType()).isNotEqualTo(findB.getPlatformType());
        assertThat(findA.getId()).isNotEqualTo(findB.getId());
    }

}