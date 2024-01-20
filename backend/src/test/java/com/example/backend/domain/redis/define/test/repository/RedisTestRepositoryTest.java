package com.example.backend.domain.redis.define.test.repository;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.test.RedisTest;
import com.example.backend.domain.define.test.repository.RedisTestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;


class RedisTestRepositoryTest extends TestConfig {
    @Autowired
    private RedisTestRepository redisTestRepository;

    @AfterEach
    void tearDown() {
//        redisTestRepository.deleteAll();
    }

    @Test
    @DisplayName("Redis 저장 & 조회 테스트")
    void redisSaveTest() {
        // given
        RedisTest savedEntity = redisTestRepository.save(RedisTest.builder()
                .description("테스트 입니다.")
                .build());

        // when
        RedisTest findEntity = redisTestRepository.findById(savedEntity.getId()).get();

        // then
        assertThat(findEntity).isNotNull();
        assertThat(findEntity.getDescription()).isEqualTo("테스트 입니다.");
    }
}