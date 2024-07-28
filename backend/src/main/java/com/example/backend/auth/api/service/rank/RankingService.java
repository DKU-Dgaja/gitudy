package com.example.backend.auth.api.service.rank;


import com.example.backend.auth.api.service.rank.response.UserRankingResponse;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
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


    /* 백업용 로직
        Redis 데이터를 백업하거나 복구할 때나 mysql에 있는 점수데이터와 redis에 저장되는 점수가 다를경우 사용하면 될거같다.
        나중에 월마다 유저 랭킹을 초기화한다던지 그럴때 사용하면 좋을듯
    */
    public void updateUserRanking() {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        List<User> users = userRepository.findAll();
        for (User user : users) {
            zSetOps.add(USER_RANKING_KEY, user.getId(), user.getScore());
        }
    }

}
