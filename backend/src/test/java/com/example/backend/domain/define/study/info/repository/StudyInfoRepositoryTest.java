package com.example.backend.domain.define.study.info.repository;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.study.api.controller.info.response.AllStudyInfoResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.Random;

import static com.example.backend.domain.define.study.info.StudyInfoFixture.createDefaultStudyInfoList;
import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("NonAsciiCharacters")
class StudyInfoRepositoryTest extends TestConfig {
    private final static int DATA_SIZE = 10;
    private final static Long LIMIT = 10L;

    @Autowired
    StudyInfoRepository studyInfoRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    //@Test
        // @Test : 테스트 어노테이션 임시 제거
        // studyInfoList 전체 테스트시 studyInfoList를 데이터 베이스에 저장할 때 strategy = GenerationType.IDENTITY로 인하여
        // 다른 테스트에서 증가한 인덱스가 그대로 적용되어 오류가 뜹니다. (단위 테스트는 성공)
    void 마이_스터디_커서_기반_페이지_조회_테스트() {

        User user = UserFixture.generateAuthUser();
        User savedUser = userRepository.save(user);
        Random random = new Random();
        Long cursorIdx = random.nextLong(LIMIT) + 1L;

        List<StudyInfo> studyInfos = createDefaultStudyInfoList(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);

        // when
        List<StudyInfoResponse> studyInfoList = studyInfoRepository.findStudyInfoListByUserId_CursorPaging(savedUser.getId(), cursorIdx, LIMIT);

        // then
        assertEquals(cursorIdx <= LIMIT ? cursorIdx - 1 : LIMIT, studyInfoList.size());
        for (StudyInfoResponse studyInfo : studyInfoList) {
            assertTrue(studyInfo.getId() < cursorIdx);
        }
    }

    @Test
    void 커서가_null일_경우_마이_스터디_페이지_조회_테스트() {
        // given
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        List<StudyInfo> studyInfos = createDefaultStudyInfoList(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);

        // when
        List<StudyInfoResponse> studyInfoList = studyInfoRepository.findStudyInfoListByUserId_CursorPaging(savedUser.getId(), null, LIMIT);
        // then
        assertEquals(LIMIT, studyInfoList.size());
    }

    @Test
    void 정렬된_모든_스터디_커서_기반_페이지_조회_테스트() {
        String sortBy = "score";
        User user = UserFixture.generateAuthUser();
        User savedUser = userRepository.save(user);
        Random random = new Random();
        Long cursorIdx = random.nextLong(LIMIT) + 1L;

        List<StudyInfo> studyInfos = createDefaultStudyInfoList(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);

        // when
        List<AllStudyInfoResponse> studyInfoList = studyInfoRepository.findStudyInfoListByParameter_CursorPaging(savedUser.getId(), cursorIdx, LIMIT, sortBy);

        // then
        int previousScore = Integer.MAX_VALUE;
        for (AllStudyInfoResponse studyInfo : studyInfoList) {
            int currentScore = studyInfo.getScore();
            assertTrue(currentScore <= previousScore);
            previousScore = currentScore;
        }
    }
}