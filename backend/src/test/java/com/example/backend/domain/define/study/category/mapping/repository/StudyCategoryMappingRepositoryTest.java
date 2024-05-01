package com.example.backend.domain.define.study.category.mapping.repository;

import com.example.backend.TestConfig;
import com.example.backend.domain.define.study.StudyCategory.info.StudyCategoryFixture;
import com.example.backend.domain.define.study.StudyCategory.mapping.StudyCategoryMappingFixture;
import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.category.info.repository.StudyCategoryRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.controller.info.response.CategoryResponseWithStudyId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("NonAsciiCharacters")
class StudyCategoryMappingRepositoryTest extends TestConfig {
    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyCategoryMappingRepository studyCategoryMappingRepository;

    @Autowired
    private StudyCategoryRepository studyCategoryRepository;

    @AfterEach
    void tearDown() {
        studyInfoRepository.deleteAllInBatch();
        studyCategoryRepository.deleteAllInBatch();
        studyCategoryMappingRepository.deleteAllInBatch();
    }

    @Test
    void studyInfoIdList를_통해_스터디들의_모든_카테고리를_조회() {
        Long user = 1L;

        // 스터디 카테고리 생성
        StudyCategory studyCategory1 = studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("studyCategory1"));
        StudyCategory studyCategory2 = studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("studyCategory2"));
        StudyCategory studyCategory3 = studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("studyCategory3"));
        StudyCategory studyCategory4 = studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("studyCategory4"));

        List<StudyCategory> categoriesOfStudy1 = new ArrayList<>();
        categoriesOfStudy1.add(studyCategory1);
        categoriesOfStudy1.add(studyCategory2);

        List<StudyCategory> categoriesOfStudy2 = new ArrayList<>();
        categoriesOfStudy2.add(studyCategory3);
        categoriesOfStudy2.add(studyCategory4);

        // Study 생성
        List<Long> studyInfoIds = new ArrayList<>();
        StudyInfo studyInfo1 = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user));
        studyInfoIds.add(studyInfo1.getId());
        StudyInfo studyInfo2 = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user));
        studyInfoIds.add(studyInfo2.getId());

        // 스터디 카테고리 매핑 생성
        studyCategoryMappingRepository.saveAll(StudyCategoryMappingFixture.generateStudyCategoryMappings(studyInfo1, categoriesOfStudy1));
        studyCategoryMappingRepository.saveAll(StudyCategoryMappingFixture.generateStudyCategoryMappings(studyInfo2, categoriesOfStudy2));

        List<CategoryResponseWithStudyId> categories =
                studyCategoryMappingRepository.findCategoryListByStudyInfoListJoinCategoryMapping(studyInfoIds);

        // Then
        assertEquals(4, categories.size());

        Set<Long> studyInfoIdsFromCategories = categories.stream()
                .map(CategoryResponseWithStudyId::getStudyInfoId)
                .collect(Collectors.toSet());
        assertTrue(studyInfoIdsFromCategories.containsAll(studyInfoIds));
    }

    @Test
    void studyInfoId를_통해_스터디들의_모든_카테고리_이름을_조회() {
        Long user = 1L;

        // 스터디 카테고리 생성
        StudyCategory studyCategory1 = studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("studyCategory1"));
        StudyCategory studyCategory2 = studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("studyCategory2"));

        List<StudyCategory> categoriesOfStudy1 = new ArrayList<>();
        categoriesOfStudy1.add(studyCategory1);
        categoriesOfStudy1.add(studyCategory2);

        // Study 생성
        List<Long> studyInfoIds = new ArrayList<>();
        StudyInfo studyInfo = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user));
        studyInfoIds.add(studyInfo.getId());

        // 스터디 카테고리 매핑 생성
        studyCategoryMappingRepository.saveAll(StudyCategoryMappingFixture.generateStudyCategoryMappings(studyInfo, categoriesOfStudy1));

        List<String> categoryNames =
                studyCategoryRepository.findCategoryNameListByStudyInfoJoinCategoryMapping(studyInfo.getId());

        // Then
        assertEquals(2, categoryNames.size());
    }
}