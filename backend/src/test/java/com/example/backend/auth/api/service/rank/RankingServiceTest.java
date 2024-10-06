package com.example.backend.auth.api.service.rank;

import com.example.backend.MockTestConfig;
import com.example.backend.TestConfig;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.service.info.StudyInfoService;
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

public class RankingServiceTest extends TestConfig {
    @Autowired
    private RankingService rankingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ZSetOperations<String, Object> zSetOperations;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyInfoService studyInfoService;

    private Random random = new Random();
    private static final String USER_RANKING_KEY = "user_ranking";
    private static final String STUDY_RANKING_KEY = "study_ranking";

    @BeforeEach
    void setUp() {
        //   when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        redisTemplate.delete(USER_RANKING_KEY);
        redisTemplate.delete(STUDY_RANKING_KEY);
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


 /*@Test
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
    }*/





    @Test
    void 여러_스터디_점수_저장_테스트() {

        List<Long> studyInfoIds = IntStream.range(1, 100)
                .mapToObj(i -> (long) i)
                .toList();

        // 유저 점수 저장 및 확인
        studyInfoIds.forEach(studyInfoId -> {
            int score = random.nextInt(1000);  // 0~1000 랜덤 점수
            rankingService.saveStudyScore(studyInfoId, score);
            Double storedScore = zSetOperations.score(STUDY_RANKING_KEY, studyInfoId);
            assertEquals(score, storedScore.intValue());
        });

    }

    @Test
    void 특정_스터디_점수_업데이트_테스트() {

        Long studyInfoId = 96L;

        rankingService.updateStudyScore(studyInfoId, 13);
    }

/*    @Test
    void 특정_스터디_랭킹_조회() {

        StudyInfo studyInfo1 = studyInfoRepository.save(StudyInfoFixture.createPublicStudyInfoScore(1L, 530));
        StudyInfo studyInfo2 = studyInfoRepository.save(StudyInfoFixture.createPublicStudyInfoScore(2L, 100));
        StudyInfo studyInfo3 = studyInfoRepository.save(StudyInfoFixture.createPublicStudyInfoScore(3L, 40));
        StudyInfo studyInfo4 = studyInfoRepository.save(StudyInfoFixture.createPublicStudyInfoScore(4L, 1200));
        StudyInfo studyInfo5 = studyInfoRepository.save(StudyInfoFixture.createPublicStudyInfoScore(5L, 30));
        StudyInfo studyInfo6 = studyInfoRepository.save(StudyInfoFixture.createPublicStudyInfoScore(6L, 20));

        rankingService.saveStudyScore(studyInfo1.getId(), studyInfo1.getScore());
        rankingService.saveStudyScore(studyInfo2.getId(), studyInfo2.getScore());
        rankingService.saveStudyScore(studyInfo3.getId(), studyInfo3.getScore());
        rankingService.saveStudyScore(studyInfo4.getId(), studyInfo4.getScore());

        StudyRankingResponse response1 = rankingService.getStudyRankings(studyInfo1);
        StudyRankingResponse response2 = rankingService.getStudyRankings(studyInfo2);
        // 3과4는 redis에 점수가 null로 저장되어있을때 잘 가져오는지 확인
        StudyRankingResponse response3 = rankingService.getStudyRankings(studyInfo3);
        StudyRankingResponse response4 = rankingService.getStudyRankings(studyInfo4);
        StudyRankingResponse response5 = rankingService.getStudyRankings(studyInfo3);
        StudyRankingResponse response6 = rankingService.getStudyRankings(studyInfo4);

        //then
        assertEquals(response4.getRanking(), 1);
        assertEquals(response4.getScore(), 1200);

        assertEquals(response1.getRanking(), 2);
        assertEquals(response1.getScore(), 530);

        assertEquals(response2.getRanking(), 3);
        assertEquals(response2.getScore(), 100);

        assertEquals(response3.getRanking(), 4);
        assertEquals(response3.getScore(), 40);

        assertEquals(response4.getRanking(), 5);
        assertEquals(response4.getScore(), 30);

        assertEquals(response5.getRanking(), 6);
        assertEquals(response5.getScore(), 20);
    }*/
/*


    @Test
    void 스터디_score_정렬_같은점수시_studyInfoId_순으로_재정렬_테스트() {
        //given
        User user = UserFixture.generateAuthUser();
        userRepository.save(user);
        User user2 = UserFixture.generateKaKaoUser();
        userRepository.save(user2);

        StudyInfo studyInfo1 = studyInfoRepository.save(StudyInfoFixture.createPublicStudyInfoScore(1L, 530));
        StudyInfo studyInfo2 = studyInfoRepository.save(StudyInfoFixture.createPublicStudyInfoScore(2L, 2));
        StudyInfo studyInfo3 = studyInfoRepository.save(StudyInfoFixture.createPublicStudyInfoScore(3L, 2));
        StudyInfo studyInfo4 = studyInfoRepository.save(StudyInfoFixture.createPublicStudyInfoScore(4L, 1200));
        StudyInfo studyInfo5 = studyInfoRepository.save(StudyInfoFixture.createPublicStudyInfoScore(5L, 2));
        StudyInfo studyInfo6 = studyInfoRepository.save(StudyInfoFixture.createPublicStudyInfoScore(6L, 2));



        rankingService.saveStudyScore(studyInfo1.getId(), studyInfo1.getScore());
        rankingService.saveStudyScore(studyInfo2.getId(), studyInfo2.getScore());
        rankingService.saveStudyScore(studyInfo3.getId(), studyInfo3.getScore());
        rankingService.saveStudyScore(studyInfo4.getId(), studyInfo4.getScore());
        rankingService.saveStudyScore(studyInfo5.getId(), studyInfo5.getScore());
        rankingService.saveStudyScore(studyInfo6.getId(), studyInfo6.getScore());



        // when
        StudyInfoListAndCursorIdxResponse response1 = studyInfoService.selectStudyInfoList(user2.getId()
                , null
                , 10L
                , "score"
                , false);

        StudyRankingResponse si1 = rankingService.getStudyRankings(studyInfo1);
        StudyRankingResponse si2 = rankingService.getStudyRankings(studyInfo2);
        StudyRankingResponse si3 = rankingService.getStudyRankings(studyInfo3);
        StudyRankingResponse si4 = rankingService.getStudyRankings(studyInfo4);
        StudyRankingResponse si5 = rankingService.getStudyRankings(studyInfo5);
        StudyRankingResponse si6 = rankingService.getStudyRankings(studyInfo6);


        // then
        assertEquals(response1.getStudyInfoList().get(0).getId(), studyInfo4.getId());
        assertEquals(response1.getStudyInfoList().get(1).getId(), studyInfo1.getId());
        assertEquals(response1.getStudyInfoList().get(2).getId(), studyInfo6.getId());
        assertEquals(response1.getStudyInfoList().get(3).getId(), studyInfo5.getId());
        assertEquals(response1.getStudyInfoList().get(4).getId(), studyInfo3.getId());
        assertEquals(response1.getStudyInfoList().get(5).getId(), studyInfo2.getId());


        assertEquals(si1.getRanking(), 2);
        assertEquals(si1.getScore(), 530);

        assertEquals(si2.getRanking(), 6);
        assertEquals(si2.getScore(), 2);

        assertEquals(si3.getRanking(), 5);
        assertEquals(si3.getScore(), 2);

        assertEquals(si4.getRanking(), 1);
        assertEquals(si4.getScore(), 1200);

        assertEquals(si5.getRanking(), 4);
        assertEquals(si5.getScore(), 2);

        assertEquals(si6.getRanking(), 3);
        assertEquals(si6.getScore(), 2);


        // when
        studyInfoService.deleteStudy(studyInfo6.getId());

        rankingService.updateStudyRanking(); // 레디스 캐시 초기화

        StudyRankingResponse si55 = rankingService.getStudyRankings(studyInfo5);

        // then
        assertEquals(si55.getRanking(), 3);
        assertEquals(si55.getScore(), 2);
    }*/
}
