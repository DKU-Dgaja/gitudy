/*
package com.example.backend.auth.api.service.rank;

import com.example.backend.MockTestConfig;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RankingServiceTest extends MockTestConfig {
    @Autowired
    private RankingService rankingService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ZSetOperations<String, Object> zSetOperations;

    private Random random = new Random();
    private static final String USER_RANKING_KEY = "user_ranking";

    @BeforeEach
    void setUp() {
     //   when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
      //  redisTemplate.delete(USER_RANKING_KEY);
    }

    @Test
    void 여러_유저_점수_저장_테스트() {

        List<Long> userIds = IntStream.range(1, 100)
                .mapToObj(i -> (long) i)
                .toList();

        // 유저 점수 저장 및 확인
        userIds.forEach(userId -> {
            int score = random.nextInt(1000);  // 0~1000 랜덤 점수
            rankingService.saveUserScore(userId, score);
            Double storedScore = zSetOperations.score(USER_RANKING_KEY, userId);
            assertEquals(score, storedScore.intValue());
        });

    }


    @Test
    void 특정_유저_점수_업데이트() {
        Long userId = 70L;

        rankingService.updateUserScore(userId, 42);
    }


   */
/* @Test
    void 특정_유저_랭킹_조회() {
        User savedUser1 = userRepository.save(generateAuthUser());  // score: 10   4등
        User savedUser2 = userRepository.save(generateAdminUser()); // score: 100  1등
        User savedUser3 = userRepository.save(generateGoogleUser()); // score: 30  3등
        User savedUser4 = userRepository.save(generateKaKaoUser());  // score: 50  2등

        rankingService.saveUserScore(savedUser1.getId(), savedUser1.getScore());
        rankingService.saveUserScore(savedUser2.getId(), savedUser2.getScore());
        rankingService.saveUserScore(savedUser3.getId(), savedUser3.getScore());
        rankingService.saveUserScore(savedUser4.getId(), savedUser4.getScore());

        //then
        UserRankingResponse response1 = rankingService.getUserRankings(savedUser1);
        UserRankingResponse response2 = rankingService.getUserRankings(savedUser2);
        UserRankingResponse response3 = rankingService.getUserRankings(savedUser3);
        UserRankingResponse response4 = rankingService.getUserRankings(savedUser4);

        assertEquals(response2.getRanking(), 1);
        assertEquals(response2.getScore(), 100);

        assertEquals(response4.getRanking(), 2);
        assertEquals(response4.getScore(), 50);

        assertEquals(response3.getRanking(), 3);
        assertEquals(response3.getScore(), 30);

        assertEquals(response1.getRanking(), 4);
        assertEquals(response1.getScore(), 10);
    }*//*


}
*/