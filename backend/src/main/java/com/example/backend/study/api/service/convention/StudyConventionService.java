package com.example.backend.study.api.service.convention;


import com.example.backend.domain.define.study.convention.StudyConvention;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.study.api.controller.convention.request.StudyConventionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyConventionService {


    private final StudyConventionRepository studyConventionRepository;

    // 컨벤션 등록
    @Transactional
    public void registerStudyConvention(StudyConventionRequest request) {

        // 컨벤션 저장
        studyConventionRepository.save(StudyConvention.builder()
                .studyInfoId(request.getStudyInfoId())
                .name(request.getName())
                .description(request.getDescription())
                .content(request.getContent())
                .isActive(request.isActive())
                .build());
    }
}
