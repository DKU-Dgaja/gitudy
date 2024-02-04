package com.example.backend.study.api.service.info;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.study.info.StudyInfoException;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public StudyInfo registerStudy(StudyInfo studyInfo) {
        return studyInfoRepository.save(studyInfo);
    }

    // StudyInfo 상세정보 조회
    @Transactional
    public Optional<StudyInfo> selectStudyInfo(Long id) {

        return studyInfoRepository.findById(id);
    }

    // Item 전부 조회
    @Transactional
    public List<StudyInfo> selectStudyInfoList() {
        return studyInfoRepository.findAll().stream()
                .map(i -> StudyInfo.builder()
                        .userId(i.getUserId())
                        .topic(i.getTopic())
                        .score(i.getScore())
                        .endDate(i.getEndDate())
                        .info(i.getInfo())
                        .status(i.getStatus())
                        .joinCode(i.getJoinCode())
                        .maximumMember(i.getMaximumMember())
                        .currentMember(i.getCurrentMember())
                        .lastCommitDay(i.getLastCommitDay())
                        .profileImageUrl(i.getProfileImageUrl())
                        .notice(i.getNotice())
                        .repositoryInfo(i.getRepositoryInfo())
                        .periodType(i.getPeriodType())
                        .build())
                .collect(Collectors.toList());
    }

    // 스터디 삭제
    @Transactional
    public boolean deleteStudy(Long studyInfoId) {
        StudyInfo studyInfoOptional = studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDYINFO_NOT_FOUND.getText());
            throw new StudyInfoException(ExceptionMessage.STUDYINFO_NOT_FOUND);
        });
        return true;
    }
}
