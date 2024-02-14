package com.example.backend.auth;

/*
* static 모음 *
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

* Mocking *
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 */

import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TestConfig {
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer";

    public static String createAuthorizationHeader(String accessToken, String refreshToken) {
        return BEARER + " " + accessToken + " " + refreshToken;
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
        List<StudyCategory> categories = new ArrayList<>();
        categories.add(new StudyCategory("c++"));
        categories.add(new StudyCategory("python"));

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
                .categories(categories)
                .build();
    }

    // MaximumMember가 10보다 클 때, generateStudyInfoRegisterRequest 생성 해주는 메소드
    public static StudyInfoRegisterRequest generateStudyInfoRegisterRequestWhenMaximumMemberExceed10(Long userId) {
        List<StudyCategory> categories = new ArrayList<>();
        categories.add(new StudyCategory("c++"));
        categories.add(new StudyCategory("python"));

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
                .categories(categories)
                .build();
    }

    // MaximumMember가 1보다 작을 때, generateStudyInfoRegisterRequest 생성 해주는 메소드
    public static StudyInfoRegisterRequest generateStudyInfoRegisterRequestWhenMaximumMemberLessThan1(Long userId) {
        List<StudyCategory> categories = new ArrayList<>();
        categories.add(new StudyCategory("c++"));
        categories.add(new StudyCategory("python"));

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
                .categories(categories)
                .build();
    }
}

