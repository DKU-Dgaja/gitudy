package com.example.backend.study.api.service.info;

import com.example.backend.domain.define.study.category.mapping.StudyCategoryMapping;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.response.StudyInfoRegisterResponse;
import com.example.backend.study.api.service.category.mapping.repository.StudyCategoryMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.backend.domain.define.study.member.constant.StudyMemberRole.STUDY_LEADER;
import static com.example.backend.domain.define.study.member.constant.StudyMemberStatus.STUDY_ACTIVE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyInfoService {

    private final StudyInfoRepository studyInfoRepository;

    private final StudyMemberRepository memberRepository;

    private final StudyCategoryMappingRepository studyCategoryMappingRepository;

    @Transactional
    public StudyInfoRegisterResponse registerStudy(StudyInfoRegisterRequest request) {
        // 새로운 스터디 생성
        StudyInfo studyInfo = saveStudyInfo(request);

        // 스터디장 생성
        StudyMember studyMember = saveStudyMember(request, studyInfo);

        // 스터디 카테고리 매핑
        List<Long> categories = saveStudyCategoryMappings(request, studyInfo);

        return StudyInfoRegisterResponse.of(studyInfo, categories);
    }

    // 카테고리 매핑 생성해주는 함수
    private List<Long> saveStudyCategoryMappings(StudyInfoRegisterRequest request, StudyInfo studyInfo) {
        List<Long> categoriesId = new ArrayList<>();
        List<StudyCategoryMapping> studyCategoryMapping = request.getCategoriesId().stream()
                .peek(categoriesId::add)
                .map(categoryId -> StudyCategoryMapping.builder()
                        .studyInfoId(studyInfo.getId())
                        .studyCategoryId(categoryId)
                        .build())
                .collect(Collectors.toList());

        studyCategoryMappingRepository.saveAll(studyCategoryMapping);
        return categoriesId;
    }

    // StudyMember를 leader로 생성해주는 함수
    private StudyMember saveStudyMember(StudyInfoRegisterRequest request, StudyInfo studyInfo) {
        StudyMember studyMember = StudyMember.builder()
                .studyInfoId(studyInfo.getId())
                .userId(request.getUserId())
                .role(STUDY_LEADER)
                .status(STUDY_ACTIVE)
                .score(0)
                .build();
        return memberRepository.save(studyMember);
    }

    // StudyInfo를 생성해주는 함수
    private StudyInfo saveStudyInfo(StudyInfoRegisterRequest request) {
        StudyInfo studyInfo = StudyInfo.builder()
                .userId(request.getUserId())
                .topic(request.getTopic())
                .score(0)
                .endDate(request.getEndDate())
                .info(request.getInfo())
                .status(request.getStatus())
                .maximumMember(request.getMaximumMember())
                .currentMember(1)
                .lastCommitDay(null)
                .profileImageUrl(request.getProfileImageUrl())
                .notice(null)
                .repositoryInfo(request.getRepositoryInfo())
                .periodType(request.getPeriodType())
                .build();
        return studyInfoRepository.save(studyInfo);
    }
}
