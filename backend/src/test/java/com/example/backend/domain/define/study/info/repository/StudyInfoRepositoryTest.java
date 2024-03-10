package com.example.backend.domain.define.study.info.repository;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.info.response.MyStudyInfoListAndCursorIdxResponse;
import com.example.backend.study.api.controller.info.response.MyStudyInfoListResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static com.example.backend.domain.define.study.info.StudyInfoFixture.createDefaultStudyInfoList;
import static com.example.backend.domain.define.study.info.StudyInfoFixture.createDefaultStudyInfoListRandomScoreAndLastCommitDay;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudyInfoRepositoryTest extends TestConfig {
    private final static int DATA_SIZE = 10;
    private final static Long LIMIT = 10L;
    private final static String sortBy = "score";
    @Autowired
    StudyInfoRepository studyInfoRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyMemberRepository studyMemberRepository;

    @AfterEach
    void tearDown() {
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
    }

    @Test
    void 커서가_null일_경우_마이_스터디_페이지_조회_테스트() {
        // given
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        List<StudyInfo> studyInfos = createDefaultStudyInfoList(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos));


        // when
        List<MyStudyInfoListResponse> studyInfoList = studyInfoRepository.findMyStudyInfoListByParameter_CursorPaging(savedUser.getId(), null, LIMIT, sortBy, true);
        // then
        assertEquals(LIMIT, studyInfoList.size());
    }

    @Test
    void 커서가_null이_아닌_경우_마이_스터디_조회_테스트_1() {
        // given
        User user = UserFixture.generateAuthUser();

        User savedUser = userRepository.save(user);

        Random random = new Random();
        Long cursorIdx = random.nextLong(LIMIT) + 1L;

        List<StudyInfo> studyInfos = createDefaultStudyInfoList(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos));
        // when
        List<MyStudyInfoListResponse> studyInfoPage = studyInfoRepository.findMyStudyInfoListByParameter_CursorPaging(savedUser.getId(), cursorIdx, LIMIT, sortBy, true);

        // then
        for (MyStudyInfoListResponse myStudyInfoList : studyInfoPage) {
            assertTrue(myStudyInfoList.getId() < cursorIdx);
        }
    }

    @Test
    void 커서가_null이_아닌_경우_마이_스터디_조회_테스트_2() {
        // given
        Long cursorIdx = 15L;

        User user = userRepository.save(UserFixture.generateAuthUser());

        List<StudyInfo> studyInfos1 = createDefaultStudyInfoList(DATA_SIZE, user.getId());
        List<StudyInfo> studyInfos2 = createDefaultStudyInfoList(DATA_SIZE, user.getId());
        studyInfoRepository.saveAll(studyInfos1);
        studyInfoRepository.saveAll(studyInfos2);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos1));
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos2));
        // when
        List<MyStudyInfoListResponse> studyInfoPage = studyInfoRepository.findMyStudyInfoListByParameter_CursorPaging(user.getId(), cursorIdx, LIMIT, sortBy, true);

        // then
        for (MyStudyInfoListResponse myStudyInfoList : studyInfoPage) {
            assertTrue(myStudyInfoList.getId() < cursorIdx);
        }
    }

    @Test
    void score_기준으로_정렬된_마이_스터디_커서_기반_페이지_조회_테스트() {
        String sortBy = "score";
        User user = UserFixture.generateAuthUser();
        User savedUser = userRepository.save(user);
        Random random = new Random();
        Long cursorIdx = random.nextLong(LIMIT) + 1L;

        List<StudyInfo> studyInfos = StudyInfoFixture.createDefaultcreateDefaultStudyInfoRandomScoreAndLastCommitDayList(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos));
        // when
        List<MyStudyInfoListResponse> studyInfoPage = studyInfoRepository.findMyStudyInfoListByParameter_CursorPaging(savedUser.getId(), cursorIdx, LIMIT, sortBy, true);

        // then
        int previousScore = Integer.MAX_VALUE;
        for (MyStudyInfoListResponse studyInfo : studyInfoPage) {
            int currentScore = studyInfo.getScore();
            assertTrue(currentScore <= previousScore);
            previousScore = currentScore;
        }
    }
    @Test
    void lastCommitDay_기준으로_정렬된_마이_스터디_커서_기반_페이지_조회_테스트() {
        // given
        String sortBy = "lastCommitDay";
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        List<StudyInfo> studyInfos = createDefaultStudyInfoListRandomScoreAndLastCommitDay(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos));

        // when
        List<MyStudyInfoListResponse> response = studyInfoRepository.findMyStudyInfoListByParameter_CursorPaging(savedUser.getId(), null, LIMIT, sortBy, true);

        assertEquals(LIMIT, response.size());
        LocalDate previousCommitDay = null;
        for (MyStudyInfoListResponse studyInfo : response) {
            LocalDate currentCommitDay = studyInfo.getLastCommitDay();
            if (previousCommitDay != null) {
                assertTrue(currentCommitDay.isBefore(previousCommitDay) || currentCommitDay.isEqual(previousCommitDay));
            }
            previousCommitDay = currentCommitDay;
        }
    }
    @Test
    void createdDateTime_기준으로_정렬된_마이_스터디_커서_기반_페이지_조회_테스트() {
        // given
        String sortBy = "createdDateTime";
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        List<StudyInfo> studyInfos = createDefaultStudyInfoListRandomScoreAndLastCommitDay(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos));

        // when
        List<MyStudyInfoListResponse> response = studyInfoRepository.findMyStudyInfoListByParameter_CursorPaging(savedUser.getId(), null, LIMIT, sortBy, true);

        // then
        assertEquals(LIMIT, response.size());

        LocalDateTime previousCreatedDateTime = response.get(0).getCreatedDateTime();
        for (MyStudyInfoListResponse studyInfo : response) {
            LocalDateTime currentCreatedDateTime = studyInfo.getCreatedDateTime();
            assertTrue(currentCreatedDateTime.compareTo(previousCreatedDateTime) <= 0);
            previousCreatedDateTime = currentCreatedDateTime;
        }
    }

    @Test
    void 커서가_null일_경우_전체_스터디_페이지_조회_테스트() {
        // given
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        List<StudyInfo> studyInfos = createDefaultStudyInfoList(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos));


        // when
        List<MyStudyInfoListResponse> studyInfoList = studyInfoRepository.findMyStudyInfoListByParameter_CursorPaging(savedUser.getId(), null, LIMIT, sortBy, false);
        // then
        assertEquals(LIMIT, studyInfoList.size());
    }

    @Test
    void 커서가_null이_아닌_경우_전체_스터디_조회_테스트_1() {
        // given
        User user = UserFixture.generateAuthUser();

        User savedUser = userRepository.save(user);

        Random random = new Random();
        Long cursorIdx = random.nextLong(LIMIT) + 1L;

        List<StudyInfo> studyInfos = createDefaultStudyInfoList(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos));
        // when
        List<MyStudyInfoListResponse> studyInfoPage = studyInfoRepository.findMyStudyInfoListByParameter_CursorPaging(savedUser.getId(), cursorIdx, LIMIT, sortBy, false);

        // then
        for (MyStudyInfoListResponse myStudyInfoList : studyInfoPage) {
            assertTrue(myStudyInfoList.getId() < cursorIdx);
        }
    }

    @Test
    void 커서가_null이_아닌_경우_전체_스터디_조회_테스트_2() {
        // given
        Long cursorIdx = 15L;

        User user = userRepository.save(UserFixture.generateAuthUser());

        List<StudyInfo> studyInfos1 = createDefaultStudyInfoList(DATA_SIZE, user.getId());
        List<StudyInfo> studyInfos2 = createDefaultStudyInfoList(DATA_SIZE, user.getId());
        studyInfoRepository.saveAll(studyInfos1);
        studyInfoRepository.saveAll(studyInfos2);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos1));
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos2));
        // when
        List<MyStudyInfoListResponse> studyInfoPage = studyInfoRepository.findMyStudyInfoListByParameter_CursorPaging(user.getId(), cursorIdx, LIMIT, sortBy, false);

        // then
        for (MyStudyInfoListResponse myStudyInfoList : studyInfoPage) {
            assertTrue(myStudyInfoList.getId() < cursorIdx);
        }
    }

    @Test
    void score_기준으로_정렬된_전체_스터디_커서_기반_페이지_조회_테스트() {
        String sortBy = "score";
        User user = UserFixture.generateAuthUser();
        User savedUser = userRepository.save(user);
        Random random = new Random();
        Long cursorIdx = random.nextLong(LIMIT) + 1L;

        List<StudyInfo> studyInfos = StudyInfoFixture.createDefaultcreateDefaultStudyInfoRandomScoreAndLastCommitDayList(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos));
        // when
        List<MyStudyInfoListResponse> studyInfoPage = studyInfoRepository.findMyStudyInfoListByParameter_CursorPaging(savedUser.getId(), cursorIdx, LIMIT, sortBy, false);

        // then
        int previousScore = Integer.MAX_VALUE;
        for (MyStudyInfoListResponse studyInfo : studyInfoPage) {
            int currentScore = studyInfo.getScore();
            assertTrue(currentScore <= previousScore);
            previousScore = currentScore;
        }
    }
    @Test
    void lastCommitDay_기준으로_정렬된_전체_스터디_커서_기반_페이지_조회_테스트() {
        // given
        String sortBy = "lastCommitDay";
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        List<StudyInfo> studyInfos = createDefaultStudyInfoListRandomScoreAndLastCommitDay(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos));

        // when
        List<MyStudyInfoListResponse> response = studyInfoRepository.findMyStudyInfoListByParameter_CursorPaging(savedUser.getId(), null, LIMIT, sortBy, false);

        assertEquals(LIMIT, response.size());
        LocalDate previousCommitDay = null;
        for (MyStudyInfoListResponse studyInfo : response) {
            LocalDate currentCommitDay = studyInfo.getLastCommitDay();
            if (previousCommitDay != null) {
                assertTrue(currentCommitDay.isBefore(previousCommitDay) || currentCommitDay.isEqual(previousCommitDay));
            }
            previousCommitDay = currentCommitDay;
        }
    }
    @Test
    void createdDateTime_기준으로_정렬된_전체_스터디_커서_기반_페이지_조회_테스트() {
        // given
        String sortBy = "createdDateTime";
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        List<StudyInfo> studyInfos = createDefaultStudyInfoListRandomScoreAndLastCommitDay(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos));

        // when
        List<MyStudyInfoListResponse> response = studyInfoRepository.findMyStudyInfoListByParameter_CursorPaging(savedUser.getId(), null, LIMIT, sortBy, false);

        // then
        assertEquals(LIMIT, response.size());

        LocalDateTime previousCreatedDateTime = response.get(0).getCreatedDateTime();
        for (MyStudyInfoListResponse studyInfo : response) {
            LocalDateTime currentCreatedDateTime = studyInfo.getCreatedDateTime();
            assertTrue(currentCreatedDateTime.compareTo(previousCreatedDateTime) <= 0);
            previousCreatedDateTime = currentCreatedDateTime;
        }
    }
}