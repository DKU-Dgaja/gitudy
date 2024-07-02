package com.example.backend.domain.define.study.convention.repository;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.study.convention.StudyConvention;
import com.example.backend.domain.define.study.convention.StudyConventionFixture;
import com.example.backend.study.api.controller.convention.response.StudyConventionResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("NonAsciiCharacters")
public class StudyConventionRepositoryTest extends TestConfig {

    @Autowired
    private StudyConventionRepository studyConventionRepository;
    private final static Long LIMIT = 4L;

    @AfterEach
    void tearDown() {
        studyConventionRepository.deleteAllInBatch();
    }

    @Test
    void 컨벤션_커서_기반_페이지_조회_테스트() {
        //given
        Random random = new Random();
        Long cursorIdx = Math.abs(random.nextLong()) + LIMIT;  // Limit 이상 랜덤값

        StudyConvention studyConvention1 = StudyConventionFixture.createStudyConventionName(1L, "1번째 컨벤션");
        StudyConvention studyConvention2 = StudyConventionFixture.createStudyConventionName(1L, "2번째 컨벤션");
        StudyConvention studyConvention3 = StudyConventionFixture.createStudyConventionName(1L, "3번째 컨벤션");
        StudyConvention studyConvention4 = StudyConventionFixture.createStudyConventionName(1L, "4번째 컨벤션");
        studyConventionRepository.saveAll(List.of(studyConvention1, studyConvention2, studyConvention3, studyConvention4));

        // when
        List<StudyConventionResponse> studyConventionInfoList = studyConventionRepository.findStudyConventionListByStudyInfoId_CursorPaging(1L, cursorIdx, LIMIT);

        // then
        for (StudyConventionResponse convention : studyConventionInfoList) {
            assertTrue(convention.getConventionId() < cursorIdx);
        }
    }

    @Test
    void 커서가_null일_경우_컨벤션_조회_테스트() {
        //given
        StudyConvention studyConvention1 = StudyConventionFixture.createStudyConventionName(1L, "1번째 컨벤션");
        StudyConvention studyConvention2 = StudyConventionFixture.createStudyConventionName(1L, "2번째 컨벤션");
        StudyConvention studyConvention3 = StudyConventionFixture.createStudyConventionName(1L, "3번째 컨벤션");
        StudyConvention studyConvention4 = StudyConventionFixture.createStudyConventionName(1L, "4번째 컨벤션");
        studyConventionRepository.saveAll(List.of(studyConvention1, studyConvention2, studyConvention3, studyConvention4));

        // when
        List<StudyConventionResponse> studyConventionInfoList = studyConventionRepository.findStudyConventionListByStudyInfoId_CursorPaging(1L, null, LIMIT);

        // then
        assertEquals(LIMIT, studyConventionInfoList.size());

    }

    @Test
    void 활성화된_컨벤션_조회_테스트() {
        // given
        Long studyId = 1L;

        studyConventionRepository.save(StudyConventionFixture.createStudyDefaultConvention(studyId));
        studyConventionRepository.save(StudyConventionFixture.createNonActiveConvention(studyId));

        // when
        var convention = studyConventionRepository.findActiveConventionByStudyInId(studyId);

        // then
        assertEquals(convention.getStudyInfoId(), studyId);
        assertTrue(convention.isActive());
    }

    @Test
    void 기본_컨벤션_조회() {
        // given
        studyConventionRepository.save(StudyConventionFixture.createDefaultConvention(1L));

        // when
        StudyConvention studyConvention = studyConventionRepository.findByStudyInfoIdAndContent(1L, "^[a-zA-Z0-9]{6} .*").get();

        assertEquals(studyConvention.getContent(), "^[a-zA-Z0-9]{6} .*");
    }

}
