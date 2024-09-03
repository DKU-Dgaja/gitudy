package com.example.backend.auth.api.service.rank;


import com.example.backend.auth.api.service.rank.response.StudyRankingResponse;
import com.example.backend.auth.api.service.rank.response.UserRankingResponse;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.constant.UserRole;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final StudyInfoRepository studyInfoRepository;
    private static final String USER_RANKING_KEY = "user_ranking";
    private static final String STUDY_RANKING_KEY = "study_ranking";

    @PostConstruct
    public void init() {
        updateUserRanking();
        updateStudyRanking();
    }


    // 사용자 점수를 처음으로 Redis Sorted Set에 저장
    public void saveUserScore(Long userId, double score) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        zSetOps.add(USER_RANKING_KEY, userId, score);
    }

    // 사용자 점수 증/감 업데이트
    public void updateUserScore(Long userId, double score) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        zSetOps.incrementScore(USER_RANKING_KEY, userId, score);
    }

    // 특정 사용자의 랭킹 조회
    public UserRankingResponse getUserRankings(User user) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        Double score = zSetOps.score(USER_RANKING_KEY, user.getId());
        if (score == null) {
            score = (double) user.getScore();
            saveUserScore(user.getId(), score);
        }
        Long ranking = zSetOps.reverseRank(USER_RANKING_KEY, user.getId());
        if (ranking == null) {
            return new UserRankingResponse(score, 0L); // 랭킹이 없으면 0 반환
        }
        return UserRankingResponse.builder()
                .ranking(ranking + 1)
                .score(score)
                .build();
    }

    // 유저 점수 삭제
    public void deleteUserScore(Long userId) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        zSetOps.remove(USER_RANKING_KEY, userId);
    }

    // 어플리케이션 로드 시점에 Cache Warming 작업
    public void updateUserRanking() {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        // USER 역할인 사용자만 필터링하여 랭킹에 추가
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getRole() == UserRole.USER) {
                zSetOps.add(USER_RANKING_KEY, user.getId(), user.getScore());
            }
        }
    }

    // 마찬가지로 로드 시점에 Cache Warming
    public void updateStudyRanking() {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        List<StudyInfo> studyInfos = studyInfoRepository.findAll();

        // STUDY_DELETED 상태를 가진 스터디 제외
        List<StudyInfo> filteredStudyInfos = studyInfos.stream()
                .filter(studyInfo -> !StudyStatus.STUDY_DELETED.equals(studyInfo.getStatus()))
                .toList();

        // 필터링된 스터디 정보를 Redis에 업데이트
        for (StudyInfo studyInfo : filteredStudyInfos) {
            zSetOps.add(STUDY_RANKING_KEY, studyInfo.getId(), studyInfo.getScore());
        }
    }

    // 스터디 점수 증/감 업데이트
    public void updateStudyScore(Long studyInfoId, double score) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        zSetOps.incrementScore(STUDY_RANKING_KEY, studyInfoId, score);
    }

    // 스터디 점수를 처음으로 Redis Sorted Set에 저장
    public void saveStudyScore(Long studyInfoId, double score) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        zSetOps.add(STUDY_RANKING_KEY, studyInfoId, score);
    }

    // 특정 스터디의 랭킹 조회
    public StudyRankingResponse getStudyRankings(StudyInfo studyInfo) {

        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        Double score = zSetOps.score(STUDY_RANKING_KEY, studyInfo.getId());
        if (score == null) {
            score = (double) studyInfo.getScore();
            saveStudyScore(studyInfo.getId(), score);
        }

        Long ranking = zSetOps.reverseRank(STUDY_RANKING_KEY, studyInfo.getId());
        if (ranking == null) {
            return new StudyRankingResponse(score, 0L);
        }
        return StudyRankingResponse.builder()
                .ranking(ranking + 1)
                .score(score)
                .build();
    }

    // 스터디 점수 삭제
    public void deleteStudyScore(Long studyInfoId) {
        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        zSetOps.remove(STUDY_RANKING_KEY, studyInfoId);
    }
}
