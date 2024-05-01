package com.example.backend.study.api.service.convention;


import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.convention.ConventionException;
import com.example.backend.domain.define.study.convention.StudyConvention;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.controller.convention.request.StudyConventionRequest;
import com.example.backend.study.api.controller.convention.request.StudyConventionUpdateRequest;
import com.example.backend.study.api.controller.convention.response.StudyConventionListAndCursorIdxResponse;
import com.example.backend.study.api.controller.convention.response.StudyConventionResponse;
import com.example.backend.study.api.service.info.StudyInfoService;
import com.example.backend.study.api.service.github.response.GithubCommitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyConventionService {


    private final StudyConventionRepository studyConventionRepository;
    private final StudyInfoService studyInfoService;

    private final static Long MAX_LIMIT = 10L;

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
        // Convention 조회 예외처리
        StudyConvention studyConvention = findByIdOrThrowStudyConventionException(conventionId);

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
        // Convention 조회 예외처리
        StudyConvention studyConvention = findByIdOrThrowStudyConventionException(conventionId);

        studyConventionRepository.delete(studyConvention);
    }

    // 컨벤션 단일 조회
    public StudyConventionResponse readStudyConvention(Long conventionId) {
        // Convention 조회 예외처리
        StudyConvention studyConvention = findByIdOrThrowStudyConventionException(conventionId);

        return StudyConventionResponse.of(studyConvention);
    }

    // 컨벤션 전체 조회
    public StudyConventionListAndCursorIdxResponse readStudyConventionList(Long studyInfoId, Long cursorIdx, Long limit) {

        // 스터디 조회 예외처리
        studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        limit = Math.min(limit, MAX_LIMIT);

        List<StudyConventionResponse> studyConventionList = studyConventionRepository.findStudyConventionListByStudyInfoId_CursorPaging(studyInfoId, cursorIdx, limit);

        StudyConventionListAndCursorIdxResponse response = StudyConventionListAndCursorIdxResponse.builder()
                .studyConventionList(studyConventionList)
                .build();

        response.setNextCursorIdx();

        return response;

    }

    // 컨벤션 검사 로직
    public boolean checkConvention(String convention, String commitMsg) {
        // 정규식 패턴 생성
        Pattern pattern = Pattern.compile(convention);

        // 커밋 메세지가 정규식과 일치하는지 반환
        return pattern.matcher(commitMsg).matches();
    }

    public StudyConvention findByIdOrThrowStudyConventionException(Long conventionId) {
        return studyConventionRepository.findById(conventionId)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", conventionId, ExceptionMessage.CONVENTION_NOT_FOUND.getText());
                    return new ConventionException(ExceptionMessage.CONVENTION_NOT_FOUND);
                });
    }
}
