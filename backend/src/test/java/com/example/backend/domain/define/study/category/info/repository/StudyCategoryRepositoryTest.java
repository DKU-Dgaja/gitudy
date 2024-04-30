package com.example.backend.domain.define.study.category.info.repository;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.StudyCategory.info.StudyCategoryFixture;
import com.example.backend.domain.define.study.StudyCategory.mapping.StudyCategoryMappingFixture;
import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.category.mapping.repository.StudyCategoryMappingRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.service.category.info.response.CategoryResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("NonAsciiCharacters")
public class StudyCategoryRepositoryTest extends TestConfig {
    private final static int DATA_SIZE = 20;
    private final static Long LIMIT = 10L;
    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyCategoryMappingRepository studyCategoryMappingRepository;

    @Autowired
    private StudyCategoryRepository studyCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        studyInfoRepository.deleteAllInBatch();
        studyCategoryRepository.deleteAllInBatch();
        studyCategoryMappingRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    void 카테고리_리스트_조회_쿼리_테스트() {
        // given
        Long cursorIdx = 25L;

        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));

        List<StudyCategory> categories
                = studyCategoryRepository.saveAll(StudyCategoryFixture.createDefaultPublicStudyCategories(DATA_SIZE));
        studyCategoryMappingRepository.saveAll(StudyCategoryMappingFixture.generateStudyCategoryMappings(study, categories));

        // when
        List<CategoryResponse> categoryResponses
                = studyCategoryRepository.findCategoryListByStudyInfoIdJoinCategoryMapping(study.getId(), cursorIdx, LIMIT);

        // then
        for (CategoryResponse categoryResponse : categoryResponses) {
            assertTrue(categoryResponse.getId() < cursorIdx);
            System.out.println(categoryResponse.getId() + " " + categoryResponse.getName());
        }
    }


    @Test
    void cursor가_null인_경우_카테고리_리스트_조회_쿼리_테스트() {
        // given
        Long cursorIdx = null;

        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));

        List<StudyCategory> categories
                = studyCategoryRepository.saveAll(StudyCategoryFixture.createDefaultPublicStudyCategories(DATA_SIZE));
        studyCategoryMappingRepository.saveAll(StudyCategoryMappingFixture.generateStudyCategoryMappings(study, categories));

        // when
        List<CategoryResponse> categoryResponses
                = studyCategoryRepository.findCategoryListByStudyInfoIdJoinCategoryMapping(study.getId(), cursorIdx, LIMIT);

        // then
        assertEquals(LIMIT, categoryResponses.size());
        for (CategoryResponse categoryResponse : categoryResponses) {
            System.out.println(categoryResponse.getId() + " " + categoryResponse.getName());
        }
    }
}
