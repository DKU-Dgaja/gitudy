package com.example.backend.study.api.service.convention;


import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.convention.ConventionException;
import com.example.backend.domain.define.study.convention.StudyConvention;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.study.api.controller.convention.request.StudyConventionRequest;
import com.example.backend.study.api.controller.convention.request.StudyConventionUpdateRequest;
import com.example.backend.study.api.controller.convention.response.StudyConventionResponse;
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
    public void registerStudyConvention(StudyConventionRequest request, Long studyInfoId) {

        // 컨벤션 저장
        studyConventionRepository.save(StudyConvention.builder()
                .studyInfoId(studyInfoId)
                .name(request.getName())
                .description(request.getDescription())
                .content(request.getContent())
                .isActive(request.isActive())
                .build());
    }

    // 컨벤션 수정
    @Transactional
    public void updateStudyConvention(StudyConventionUpdateRequest request, Long conventionId) {

        // Convention 조회
        StudyConvention studyConvention = studyConventionRepository.findById(conventionId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", conventionId, ExceptionMessage.CONVENTION_NOT_FOUND.getText());
            return new ConventionException(ExceptionMessage.CONVENTION_NOT_FOUND);
        });

        // 기존 Convention 업데이트
        studyConvention.updateConvention(
                request.getName(),
                request.getDescription(),
                request.getContent(),
                request.isActive());
    }

    // 컨벤션 삭제
    @Transactional
    public void deleteStudyConvention(Long conventionId) {

        // Convention 조회
        StudyConvention studyConvention = studyConventionRepository.findById(conventionId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", conventionId, ExceptionMessage.CONVENTION_NOT_FOUND.getText());
            return new ConventionException(ExceptionMessage.CONVENTION_NOT_FOUND);
        });

        studyConventionRepository.delete(studyConvention);
    }

    // 컨벤션 단일 조회
    public StudyConventionResponse readStudyConvention(Long conventionId) {

        // Convention 조회
        StudyConvention studyConvention = studyConventionRepository.findById(conventionId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", conventionId, ExceptionMessage.CONVENTION_NOT_FOUND.getText());
            return new ConventionException(ExceptionMessage.CONVENTION_NOT_FOUND);
        });

        return StudyConventionResponse.of(studyConvention);
    }

}
