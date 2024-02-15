package com.example.backend.study.api.service.info;

import com.example.backend.domain.define.study.category.mapping.StudyCategoryMapping;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.response.StudyInfoRegisterResponse;
import com.example.backend.study.api.service.category.info.repository.StudyCategoryRepository;
import com.example.backend.study.api.service.category.mapping.repository.StudyCategoryMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.example.backend.domain.define.study.member.constant.StudyMemberRole.STUDY_LEADER;
import static com.example.backend.domain.define.study.member.constant.StudyMemberStatus.STUDY_ACTIVE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyInfoService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int JOIN_CODE_LENGTH = 10;

    private final StudyInfoRepository studyInfoRepository;

    private final StudyMemberRepository memberRepository;

    private final StudyCategoryRepository studyCategoryRepository;

    private final StudyCategoryMappingRepository studyCategoryMappingRepository;

    @Transactional
    public StudyInfoRegisterResponse registerStudy(StudyInfoRegisterRequest request) {
        // joinCode 생성
        String joinCode = generateRandomString(JOIN_CODE_LENGTH);

        // 새로운 스터디 생성
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

        // 스터디장 생성
        StudyMember studyMember = StudyMember.builder()
                .studyInfoId(studyInfo.getId())
                .userId(request.getUserId())
                .role(STUDY_LEADER)
                .status(STUDY_ACTIVE)
                .score(0)
                .build();
        memberRepository.save(studyMember);

        List<Long> categories = new ArrayList<>();

        List<StudyCategoryMapping> studyCategoryMapping = new ArrayList<StudyCategoryMapping>();

        for (Long categoryId : request.getCategoriesId()) {
            // 스터디 카테코리 매핑
            categories.add(categoryId);
            studyCategoryMapping.add(StudyCategoryMapping.builder()
                    .studyInfoId(studyInfo.getId())
                    .studyCategoryId(categoryId)
                    .build()
            );
        }
        studyCategoryMappingRepository.saveAll(studyCategoryMapping);

        return StudyInfoRegisterResponse.of(studyInfo, categories);
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
