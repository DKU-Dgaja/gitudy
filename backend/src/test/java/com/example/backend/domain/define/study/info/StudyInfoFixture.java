package com.example.backend.domain.define.study.info;

import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.request.StudyInfoUpdateRequest;
import com.example.backend.study.api.controller.info.response.UpdateStudyInfoPageResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.backend.domain.define.study.info.constant.StudyStatus.STUDY_PRIVATE;
import static com.example.backend.domain.define.study.info.constant.StudyStatus.STUDY_PUBLIC;

public class StudyInfoFixture {

    public static StudyInfo createDefaultPublicStudyInfo(Long userId) {
        return StudyInfo.builder()
                .userId(userId)
                .topic("토픽")
                .status(STUDY_PUBLIC)
                .build();
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

    // generateStudyInfoRegisterRequest 생성 해주는 메소드
    public static StudyInfoRegisterRequest generateStudyInfoRegisterRequest(Long userId, List<StudyCategory> studyCategories) {
        List<Long> categoriesId = getCategoriesId(studyCategories);

        return StudyInfoRegisterRequest.builder()
                .userId(userId)
                .topic("Sample Study")
                .endDate(LocalDate.now().plusMonths(3))
                .info("This is a sample study.")
                .status(StudyStatus.STUDY_PUBLIC)
                .maximumMember(5)
                .profileImageUrl("https://example.com/profile.jpg")
                .repositoryInfo(new RepositoryInfo("구영민", "aaa333", "BRANCH_NAME"))
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .categoriesId(categoriesId)
                .build();
    }

    // MaximumMember가 10보다 클 때, generateStudyInfoRegisterRequest 생성 해주는 메소드
    public static StudyInfoRegisterRequest generateStudyInfoRegisterRequestWhenMaximumMemberExceed10(Long userId, List<StudyCategory> studyCategories) {
        List<Long> categoriesId = getCategoriesId(studyCategories);

        return StudyInfoRegisterRequest.builder()
                .userId(userId)
                .topic("Sample Study")
                .endDate(LocalDate.now().plusMonths(3))
                .info("This is a sample study.")
                .status(StudyStatus.STUDY_PUBLIC)
                .maximumMember(11)
                .profileImageUrl("https://example.com/profile.jpg")
                .repositoryInfo(new RepositoryInfo("구영민", "aaa333", "BRANCH_NAME"))
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .categoriesId(categoriesId)
                .build();
    }

    // MaximumMember가 1보다 작을 때, generateStudyInfoRegisterRequest 생성 해주는 메소드
    public static StudyInfoRegisterRequest generateStudyInfoRegisterRequestWhenMaximumMemberLessThan1(Long userId, List<StudyCategory> studyCategories) {
        List<Long> categoriesId = getCategoriesId(studyCategories);

        return StudyInfoRegisterRequest.builder()
                .userId(userId)
                .topic("Sample Study")
                .endDate(LocalDate.now().plusMonths(3))
                .info("This is a sample study.")
                .status(StudyStatus.STUDY_PUBLIC)
                .maximumMember(-1)
                .profileImageUrl("https://example.com/profile.jpg")
                .repositoryInfo(new RepositoryInfo("구영민", "aaa333", "BRANCH_NAME"))
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .categoriesId(categoriesId)
                .build();
    }

    // 카테고리를 받아 StudyInfoRegisterRequest요청을 생성해주는 함수
    public static StudyInfoRegisterRequest generateStudyInfoRegisterRequestWithCategory(Long userId, List<StudyCategory> studyCategories) {
        List<Long> categoriesId = getCategoriesId(studyCategories);

        return StudyInfoRegisterRequest.builder()
                .userId(userId)
                .topic("Sample Study")
                .endDate(LocalDate.now().plusMonths(3))
                .info("This is a sample study.")
                .status(StudyStatus.STUDY_PUBLIC)
                .maximumMember(5)
                .profileImageUrl("https://example.com/profile.jpg")
                .repositoryInfo(new RepositoryInfo("구영민", "aaa333", "BRANCH_NAME"))
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .categoriesId(categoriesId)
                .build();
    }

    // 업데이트 된 StudyInfoUpdateRequest를 카테고리를 파라미터로 받아 생성해주는 함수
    public static StudyInfoUpdateRequest generateUpdatedStudyInfoUpdateRequestWithCategory(Long userId, List<StudyCategory> studyCategories) {
        List<Long> categoriesId = getCategoriesId(studyCategories);

        return StudyInfoUpdateRequest.builder()
                .userId(userId)
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
        List<Long> categoriesId = getCategoriesId(studyCategories);

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
                .categoriesId(categoriesId)
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

    // 비공개 스터디 생성 메서드
    public static StudyInfo createDefaultPrivateStudyInfo(Long userId) {
        return StudyInfo.builder()
                .userId(userId)
                .topic("토픽")
                .status(STUDY_PRIVATE)
                .build();
    }
}
