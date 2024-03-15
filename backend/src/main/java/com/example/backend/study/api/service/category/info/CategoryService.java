package com.example.backend.study.api.service.category.info;

import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.category.info.repository.StudyCategoryRepository;
import com.example.backend.study.api.controller.category.info.request.CategoryRegisterRequest;
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
}
