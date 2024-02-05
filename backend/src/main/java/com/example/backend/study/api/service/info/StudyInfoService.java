package com.example.backend.study.api.service.info;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.study.info.StudyInfoException;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.response.AllStudyInfoResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoRegisterResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyInfoService {
    private final StudyInfoRepository studyInfoRepository;

    // 스터디 등록
    @Transactional
    public StudyInfoRegisterResponse registerStudy(StudyInfoRegisterRequest request) {
        if(request.getMaximumMember() > 10){
            log.warn(">>>> {} : {} <<<<", request.getMaximumMember(), ExceptionMessage.MAXIMUM_10_ERROR.getText());
            throw new StudyInfoException(ExceptionMessage.MAXIMUM_10_ERROR);
        }
        if(request.getMaximumMember() < 0){
            log.warn(">>>> {} : {} <<<<", request.getMaximumMember(), ExceptionMessage. MINIMUM_1_ERROR.getText());
            throw new StudyInfoException(ExceptionMessage. MINIMUM_1_ERROR);
        }
        StudyInfo studyInfo = StudyInfo.builder()
                .userId(request.getUserId())
                .topic(request.getTopic())
                .score(0)
                .endDate(request.getEndDate())
                .info(request.getInfo())
                .status(request.getStatus())
                .joinCode(request.getJoinCode())
                .maximumMember(request.getMaximumMember())
                .currentMember(1)
                .lastCommitDay(null)
                .profileImageUrl(request.getProfileImageUrl())
                .notice(null)
                .repositoryInfo(request.getRepositoryInfo())
                .periodType(request.getPeriodType())
                .build();
        studyInfoRepository.save(studyInfo);
        return StudyInfoRegisterResponse.of(studyInfo);
    }

    // StudyInfo 상세정보 조회
    public Optional<StudyInfoResponse> selectStudyInfo(Long studyInfoId) {
        studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDYINFO_NOT_FOUND.getText());
            throw new StudyInfoException(ExceptionMessage.STUDYINFO_NOT_FOUND);
        });
        Optional<StudyInfo> request = studyInfoRepository.findById(studyInfoId);
        StudyInfoResponse response = StudyInfoResponse.builder()
                .userId(request.get().getUserId())
                .topic(request.get().getTopic())
                .score(request.get().getScore())
                .endDate(request.get().getEndDate())
                .info(request.get().getInfo())
                .status(request.get().getStatus())
                .maximumMember(request.get().getMaximumMember())
                .currentMember(request.get().getCurrentMember())
                .profileImageUrl(request.get().getProfileImageUrl())
                .notice(request.get().getNotice())
                .repositoryInfo(request.get().getRepositoryInfo())
                .periodType(request.get().getPeriodType())
                .build();
        return Optional.ofNullable(response);
    }

    // StudyInfo 모두 조회
    @Transactional
    public List<AllStudyInfoResponse> selectStudyInfoList() {
        List<AllStudyInfoResponse> response = studyInfoRepository.findAll().stream()
                .map(i -> AllStudyInfoResponse.of(StudyInfo.builder()
                        .userId(i.getUserId())
                        .topic(i.getTopic())
                        .score(i.getScore())
                        .endDate(i.getEndDate())
                        .info(i.getInfo())
                        .status(i.getStatus())
                        .maximumMember(i.getMaximumMember())
                        .currentMember(i.getCurrentMember())
                        .lastCommitDay(i.getLastCommitDay())
                        .profileImageUrl(i.getProfileImageUrl())
                        .periodType(i.getPeriodType())
                        .build()))
                .collect(Collectors.toList());
        return response;
    }


    // 스터디 삭제
    @Transactional
    public boolean deleteStudy(Long studyInfoId) {
        StudyInfo studyInfo = studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDYINFO_NOT_FOUND.getText());
            throw new StudyInfoException(ExceptionMessage.STUDYINFO_NOT_FOUND);
        });
        studyInfoRepository.delete(studyInfo);
        return true;
    }
}
