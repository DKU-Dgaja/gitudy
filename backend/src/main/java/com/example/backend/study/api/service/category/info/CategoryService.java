package com.example.backend.study.api.service.category.info;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.category.CategoryException;
import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.category.info.repository.StudyCategoryRepository;
import com.example.backend.study.api.controller.category.info.request.CategoryRegisterRequest;
import com.example.backend.study.api.controller.category.info.request.CategoryUpdateRequest;
import com.example.backend.study.api.controller.category.info.response.CategoryListAndCursorIdxResponse;
import com.example.backend.study.api.service.category.info.response.CategoryResponse;
import com.example.backend.study.api.service.info.StudyInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final StudyCategoryRepository studyCategoryRepository;
    private final StudyInfoService studyInfoService;

    @Transactional
    public void registerCategory(CategoryRegisterRequest request) {
        StudyCategory studyCategory = StudyCategory.builder()
                .name(request.getName())
                .build();

        studyCategoryRepository.save(studyCategory);
    }

    @Transactional
    public void updateCategory(CategoryUpdateRequest request, Long categoryId) {
        // Category 조회 예외 처리
        StudyCategory studyCategory = findByIdOrThrowCategoryException(categoryId);

        studyCategory.updateCategory(request.getName());
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        // Category 조회 예외처리
        StudyCategory studyCategory = findByIdOrThrowCategoryException(categoryId);

        studyCategoryRepository.deleteById(studyCategory.getId());
    }

    public CategoryListAndCursorIdxResponse selectCategoryList(Long studyInfoId, Long cursorIdx, Long limit) {
        // 스터디 조회 예외처리
        studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        List<CategoryResponse> categoryNames =
                studyCategoryRepository.findCategoryListByStudyInfoIdJoinCategoryMapping(studyInfoId, cursorIdx, limit);

        CategoryListAndCursorIdxResponse response = (CategoryListAndCursorIdxResponse.builder()
                .categoryNames(categoryNames)
                .build());
        response.getNextCursorIdx();
        return response;
    }

    public StudyCategory findByIdOrThrowCategoryException(Long categoryId) {
        return studyCategoryRepository.findById(categoryId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", categoryId, ExceptionMessage.CATEGORY_NOT_FOUND.getText());
            return new CategoryException(ExceptionMessage.CATEGORY_NOT_FOUND);
        });
    }

    public List<CategoryResponse> selectCategoryList() {

        return studyCategoryRepository.findAll().stream()  // Stream<Category>
                .map(CategoryResponse::of) // Stream<CategoryResponse>
                .toList();  // List<CategoryList>
    }
}
