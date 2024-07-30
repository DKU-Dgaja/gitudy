package com.example.backend.auth.api.service.rank;


import com.example.backend.auth.api.service.rank.response.UserRankingResponse;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    public static final String USER_RANKING_KEY = "user_ranking";

    @PostConstruct
    public void init() {
        updateUserRanking();
    }


    // 사용자 점수를 Redis Sorted Set에 저장
    public void saveUserScore(Long userId, int score) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        zSetOps.add(USER_RANKING_KEY, userId, score);
    }

    // 사용자 점수 업데이트
    public void updateUserScore(Long userId, int score) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        zSetOps.incrementScore(USER_RANKING_KEY, userId, score);
    }

    // 특정 사용자의 랭킹 조회
    public UserRankingResponse getUserRankings(User user) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        Double score = zSetOps.score(USER_RANKING_KEY, user.getId());
        if (score == null) {
            score = (double) user.getScore();
            saveUserScore(user.getId(), score.intValue());
        }
        Long ranking = zSetOps.reverseRank(USER_RANKING_KEY, user.getId());
        if (ranking == null) {
            return new UserRankingResponse(score.intValue(), 0L); // 랭킹이 없으면 0 반환
        }
        return new UserRankingResponse(score.intValue(), ranking + 1);
    }

    // 어플리케이션 로드 시점에 Cache Warming 작업
    public void updateUserRanking() {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        List<User> users = userRepository.findAll();
        for (User user : users) {
            zSetOps.add(USER_RANKING_KEY, user.getId(), user.getScore());
        }
    }

}
