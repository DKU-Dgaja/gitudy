package com.example.backend.study.api.service.category.info;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.category.CategoryException;
import com.example.backend.common.exception.comment.study.StudyCommentException;
import com.example.backend.common.exception.study.StudyInfoException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.category.info.repository.StudyCategoryRepository;
import com.example.backend.domain.define.study.comment.study.StudyComment;
import com.example.backend.study.api.controller.category.info.request.CategoryRegisterRequest;
import com.example.backend.study.api.controller.category.info.request.CategoryUpdateRequest;
import com.example.backend.study.api.controller.comment.study.request.StudyCommentUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    @Autowired
    private StudyCategoryRepository studyCategoryRepository;

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
}
