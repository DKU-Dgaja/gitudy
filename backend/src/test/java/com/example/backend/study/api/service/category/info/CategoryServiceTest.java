package com.example.backend.study.api.service.category.info;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.category.info.repository.StudyCategoryRepository;
import com.example.backend.study.api.controller.category.info.request.CategoryRegisterRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class CategoryServiceTest extends TestConfig {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyCategoryRepository studyCategoryRepository;

    @Autowired
    private CategoryService categoryService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyCategoryRepository.deleteAllInBatch();
    }
    @Test
    void 카테고리_등록_테스트() {
        // given
        User user = userRepository.save(generateAuthUser());

        CategoryRegisterRequest request = CategoryRegisterRequest.builder()
                .userId(user.getId())
                .name("categoryName")
                .build();

        //when
        categoryService.registerCategory(request);

        // then
        List<StudyCategory> savedStudyCategories = studyCategoryRepository.findAll();
        assertEquals(savedStudyCategories.size(), 1);
    }
}