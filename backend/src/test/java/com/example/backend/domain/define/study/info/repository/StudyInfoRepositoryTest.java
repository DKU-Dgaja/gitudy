package com.example.backend.domain.define.study.info.repository;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.info.response.StudyInfoListResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        List<StudyInfoListResponse> studyInfoList = studyInfoRepository.findStudyInfoListByParameter_CursorPaging(savedUser.getId(), null, LIMIT, sortBy, true);
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
        List<StudyInfoListResponse> studyInfoPage = studyInfoRepository.findStudyInfoListByParameter_CursorPaging(savedUser.getId(), cursorIdx, LIMIT, sortBy, true);

        // then
        for (StudyInfoListResponse myStudyInfoList : studyInfoPage) {
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
        List<StudyInfoListResponse> studyInfoPage = studyInfoRepository.findStudyInfoListByParameter_CursorPaging(user.getId(), cursorIdx, LIMIT, sortBy, true);

        // then
        for (StudyInfoListResponse myStudyInfoList : studyInfoPage) {
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
        List<StudyInfoListResponse> studyInfoPage = studyInfoRepository.findStudyInfoListByParameter_CursorPaging(savedUser.getId(), cursorIdx, LIMIT, sortBy, true);

        // then
        int previousScore = Integer.MAX_VALUE;
        for (StudyInfoListResponse studyInfo : studyInfoPage) {
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
        List<StudyInfoListResponse> response = studyInfoRepository.findStudyInfoListByParameter_CursorPaging(savedUser.getId(), null, LIMIT, sortBy, true);

        assertEquals(LIMIT, response.size());
        LocalDate previousCommitDay = null;
        for (StudyInfoListResponse studyInfo : response) {
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
        List<StudyInfoListResponse> response = studyInfoRepository.findStudyInfoListByParameter_CursorPaging(savedUser.getId(), null, LIMIT, sortBy, true);

        // then
        assertEquals(LIMIT, response.size());

        LocalDateTime previousCreatedDateTime = response.get(0).getCreatedDateTime();
        for (StudyInfoListResponse studyInfo : response) {
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
        List<StudyInfoListResponse> studyInfoList = studyInfoRepository.findStudyInfoListByParameter_CursorPaging(savedUser.getId(), null, LIMIT, sortBy, false);
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
        List<StudyInfoListResponse> studyInfoPage = studyInfoRepository.findStudyInfoListByParameter_CursorPaging(savedUser.getId(), cursorIdx, LIMIT, sortBy, false);

        // then
        for (StudyInfoListResponse myStudyInfoList : studyInfoPage) {
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
        List<StudyInfoListResponse> studyInfoPage = studyInfoRepository.findStudyInfoListByParameter_CursorPaging(user.getId(), cursorIdx, LIMIT, sortBy, false);

        // then
        for (StudyInfoListResponse myStudyInfoList : studyInfoPage) {
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
        List<StudyInfoListResponse> studyInfoPage = studyInfoRepository.findStudyInfoListByParameter_CursorPaging(savedUser.getId(), cursorIdx, LIMIT, sortBy, false);

        // then
        int previousScore = Integer.MAX_VALUE;
        for (StudyInfoListResponse studyInfo : studyInfoPage) {
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
        List<StudyInfoListResponse> response = studyInfoRepository.findStudyInfoListByParameter_CursorPaging(savedUser.getId(), null, LIMIT, sortBy, false);

        assertEquals(LIMIT, response.size());
        LocalDate previousCommitDay = null;
        for (StudyInfoListResponse studyInfo : response) {
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
        List<StudyInfoListResponse> response = studyInfoRepository.findStudyInfoListByParameter_CursorPaging(savedUser.getId(), null, LIMIT, sortBy, false);

        // then
        assertEquals(LIMIT, response.size());

        LocalDateTime previousCreatedDateTime = response.get(0).getCreatedDateTime();
        for (StudyInfoListResponse studyInfo : response) {
            LocalDateTime currentCreatedDateTime = studyInfo.getCreatedDateTime();
            assertTrue(currentCreatedDateTime.compareTo(previousCreatedDateTime) <= 0);
            previousCreatedDateTime = currentCreatedDateTime;
        }
    }

    @Test
    void 레포지토리_정보를_이용해_스터디_조회_성공_테스트() {
        // given
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));

        // when
        StudyInfo findStudy = studyInfoRepository.findByRepositoryFullName(study.getRepositoryInfo().getOwner(), study.getRepositoryInfo().getName()).get();

        // then
        assertEquals(study.getId(), findStudy.getId());
    }

    @Test
    void 레포지토리_정보를_이용해_스터디_조회_실패_테스트() {
        // given
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));

        String invalidName = "invalid-name";

        // when
        Optional<StudyInfo> findStudy = studyInfoRepository.findByRepositoryFullName(study.getRepositoryInfo().getOwner(), invalidName);

        // then
        assertTrue(findStudy.isEmpty());
    }

    @Test
    void 유저_아이디에_해당하는_회원의_스터디_전부_비활성화() {
        // given
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));
        studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));
        studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));
        studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));
        studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));

        // when
        studyInfoRepository.closeStudiesOwnedByUserId(savedUser.getId());
        List<StudyInfo> allByUserId = studyInfoRepository.findAllByUserId(savedUser.getId());

        // then
        assertTrue(allByUserId.size() == 5);
        for (int i = 0; i < allByUserId.size(); i++) {
            assertTrue(allByUserId.get(i).getStatus() == StudyStatus.STUDY_INACTIVE);
        }
    }
}