package com.example.backend.domain.define.study.info;

import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.backend.domain.define.study.info.constant.StudyStatus.STUDY_PUBLIC;

public class StudyInfoFixture {

    public static final Long CATEGORIES_ID_1 = 1L;
    public static final Long CATEGORIES_ID_2 = 2L;

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
    public static StudyInfoRegisterRequest generateStudyInfoRegisterRequest(Long userId) {
        List<Long> categoriesId = new ArrayList<>();
        categoriesId.add(CATEGORIES_ID_1);
        categoriesId.add(CATEGORIES_ID_2);

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
    public static StudyInfoRegisterRequest generateStudyInfoRegisterRequestWhenMaximumMemberExceed10(Long userId) {
        List<Long> categoriesId = new ArrayList<>();
        categoriesId.add(CATEGORIES_ID_1);
        categoriesId.add(CATEGORIES_ID_2);

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
    public static StudyInfoRegisterRequest generateStudyInfoRegisterRequestWhenMaximumMemberLessThan1(Long userId) {
        List<Long> categoriesId = new ArrayList<>();
        categoriesId.add(CATEGORIES_ID_1);
        categoriesId.add(CATEGORIES_ID_2);

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
                .maximumMember(-1)
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
}
