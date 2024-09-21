package com.example.backend.study.api.service.info;

import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyManagementService {
    private final StudyInfoRepository studyInfoRepository;
    private final StudyMemberRepository studyMemberRepository;
    @Transactional
    public void closeStudiesOwnedByUser(Long userId) {
        studyInfoRepository.closeStudiesOwnedByUserId(userId);
        log.info(">>>> Closed studies owned by {} <<<<", userId);
    }

    @Transactional
    public void inactiveUserFromAllStudies(Long userId) {
        studyMemberRepository.inActiveFromAllStudiesByUserId(userId);
        log.info(">>>> Inactivated user {} from all studies <<<<", userId);
    }
}
