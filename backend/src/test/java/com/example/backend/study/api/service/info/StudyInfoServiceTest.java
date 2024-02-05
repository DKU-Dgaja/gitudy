package com.example.backend.study.api.service.info;

import com.example.backend.auth.TestConfig;
import com.example.backend.common.exception.study.info.StudyInfoException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyPeriodType;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.response.AllStudyInfoResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoRegisterResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoResponse;
import com.example.backend.study.api.service.info.StudyInfoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StudyInfoServiceTest extends TestConfig {
    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyInfoService studyInfoService;
    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
    @Test
    @DisplayName("하나의 스터디 상세정보 테스트")
    void testSelectStudyInfo() {
        // given
        User user = userRepository.save(generateAuthUser());
        StudyInfo studyInfo =studyInfoRepository.save(generateStudyInfo(user.getId()));
        // when
        Optional<StudyInfoResponse> result = studyInfoService.selectStudyInfo(studyInfo.getId());

        // then
        // createdDateTime, modifiedDateTime에서 정밀도 오류가 나서 하드코딩 했습니다..
        // assertThat(studyInfo).usingRecursiveComparison() .isEqualTo(result.get());;

        assertTrue(result.isPresent());
        assertThat(studyInfo.getTopic()).isEqualTo(result.get().getTopic());
        assertThat(studyInfo.getScore()).isEqualTo(result.get().getScore());
        assertThat(studyInfo.getEndDate()).isEqualTo(result.get().getEndDate());
        assertThat(studyInfo.getInfo()).isEqualTo(result.get().getInfo());
        assertThat(studyInfo.getStatus()).isEqualTo(result.get().getStatus());
        assertThat(studyInfo.getMaximumMember()).isEqualTo(result.get().getMaximumMember());
        assertThat(studyInfo.getCurrentMember()).isEqualTo(result.get().getCurrentMember());
        assertThat(studyInfo.getProfileImageUrl()).isEqualTo(result.get().getProfileImageUrl());
        assertThat(studyInfo.getRepositoryInfo()).usingRecursiveComparison().isEqualTo(result.get().getRepositoryInfo());
        assertThat(studyInfo.getPeriodType()).isEqualTo(result.get().getPeriodType());
    }


    @Test
    @DisplayName("모든 스터디 반환 테스트")
    void testSelectStudyInfoList() {
        // given
        Long userId1= 1L;
        Long userId2= 2L;
        StudyInfo studyInfo1 = generateStudyInfo(userId1);
        StudyInfo studyInfo2 = generateStudyInfo(userId2);
        studyInfoRepository.save(studyInfo1);
        studyInfoRepository.save(studyInfo2);

        // when
        List<AllStudyInfoResponse> result = studyInfoService.selectStudyInfoList();

        // then
        assertEquals(2, result.size());
        assertThat(studyInfo1.getUserId()).isEqualTo(result.get(0).getUserId());
        assertThat(studyInfo1.getTopic()).isEqualTo(result.get(0).getTopic());
        assertThat(studyInfo1.getScore()).isEqualTo(result.get(0).getScore());
        assertThat(studyInfo1.getEndDate()).isEqualTo(result.get(0).getEndDate());
        assertThat(studyInfo1.getInfo()).isEqualTo(result.get(0).getInfo());
        assertThat(studyInfo1.getStatus()).isEqualTo(result.get(0).getStatus());
        assertThat(studyInfo1.getMaximumMember()).isEqualTo(result.get(0).getMaximumMember());
        assertThat(studyInfo1.getCurrentMember()).isEqualTo(result.get(0).getCurrentMember());
        assertThat(studyInfo1.getLastCommitDay()).isEqualTo(result.get(0).getLastCommitDay());
        assertThat(studyInfo1.getProfileImageUrl()).isEqualTo(result.get(0).getProfileImageUrl());
        assertThat(studyInfo1.getPeriodType()).isEqualTo(result.get(0).getPeriodType());

        assertThat(studyInfo2.getUserId()).isEqualTo(result.get(1).getUserId());
        assertThat(studyInfo2.getTopic()).isEqualTo(result.get(1).getTopic());
        assertThat(studyInfo2.getScore()).isEqualTo(result.get(1).getScore());
        assertThat(studyInfo2.getEndDate()).isEqualTo(result.get(1).getEndDate());
        assertThat(studyInfo2.getInfo()).isEqualTo(result.get(1).getInfo());
        assertThat(studyInfo2.getStatus()).isEqualTo(result.get(1).getStatus());
        assertThat(studyInfo2.getMaximumMember()).isEqualTo(result.get(1).getMaximumMember());
        assertThat(studyInfo2.getCurrentMember()).isEqualTo(result.get(1).getCurrentMember());
        assertThat(studyInfo2.getLastCommitDay()).isEqualTo(result.get(1).getLastCommitDay());
        assertThat(studyInfo2.getProfileImageUrl()).isEqualTo(result.get(1).getProfileImageUrl());
        assertThat(studyInfo2.getPeriodType()).isEqualTo(result.get(1).getPeriodType());

    }

    @Test
    @DisplayName("StudyInfo 등록 테스트")
    void testRegisterStudy() {
        // given
        StudyInfoRegisterRequest request = generateStudyInfoRegisterRequest(1L);

        // when
        StudyInfoRegisterResponse response = studyInfoService.registerStudy(request);

        // then
        assertThat(request.getTopic()).isEqualTo(response.getTopic());
        assertThat(request.getEndDate()).isEqualTo(response.getEndDate());
        assertThat(request.getInfo()).isEqualTo(response.getInfo());
        assertThat(request.getStatus()).isEqualTo(response.getStatus());
        assertThat(request.getJoinCode()).isEqualTo(response.getJoinCode());
        assertThat(request.getMaximumMember()).isEqualTo(response.getMaximumMember());
        assertThat(request.getProfileImageUrl()).isEqualTo(response.getProfileImageUrl());
        assertThat(request.getRepositoryInfo()).usingRecursiveComparison().isEqualTo(response.getRepositoryInfo());
        assertThat(request.getPeriodType()).isEqualTo(response.getPeriodType());
    }
    @Test
    @DisplayName("StudyInfo 삭제 테스트")
    void testDeleteStudy() {
        // given
        User user = userRepository.save(generateAuthUser());
        StudyInfo studyInfo= studyInfoRepository.save(generateStudyInfo(user.getId()));

        // when
        boolean isDeleted = studyInfoService.deleteStudy(studyInfo.getId());

        // then
        assertTrue(isDeleted);
        assertThrows(StudyInfoException.class, () -> studyInfoService.deleteStudy(studyInfo.getId()),
                "데이터베이스에서 스터디정보를 찾을 수 없습니다.");
    }
    @Test
    @DisplayName("StudyInfo 삭제 예외 테스트")
    void testDeleteStudyException() {
        // given - 이미 존재하지 않는 studyInfoId를 사용하여 테스트
        Long nonExistingStudyInfoId = 999L;

        // when, then
        assertThrows(StudyInfoException.class, () -> studyInfoService.deleteStudy(nonExistingStudyInfoId),
                "데이터베이스에서 스터디정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("스터디인원의 최대 수가 10이 넘어가면 예외가 발생한다.")
    void registerStudy_maximum_throwException() {
        int invaildMaximumMember = 100;
        StudyInfoRegisterRequest request = StudyInfoRegisterRequest.builder()
                .userId(1L)
                .topic("Sample Study")
                .endDate(LocalDate.now().plusMonths(3))
                .info("This is a sample study.")
                .status(StudyStatus.STUDY_PUBLIC)
                .joinCode(null)
                .maximumMember(invaildMaximumMember)
                .profileImageUrl("https://example.com/profile.jpg")
                .repositoryInfo(new RepositoryInfo("구영민", "aaa333", "BRANCH_NAME"))
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .build();

        // when, then
        assertThrows(StudyInfoException.class, () -> studyInfoService.registerStudy(request),
                "깃터디 최대인원 수는 10명입니다.");
    }

    @Test
    @DisplayName("스터디인원의 최대 수 음수면 예외가 발생한다.")
    void registerStudy_minimum_throwException() {
        int invaildMaximumMember = -1;
        StudyInfoRegisterRequest request = StudyInfoRegisterRequest.builder()
                .userId(1L)
                .topic("Sample Study")
                .endDate(LocalDate.now().plusMonths(3))
                .info("This is a sample study.")
                .status(StudyStatus.STUDY_PUBLIC)
                .joinCode(null)
                .maximumMember(invaildMaximumMember)
                .profileImageUrl("https://example.com/profile.jpg")
                .repositoryInfo(new RepositoryInfo("구영민", "aaa333", "BRANCH_NAME"))
                .periodType(StudyPeriodType.STUDY_PERIOD_EVERYDAY)
                .build();

        // when, then
        assertThrows(StudyInfoException.class, () -> studyInfoService.registerStudy(request),
                "깃터디 최소인원 수는 1명입니다.");
    }
}