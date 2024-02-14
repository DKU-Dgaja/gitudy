package com.example.backend.study.api.service.info;

import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.response.StudyInfoRegisterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyInfoService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int JOIN_CODE_LENGTH = 10;

    private final StudyInfoRepository studyInfoRepository;
    @Transactional
    public StudyInfoRegisterResponse registerStudy(StudyInfoRegisterRequest request) {
        String joinCode = generateRandomString(JOIN_CODE_LENGTH);
        StudyInfo studyInfo = StudyInfo.builder()
                .userId(request.getUserId())
                .topic(request.getTopic())
                .score(0)
                .endDate(request.getEndDate())
                .info(request.getInfo())
                .status(request.getStatus())
                .joinCode(joinCode)
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
    private String generateRandomString(int length) {
        Random random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
