package com.example.backend.domain.define.study.info;

import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.request.StudyInfoUpdateRequest;
import com.example.backend.study.api.controller.info.response.StudyInfoDetailResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoListAndCursorIdxResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoListResponse;
import com.example.backend.study.api.controller.info.response.UpdateStudyInfoPageResponse;
import com.example.backend.study.api.service.info.response.UserNameAndProfileImageResponse;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.backend.domain.define.study.info.constant.StudyStatus.STUDY_PRIVATE;
import static com.example.backend.domain.define.study.info.constant.StudyStatus.STUDY_PUBLIC;

public class StudyInfoFixture {

    public static StudyInfo createDefaultPublicStudyInfo(Long userId) {
        return StudyInfo.builder()
                .userId(userId)
                .topic("토픽")
                .status(STUDY_PUBLIC)
                .maximumMember(10)
                .build();
    }

    // 비공개 스터디 생성 메서드
    public static StudyInfo createPrivateStudyInfo(Long userId, String joinCode) {
        return StudyInfo.builder()
                .userId(userId)
                .topic("토픽")
                .joinCode(joinCode)
                .status(STUDY_PRIVATE)
                .build();
    }

    // 테스트용 스터디 정보 목록 생성 메서드
    public static List<StudyInfo> createDefaultStudyInfoList(int count, Long userId) {
        List<StudyInfo> studyInfos = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            studyInfos.add(generateStudyInfo(userId));
        }
        return studyInfos;
    }

    // StudyInfo 생성 해주는 메소드
    public static StudyInfo generateStudyInfo(Long userId) {
        return StudyInfo.builder()
                .userId(userId)
                .topic("Sample Study")
                .score(100)
                .endDate(LocalDate.now().plusMonths(3))
                .info("This is a sample study.")
                .status(StudyStatus.STUDY_PUBLIC)
                .joinCode("ABC123")
                .maximumMember(5)
                .currentMember(3)
                .lastCommitDay(LocalDate.now())
                .profileImageUrl("https://example.com/profile.jpg")
                .notice("Important notice for the study.")
                .repositoryInfo(new RepositoryInfo("구영민", "aaa333", "BRANCH_NAME"))
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .build();
    }

    // StudyInfoDetailResponse를 생성해주는 함수
    public static StudyInfoDetailResponse generateStudyInfoDetailResponse(StudyInfo studyInfo){
        return StudyInfoDetailResponse.builder()
                .userId(studyInfo.getId())
                .topic(studyInfo.getTopic())
                .score(studyInfo.getScore())
                .info(studyInfo.getInfo())
                .maximumMember(studyInfo.getMaximumMember())
                .currentMember(studyInfo.getCurrentMember())
                .lastCommitDay(studyInfo.getLastCommitDay())
                .profileImageUrl(studyInfo.getProfileImageUrl())
                .periodType(studyInfo.getPeriodType())
                .status(studyInfo.getStatus())
                .createdDateTime(studyInfo.getCreatedDateTime())
                .modifiedDateTime(studyInfo.getModifiedDateTime())
                .build();
    }

    // generateStudyInfoRegisterRequest 생성 해주는 메소드
    public static StudyInfoRegisterRequest generateStudyInfoRegisterRequest(List<StudyCategory> studyCategories) {
        List<Long> categoriesId = getCategoriesId(studyCategories);

        return StudyInfoRegisterRequest.builder()
                .topic("Sample Study")
                .info("This is a sample study.")
                .status(StudyStatus.STUDY_PUBLIC)
                .maximumMember(5)
                .profileImageUrl("https://example.com/profile.jpg")
                .branchName("BRANCH_NAME")
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .categoriesId(categoriesId)
                .build();
    }

    // MaximumMember가 10보다 클 때, generateStudyInfoRegisterRequest 생성 해주는 메소드
    public static StudyInfoRegisterRequest generateStudyInfoRegisterRequestWhenMaximumMemberExceed10(List<StudyCategory> studyCategories) {
        List<Long> categoriesId = getCategoriesId(studyCategories);

        return StudyInfoRegisterRequest.builder()
                .topic("Sample Study")
                .info("This is a sample study.")
                .status(StudyStatus.STUDY_PUBLIC)
                .maximumMember(11)
                .profileImageUrl("https://example.com/profile.jpg")
                .branchName("BRANCH_NAME")
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .categoriesId(categoriesId)
                .build();
    }

    // MaximumMember가 1보다 작을 때, generateStudyInfoRegisterRequest 생성 해주는 메소드
    public static StudyInfoRegisterRequest generateStudyInfoRegisterRequestWhenMaximumMemberLessThan1(List<StudyCategory> studyCategories) {
        List<Long> categoriesId = getCategoriesId(studyCategories);

        return StudyInfoRegisterRequest.builder()
                .topic("Sample Study")
                .info("This is a sample study.")
                .status(StudyStatus.STUDY_PUBLIC)
                .maximumMember(-1)
                .profileImageUrl("https://example.com/profile.jpg")
                .branchName("BRANCH_NAME")
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .categoriesId(categoriesId)
                .build();
    }

    // 카테고리를 받아 StudyInfoRegisterRequest요청을 생성해주는 함수
    public static StudyInfoRegisterRequest generateStudyInfoRegisterRequestWithCategory(List<StudyCategory> studyCategories) {
        List<Long> categoriesId = getCategoriesId(studyCategories);

        return StudyInfoRegisterRequest.builder()
                .topic("Sample Study")
                .info("This is a sample study.")
                .status(StudyStatus.STUDY_PUBLIC)
                .maximumMember(5)
                .profileImageUrl("https://example.com/profile.jpg")
                .branchName("BRANCH_NAME")
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .categoriesId(categoriesId)
                .build();
    }

    // 업데이트 된 StudyInfoUpdateRequest를 카테고리를 파라미터로 받아 생성해주는 함수
    public static StudyInfoUpdateRequest generateUpdatedStudyInfoUpdateRequestWithCategory(List<StudyCategory> studyCategories) {
        List<Long> categoriesId = getCategoriesId(studyCategories);

        return StudyInfoUpdateRequest.builder()
                .topic("Updated : Sample Study")
                .endDate(LocalDate.now().plusMonths(6)) // 3 -> 6으로 변경
                .info("Updated : This is a sample study.")
                .status(StudyStatus.STUDY_PRIVATE) // 공개에서 비공개로 업데이트
                .maximumMember(10) // 5 -> 10으로 변경
                .profileImageUrl("Updated : https://example.com/profile.jpg")
                .repositoryInfo(new RepositoryInfo("Updated : 구영민", "Updated : aaa333", "Updated : BRANCH_NAME"))
                .periodType(StudyPeriodType.STUDY_PERIOD_NONE) // STUDY_PERIOD_EVERYDAY -> STUDY_PERIOD_NONE
                .categoriesId(categoriesId)
                .build();
    }

    // 카테고리를 받아 StudyInfoRegisterRequest요청을 생성해주는 함수
    public static UpdateStudyInfoPageResponse generateUpdateStudyInfoPageResponseWithCategory(Long userId, List<StudyCategory> studyCategories) {
        List<String> categoryNames = getCategoryNames(studyCategories);

        return UpdateStudyInfoPageResponse.builder()
                .userId(userId)
                .topic("Sample Study")
                .endDate(LocalDate.now().plusMonths(3))
                .info("This is a sample study.")
                .status(StudyStatus.STUDY_PUBLIC)
                .joinCode("ABC123")
                .maximumMember(5)
                .profileImageUrl("https://example.com/profile.jpg")
                .repositoryInfo(new RepositoryInfo("구영민", "aaa333", "BRANCH_NAME"))
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .categoryNames(categoryNames)
                .build();
    }

    // 카테고리 id 리스트를 생성해주는 함수
    private static List<Long> getCategoriesId(List<StudyCategory> studyCategories) {
        List<Long> categoriesId = studyCategories.stream()
                .limit(studyCategories.size())
                .map(StudyCategory::getId)
                .collect(Collectors.toList());
        return categoriesId;
    }

    // 카테고리 name 리스트를 생성해주는 함수
    private static List<String> getCategoryNames(List<StudyCategory> studyCategories) {
        List<String> CategoryNames = studyCategories.stream()
                .limit(studyCategories.size())
                .map(StudyCategory::getName)
                .collect(Collectors.toList());
        return CategoryNames;
    }

    // 테스트용 스터디 정보 생성 메서드 (Score와 LastCommitDay는 랜덤 값으로 생성)
    public static List<StudyInfo> createDefaultStudyInfoListRandomScoreAndLastCommitDay(int count, Long userId) {
        List<StudyInfo> studyInfos = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            studyInfos.add(createDefaultStudyInfoRandomScoreAndLastCommitDay(userId));
        }
        return studyInfos;
    }

    // 테스트용 스터디 정보 생성 메서드 (Score와 LastCommitDay는 랜덤 값으로 생성)
    public static StudyInfo createDefaultStudyInfoRandomScoreAndLastCommitDay(Long userId) {
        Random random = new Random();
        int minScore = 1;
        int maxScore = 100;
        int randomScore = random.nextInt(maxScore - minScore + 1) + minScore;

        // 마지막 커밋 날짜를 현재 날짜를 기준으로 랜덤으로 설정
        LocalDate now = LocalDate.now();
        long daysToAdd = random.nextInt(365);
        LocalDate randomLastCommitDay = now.minusDays(daysToAdd);

        return StudyInfo.builder()
                .userId(userId)
                .topic("Sample Study")
                .score(randomScore) // 랜덤 스코어 설정
                .endDate(now.plusMonths(3))
                .info("info")
                .status(StudyStatus.STUDY_PUBLIC)
                .joinCode("ABC123")
                .maximumMember(5)
                .currentMember(3)
                .lastCommitDay(randomLastCommitDay) // 랜덤 마지막 커밋 날짜 설정
                .profileImageUrl("https://example.com/profile.jpg")
                .notice("Notice")
                .repositoryInfo(new RepositoryInfo("구영민", "aaa333", "BRANCH_NAME"))
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .build();
    }
    // MyStudyInfoListAndCursorIdxResponse를 생성해주는 함수
    public static StudyInfoListAndCursorIdxResponse generateMyStudyInfoListAndCursorIdxResponse() {
        List<StudyInfoListResponse> studyInfoList = new ArrayList<>();
        Map<Long, List<UserNameAndProfileImageResponse>> studyUserInfoMap = new HashMap<>();
        Map<Long, List<String>> studyCategoryMappingMap = new HashMap<>();
        Long cursorIdx = 123L;

        return StudyInfoListAndCursorIdxResponse.builder()
                .studyInfoList(studyInfoList)
                .cursorIdx(cursorIdx)
                .studyUserInfoMap(studyUserInfoMap)
                .studyCategoryMappingMap(studyCategoryMappingMap)
                .build();
    }

    // 누락 테스트용 스터디 정보 생성 메서드 (Score지정)
    public static StudyInfo testSortScoreStudyCursorPaginationWithoutMissingData(Long userId, int score) {
        Random random = new Random();

        // 마지막 커밋 날짜를 현재 날짜를 기준으로 랜덤으로 설정
        LocalDate now = LocalDate.now();
        long daysToAdd = random.nextInt(365);
        LocalDate randomLastCommitDay = now.minusDays(daysToAdd);

        return StudyInfo.builder()
                .userId(userId)
                .topic("Sample Study")
                .score(score) // 랜덤 스코어 설정
                .endDate(now.plusMonths(3))
                .info("info")
                .status(StudyStatus.STUDY_PUBLIC)
                .joinCode("ABC123")
                .maximumMember(5)
                .currentMember(3)
                .lastCommitDay(randomLastCommitDay) // 랜덤 마지막 커밋 날짜 설정
                .profileImageUrl("https://example.com/profile.jpg")
                .notice("Notice")
                .repositoryInfo(new RepositoryInfo("구영민", "aaa333", "BRANCH_NAME"))
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .build();
    }

    // 누락 테스트용 스터디 정보 생성 메서드 (Score지정)
    public static StudyInfo testSortLastCommitDayStudyCursorPaginationWithoutMissingData(Long userId, LocalDate lastCommitDay) {
        LocalDate now = LocalDate.now();

        return StudyInfo.builder()
                .userId(userId)
                .topic("Sample Study")
                .score(100)
                .endDate(now.plusMonths(3))
                .info("info")
                .status(StudyStatus.STUDY_PUBLIC)
                .joinCode("ABC123")
                .maximumMember(5)
                .currentMember(3)
                .lastCommitDay(lastCommitDay) // 랜덤 마지막 커밋 날짜 설정
                .profileImageUrl("https://example.com/profile.jpg")
                .notice("Notice")
                .repositoryInfo(new RepositoryInfo("구영민", "aaa333", "BRANCH_NAME"))
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .build();
    }
    // 테스트용 스터디 정보 리스트 생성 메서드 (Score와 LastCommitDay는 랜덤 값으로 생성)
    public static List<StudyInfo> createDefaultcreateDefaultStudyInfoRandomScoreAndLastCommitDayList(int count, Long userId) {
        List<StudyInfo> studyInfos = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            studyInfos.add(createDefaultStudyInfoRandomScoreAndLastCommitDay(userId));
        }
        return studyInfos;
    }
}
