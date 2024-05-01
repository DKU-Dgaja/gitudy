package com.example.backend.study.api.service.category.info;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.category.CategoryException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.StudyCategory.info.StudyCategoryFixture;
import com.example.backend.domain.define.study.StudyCategory.mapping.StudyCategoryMappingFixture;
import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.category.info.repository.StudyCategoryRepository;
import com.example.backend.domain.define.study.category.mapping.repository.StudyCategoryMappingRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.controller.category.info.request.CategoryRegisterRequest;
import com.example.backend.study.api.controller.category.info.request.CategoryUpdateRequest;
import com.example.backend.study.api.controller.category.info.response.CategoryListAndCursorIdxResponse;
import com.example.backend.study.api.service.category.info.response.CategoryResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class CategoryServiceTest extends TestConfig {
    private final static int DATA_SIZE = 10;
    private final static Long LIMIT = 5L;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyCategoryRepository studyCategoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyCategoryMappingRepository studyCategoryMappingRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyCategoryRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyCategoryMappingRepository.deleteAllInBatch();
    }
    @Test
    void 카테고리_등록_테스트() {
        // given
        User user = userRepository.save(generateAuthUser());

        CategoryRegisterRequest request = CategoryRegisterRequest.builder()
                .name("categoryName")
                .build();

        //when
        categoryService.registerCategory(request);

        // then
        List<StudyCategory> savedStudyCategories = studyCategoryRepository.findAll();
        assertEquals(savedStudyCategories.size(), 1);
    }

    @Test
    void 카테고리_수정_테스트() {
        // given
        String updatedName = "update";
        User user = userRepository.save(generateAuthUser());

        StudyCategory studyCategory
                = studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("name"));
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name(updatedName)
                .build();

        //when
        categoryService.updateCategory(request, studyCategory.getId());

        // then
        Optional<StudyCategory> savedStudyCategory = studyCategoryRepository.findById(studyCategory.getId());
        assertEquals(savedStudyCategory.get().getName(), updatedName);
    }
    @Test
    void 카테고리_수정_예외_테스트_카테고리_없음() {
        // given
        Long invaildCategoryId = 987654L;
        String updatedName = "update";
        User user = userRepository.save(generateAuthUser());

        StudyCategory studyCategory
                = studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("name"));
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name(updatedName)
                .build();

        // when, then
        assertThrows(CategoryException.class, () -> {
            categoryService.updateCategory(request, invaildCategoryId);
        }, ExceptionMessage.CATEGORY_NOT_FOUND.getText());
    }
    @Test
    void 카테고리_삭제_테스트() {
        // given
        userRepository.save(generateAuthUser());

        StudyCategory studyCategory
                = studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("name"));

        //when
        categoryService.deleteCategory(studyCategory.getId());

        // then
        Optional<StudyCategory> savedStudyCategory = studyCategoryRepository.findById(studyCategory.getId());
        assertTrue(savedStudyCategory.isEmpty());
    }

    @Test
    void 카테고리_삭제_예외_테스트_카테고리_없음() {
        // given
        Long invaildCategoryId = 987654L;
        userRepository.save(generateAuthUser());

        studyCategoryRepository.save(StudyCategoryFixture.createDefaultPublicStudyCategory("name"));

        // when, then
        assertThrows(CategoryException.class, () -> {
            categoryService.deleteCategory(invaildCategoryId);
        }, ExceptionMessage.CATEGORY_NOT_FOUND.getText());
    }

    @Test
    void 커서가_null이_아닌_경우_카테고리_리스트_조회_테스트_1() {
        // given
        Long cursorIdx = 5L;

        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));

        List<StudyCategory> categories
                = studyCategoryRepository.saveAll(StudyCategoryFixture.createDefaultPublicStudyCategories(DATA_SIZE));
        studyCategoryMappingRepository.saveAll(StudyCategoryMappingFixture.generateStudyCategoryMappings(study, categories));

        // when
        CategoryListAndCursorIdxResponse categoryListAndCursorIdxResponse = categoryService.selectCategoryList(study.getId(), cursorIdx, LIMIT);

        for (CategoryResponse categoryResponse : categoryListAndCursorIdxResponse.getCategoryResponseList()) {
            assertTrue(categoryResponse.getId() < cursorIdx);
            System.out.println(categoryResponse.getId() + " " + categoryResponse.getName());
        }
    }

    @Test
    void 커서가_null이_아닌_경우_카테고리_리스트_조회_테스트_2() {
        // given
        Long cursorIdx = 15L;

        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));

        List<StudyCategory> categories
                = studyCategoryRepository.saveAll(StudyCategoryFixture.createDefaultPublicStudyCategories(DATA_SIZE));
        studyCategoryMappingRepository.saveAll(StudyCategoryMappingFixture.generateStudyCategoryMappings(study, categories));

        // when
        CategoryListAndCursorIdxResponse categoryListAndCursorIdxResponse = categoryService.selectCategoryList(study.getId(), cursorIdx, LIMIT);

        for (CategoryResponse categoryResponse : categoryListAndCursorIdxResponse.getCategoryResponseList()) {
            assertTrue(categoryResponse.getId() < cursorIdx);
            System.out.println(categoryResponse.getId() + " " + categoryResponse.getName());
        }
    }

    @Test
    void 커서가_null인_경우_카테고리_리스트_조회_테스트() {
        // given
        Long cursorIdx = null;

        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfo(user.getId()));

        List<StudyCategory> categories
                = studyCategoryRepository.saveAll(StudyCategoryFixture.createDefaultPublicStudyCategories(DATA_SIZE));
        studyCategoryMappingRepository.saveAll(StudyCategoryMappingFixture.generateStudyCategoryMappings(study, categories));

        // when
        CategoryListAndCursorIdxResponse categoryListAndCursorIdxResponse = categoryService.selectCategoryList(study.getId(), cursorIdx, LIMIT);

        assertEquals(LIMIT, categoryListAndCursorIdxResponse.getCategoryResponseList().size());
    }
}