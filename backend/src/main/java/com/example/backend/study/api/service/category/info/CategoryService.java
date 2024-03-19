package com.example.backend.study.api.service.category.info;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.category.CategoryException;
import com.example.backend.common.exception.study.StudyInfoException;
import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.category.info.repository.StudyCategoryRepository;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.controller.category.info.request.CategoryRegisterRequest;
import com.example.backend.study.api.controller.category.info.request.CategoryUpdateRequest;
import com.example.backend.study.api.controller.category.info.response.CategoryListAndCursorIdxResponse;
import com.example.backend.study.api.service.category.info.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    @Autowired
    private StudyCategoryRepository studyCategoryRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Transactional
    public void registerCategory(CategoryRegisterRequest request) {
        StudyCategory studyCategory = StudyCategory.builder()
                .name(request.getName())
                .build();

        studyCategoryRepository.save(studyCategory);
    }

    @Transactional
    public void updateCategory(CategoryUpdateRequest request, Long categoryId) {
        StudyCategory studyCategory = studyCategoryRepository.findById(categoryId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", categoryId, ExceptionMessage.CATEGORY_NOT_FOUND.getText());
            throw new CategoryException(ExceptionMessage.CATEGORY_NOT_FOUND);
        });
        studyCategory.updateCategory(request.getName());
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        StudyCategory studyCategory = studyCategoryRepository.findById(categoryId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", categoryId, ExceptionMessage.CATEGORY_NOT_FOUND.getText());
            throw new CategoryException(ExceptionMessage.CATEGORY_NOT_FOUND);
        });
        studyCategoryRepository.deleteById(studyCategory.getId());
    }
    public CategoryListAndCursorIdxResponse selectCategoryList(Long studyInfoId, Long cursorIdx, Long limit) {
        // 스터디가 있는지 확인
        studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_INFO_NOT_FOUND.getText());
            throw new StudyInfoException(ExceptionMessage.STUDY_INFO_NOT_FOUND);
        });

        List<CategoryResponse> categoryNames =
                studyCategoryRepository.findCategoryListByStudyInfoIdJoinCategoryMapping(studyInfoId, cursorIdx, limit);

        CategoryListAndCursorIdxResponse response = (CategoryListAndCursorIdxResponse.builder()
                .categoryNames(categoryNames)
                .build());
        response.getNextCursorIdx();
        return response;
    }
}
