package com.example.backend.study.api.service.info;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.study.StudyInfoException;
import com.example.backend.domain.define.study.category.mapping.StudyCategoryMapping;
import com.example.backend.domain.define.study.category.mapping.repository.StudyCategoryMappingRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.request.StudyInfoUpdateRequest;
import com.example.backend.study.api.controller.info.response.MyStudyInfoListResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoRegisterResponse;
import com.example.backend.study.api.controller.info.response.UpdateStudyInfoPageResponse;
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

    private final StudyMemberRepository studyMemberRepository;

    @Transactional
    public StudyInfoRegisterResponse registerStudy(StudyInfoRegisterRequest request) {
        // 새로운 스터디 생성
        StudyInfo studyInfo = saveStudyInfo(request);

        // 스터디장 생성
        StudyMember studyMember = saveStudyMember(request, studyInfo);

        // 스터디 카테고리 매핑
        List<Long> categories = saveStudyCategoryMappings(request.getCategoriesId(), studyInfo);

        return StudyInfoRegisterResponse.of(studyInfo, categories);
    }


    @Transactional
    public void updateStudyInfo(StudyInfoUpdateRequest request, Long studyInfoId) {
        // Study 조회
        StudyInfo studyInfo = studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_INFO_NOT_FOUND.getText());
            return new StudyInfoException(ExceptionMessage.STUDY_INFO_NOT_FOUND);
        });

        // 변경 전 카테고리 매핑 삭제
        studyCategoryMappingRepository.deleteByStudyInfoId(studyInfo.getId());

        // 변경 후 카테고리 매핑 생성
        List<Long> categories = saveStudyCategoryMappings(request.getCategoriesId(), studyInfo);

        // 스터디 업데이트
        studyInfo.updateStudyInfo(request);
    }

    // 스터디 삭제
    @Transactional
    public boolean deleteStudy(Long studyInfoId) {
        // 스터디가 있는지 확인
        StudyInfo studyInfo = studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_INFO_NOT_FOUND.getText());
            throw new StudyInfoException(ExceptionMessage.STUDY_INFO_NOT_FOUND);
        });

        // 스터디 상태정보 변경
        studyInfo.updateDeletedStudy();

        // 스터디 멤버 상태정보 변경
        updateWithdrawalStudyMember(studyInfoId);

        return true;
    }

    public UpdateStudyInfoPageResponse updateStudyInfoPage(Long studyInfoId) {
        // Study 조회
        StudyInfo studyInfo = studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_INFO_NOT_FOUND.getText());
            return new StudyInfoException(ExceptionMessage.STUDY_INFO_NOT_FOUND);
        });
        List<Long> categoriesId = getCategoriesId(studyInfoId);

        UpdateStudyInfoPageResponse response = getUpdateStudyInfoPageResponse(studyInfo, categoriesId);

        return response;
    }

    // 정렬된 모든 마이 스터디 조회
    public List<MyStudyInfoListResponse> selectMyStudyInfoList(Long userId, Long cursorIdx, Long limit, String sortBy) {
        return studyInfoRepository.findMyStudyInfoListByParameter_CursorPaging(userId, cursorIdx, limit, sortBy);
    }

    // studyinfoId를 파라미터로 받아 카테고리 id를 생성해주는 함수
    private List<Long> getCategoriesId(Long studyInfoId) {
        List<Long> categoriesId = studyCategoryMappingRepository.findByStudyInfoId(studyInfoId)
                .stream()
                .map(StudyCategoryMapping::getStudyCategoryId)
                .collect(Collectors.toList());
        return categoriesId;
    }

    private static UpdateStudyInfoPageResponse getUpdateStudyInfoPageResponse(StudyInfo studyInfo, List<Long> categoriesId) {
        UpdateStudyInfoPageResponse response = UpdateStudyInfoPageResponse.builder()
                .userId(studyInfo.getUserId())
                .topic(studyInfo.getTopic())
                .endDate(studyInfo.getEndDate())
                .info(studyInfo.getInfo())
                .status(studyInfo.getStatus())
                .joinCode(studyInfo.getJoinCode())
                .maximumMember(studyInfo.getMaximumMember())
                .profileImageUrl(studyInfo.getProfileImageUrl())
                .repositoryInfo(studyInfo.getRepositoryInfo())
                .periodType(studyInfo.getPeriodType())
                .categoriesId(categoriesId)
                .build();
        return response;
    }

    private void updateWithdrawalStudyMember(Long studyInfoId) {
        List<StudyMember> studyMembers = studyMemberRepository.findByStudyInfoId(studyInfoId);
        studyMembers.forEach(StudyMember::updateWithdrawalStudyMember);
    }

    // 카테고리 매핑 생성해주는 함수
    private List<Long> saveStudyCategoryMappings(List<Long> categories, StudyInfo studyInfo) {
        List<Long> categoriesId = new ArrayList<>();
        List<StudyCategoryMapping> studyCategoryMapping = categories.stream()
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
