package com.example.backend.study.api.service.info;

import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.github.GithubApiException;
import com.example.backend.common.exception.study.StudyInfoException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.category.info.repository.StudyCategoryRepository;
import com.example.backend.domain.define.study.category.mapping.StudyCategoryMapping;
import com.example.backend.domain.define.study.category.mapping.repository.StudyCategoryMappingRepository;
import com.example.backend.domain.define.study.convention.StudyConvention;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.domain.define.study.github.GithubApiToken;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.request.StudyInfoUpdateRequest;
import com.example.backend.study.api.controller.info.response.*;
import com.example.backend.study.api.service.github.GithubApiService;
import com.example.backend.study.api.service.github.GithubApiTokenService;
import com.example.backend.study.api.service.info.response.UserNameAndProfileImageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.backend.domain.define.study.member.constant.StudyMemberRole.STUDY_LEADER;
import static com.example.backend.domain.define.study.member.constant.StudyMemberStatus.STUDY_ACTIVE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyInfoService {
    private final static String DEFAULT_NAME = "default convention";
    private final static String DEFAULT_CONTENT = "^[a-zA-Z0-9]{6} .*";
    private final static String DEFAULT_BRANCH = "main";

    private final StudyInfoRepository studyInfoRepository;
    private final StudyMemberRepository memberRepository;
    private final StudyCategoryMappingRepository studyCategoryMappingRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final StudyCategoryRepository studyCategoryRepository;
    private final UserRepository userRepository;
    private final StudyConventionRepository studyConventionRepository;
    private final GithubApiService githubApiService;
    private final GithubApiTokenService githubApiTokenService;

    @Transactional
    public StudyInfoRegisterResponse registerStudy(StudyInfoRegisterRequest request, UserInfoResponse userInfo) {
        // 새로운 스터디 생성
        StudyInfo studyInfo = saveStudyInfo(request, userInfo);

        // 스터디장 생성
        saveStudyMember(request, studyInfo, userInfo.getUserId());

        // 스터디 카테고리 매핑
        List<Long> categories = saveStudyCategoryMappings(request.getCategoriesId(), studyInfo);

        // 스터디 가입 시 User score +5
        Optional<User> user = userRepository.findById(userInfo.getUserId());
        user.get().addUserScore(5);

        // 기본 컨벤션 생성
        registerDefaultConvention(studyInfo.getId());

        // github에 스터디 레포지토리 생성
        GithubApiToken token = githubApiTokenService.getToken(userInfo.getUserId());
        githubApiService.createRepository(token.githubApiToken(), studyInfo.getRepositoryInfo(), "README.md를 작성해주세요.");

        return StudyInfoRegisterResponse.of(studyInfo, categories);
    }

    @Transactional
    public void updateStudyInfo(StudyInfoUpdateRequest request, Long studyInfoId) {
        // 스터디 조회 예외처리
        StudyInfo studyInfo = findStudyInfoByIdOrThrowException(studyInfoId);

        // 변경 전 카테고리 매핑 삭제
        studyCategoryMappingRepository.deleteByStudyInfoId(studyInfo.getId());

        // 변경 후 카테고리 매핑 생성
        saveStudyCategoryMappings(request.getCategoriesId(), studyInfo);

        // 스터디 업데이트
        studyInfo.updateStudyInfo(request);
    }

    // 스터디 삭제
    @Transactional
    public boolean deleteStudy(Long studyInfoId) {
        // 스터디 조회 예외처리
        StudyInfo studyInfo = findStudyInfoByIdOrThrowException(studyInfoId);

        // 스터디 상태정보 변경
        studyInfo.updateDeletedStudy();

        // 스터디 멤버 상태정보 변경
        updateWithdrawalStudyMember(studyInfoId);

        return true;
    }

    public UpdateStudyInfoPageResponse updateStudyInfoPage(Long studyInfoId) {
        // 스터디 조회 예외처리
        StudyInfo studyInfo = findStudyInfoByIdOrThrowException(studyInfoId);

        List<String> categoryNames = studyCategoryRepository.findCategoryNameListByStudyInfoJoinCategoryMapping(studyInfoId);

        return getUpdateStudyInfoPageResponse(studyInfo, categoryNames);
    }

    // 정렬된 스터디 조회
    public StudyInfoListAndCursorIdxResponse selectStudyInfoList(Long userId, Long cursorIdx, Long limit, String sortBy, boolean myStudy) {
        List<StudyInfoListResponse> studyInfoListResponse = studyInfoRepository.findStudyInfoListByParameter_CursorPaging(userId, cursorIdx, limit, sortBy, myStudy);
        List<Long> studyInfoIdList = getStudyInfoIdList(studyInfoListResponse);


        // Map<STUDY_INFO_ID, List<UserNameAndProfileImageResponse>>
        List<StudyMemberWithUserInfoResponse> studyMemberWithUserInfoResponses
                = studyMemberRepository.findStudyMemberListByStudyInfoListJoinUserInfo(studyInfoIdList);
        Map<Long, List<UserNameAndProfileImageResponse>> studyUserInfoMap = getStudyUserInfoMap(studyMemberWithUserInfoResponses);

        List<StudyInfoListWithMemberResponse> withMemberResponses =
                convertToWithMemberResponse(studyInfoListResponse, studyUserInfoMap);


        // Map<STUDY_INFO_ID, List<STUDY_CATEGORY_NAME>>
        List<CategoryResponseWithStudyId> categoryResponseWithStudyIdList
                = studyCategoryMappingRepository.findCategoryListByStudyInfoListJoinCategoryMapping(studyInfoIdList);
        Map<Long, List<String>> studyCategoryMappingMap = getStudyCategoryMappingMap(categoryResponseWithStudyIdList);


        StudyInfoListAndCursorIdxResponse response = StudyInfoListAndCursorIdxResponse.builder()
                .studyInfoList(withMemberResponses)
                .studyCategoryMappingMap(studyCategoryMappingMap)
                .build();
        response.setNextCursorIdx();
        return response;
    }

    // 스터디 상세정보 조회
    public StudyInfoDetailResponse selectStudyInfoDetail(Long studyInfoId, Long userId) {
        // Study 조회
        StudyInfo studyInfo = findStudyInfoByIdOrThrowException(studyInfoId);

        List<String> categoryNames = studyCategoryRepository.findCategoryNameListByStudyInfoJoinCategoryMapping(studyInfoId);
        return getStudyInfoDetailResponse(studyInfo, categoryNames, userId);
    }

    // 마이/전체스터디 개수 조회
    public StudyInfoCountResponse getStudyInfoCount(Long userId, boolean myStudy) {
        return StudyInfoCountResponse.builder()
                .count(studyInfoRepository.findStudyInfoCount(userId, myStudy))
                .build();
    }

    // StudyInfoDetailResponse를 생성해주는 함수
    private static StudyInfoDetailResponse getStudyInfoDetailResponse(StudyInfo studyInfo, List<String> categoryNames, Long userId) {
        return StudyInfoDetailResponse.of(studyInfo, categoryNames, userId);
    }

    // Map<STUDY_INFO_ID, List<STUDY_CATEGORY_NAME>> 생성해주는 함수
    private static Map<Long, List<String>> getStudyCategoryMappingMap(List<CategoryResponseWithStudyId> categoryResponseWithStudyIdList) {
        return categoryResponseWithStudyIdList.stream()
                .collect(Collectors.groupingBy(
                        CategoryResponseWithStudyId::getStudyInfoId,
                        Collectors.mapping(CategoryResponseWithStudyId::getName, Collectors.toList())
                ));
    }


    // Map<STUDY_INFO_ID, List<UserNameAndProfileImageResponse>> 생성 해주는 함수
    public Map<Long, List<UserNameAndProfileImageResponse>> getStudyUserInfoMap(List<StudyMemberWithUserInfoResponse> studyMemberWithUserInfoResponses) {
        return studyMemberWithUserInfoResponses.stream()
                .collect(Collectors.groupingBy(
                        StudyMemberWithUserInfoResponse::getStudyInfoId,
                        Collectors.mapping(StudyMemberWithUserInfoResponse::getUserNameAndProfileImageResponseList, Collectors.toList())
                ));
    }

    // studyInfoIdList 만들어주는 함수
    private static List<Long> getStudyInfoIdList(List<StudyInfoListResponse> studyInfoListResponse) {
        return studyInfoListResponse.stream()
                .map(StudyInfoListResponse::getId)
                .collect(Collectors.toList());
    }

    private static UpdateStudyInfoPageResponse getUpdateStudyInfoPageResponse(StudyInfo studyInfo, List<String> categoryNames) {
        return UpdateStudyInfoPageResponse.builder()
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
                .categoryNames(categoryNames)
                .build();
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
                .toList();

        studyCategoryMappingRepository.saveAll(studyCategoryMapping);
        return categoriesId;
    }

    // StudyMember를 leader로 생성해주는 함수
    private StudyMember saveStudyMember(StudyInfoRegisterRequest request, StudyInfo studyInfo, Long userId) {
        StudyMember studyMember = StudyMember.builder()
                .studyInfoId(studyInfo.getId())
                .userId(userId)
                .role(STUDY_LEADER)
                .status(STUDY_ACTIVE)
                .score(0)
                .build();
        return memberRepository.save(studyMember);
    }

    // StudyInfo를 생성해주는 함수
    private StudyInfo saveStudyInfo(StudyInfoRegisterRequest request, UserInfoResponse userInfo) {
        StudyInfo studyInfo = StudyInfo.builder()
                .userId(userInfo.getUserId())
                .topic(request.getTopic())
                .score(0)
                .info(request.getInfo())
                .status(request.getStatus())
                .maximumMember(request.getMaximumMember())
                .currentMember(1)
                .lastCommitDay(null)
                .profileImageUrl(request.getProfileImageUrl())
                .notice(null)
                .repositoryInfo(RepositoryInfo.builder()
                        .name(request.getRepositoryName())
                        .owner(userInfo.getGithubId())
                        .branchName(DEFAULT_BRANCH)
                        .build())
                .periodType(request.getPeriodType())
                .build();
        return studyInfoRepository.save(studyInfo);
    }

    // 기본 컨벤션 생성
    public void registerDefaultConvention(Long id) {
        // 기본 컨벤션 저장
        studyConventionRepository.save(StudyConvention.builder()
                .studyInfoId(id)
                .name(DEFAULT_NAME)
                .content(DEFAULT_CONTENT)
                .isActive(true)
                .build());
    }

    public StudyInfo findStudyInfoByIdOrThrowException(Long studyInfoId) {
        return studyInfoRepository.findById(studyInfoId)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_INFO_NOT_FOUND.getText());
                    return new StudyInfoException(ExceptionMessage.STUDY_INFO_NOT_FOUND);
                });
    }

    public static List<StudyInfoListWithMemberResponse> convertToWithMemberResponse(
            List<StudyInfoListResponse> studyInfoListResponses,
            Map<Long, List<UserNameAndProfileImageResponse>> userInfoMap) {

        return studyInfoListResponses.stream()
                .map(studyInfo -> {
                    List<UserNameAndProfileImageResponse> userInfo = userInfoMap.getOrDefault(studyInfo.getId(), new ArrayList<>());
                    return StudyInfoListWithMemberResponse.from(studyInfo, userInfo);
                })
                .collect(Collectors.toList());
    }

    public void checkDuplicateRepoName(UserInfoResponse userInfo, String repoName) {

        // 사용자의 깃허브 토큰 조회
        GithubApiToken token = githubApiTokenService.getToken(userInfo.getUserId());

        // 레포지토리 이름 중복 확인
        if (githubApiService.repositoryExists(token.githubApiToken(), userInfo.getGithubId(), repoName)) {
            log.error(">>>> [ {} : {} ] <<<<", ExceptionMessage.GITHUB_API_REPOSITORY_ALREADY_EXISTS.getText(), repoName);
            throw new GithubApiException(ExceptionMessage.GITHUB_API_REPOSITORY_ALREADY_EXISTS);
        }
    }
}
