package com.example.backend.study.api.service.info;

import com.example.backend.auth.TestConfig;
import com.example.backend.common.exception.study.info.StudyInfoException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.study.api.service.info.StudyInfoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    }
    @Test
    @DisplayName("하나의 스터디 상세정보 테스트")
    void testSelectStudyInfo() {
        // given
        User user = userRepository.save(generateAuthUser());
        StudyInfo studyInfo =studyInfoRepository.save(generateStudyInfo(user.getId()));
        System.out.println(studyInfo.getId());
        // when
        Optional<StudyInfo> result = studyInfoService.selectStudyInfo(studyInfo.getId());

        // then
        // createdDateTime, modifiedDateTime에서 정밀도 오류가 나서 하드코딩 했습니다..
        // assertThat(studyInfo).usingRecursiveComparison() .isEqualTo(result.get());;

        assertTrue(result.isPresent());
        assertThat(studyInfo.getId()).isEqualTo(result.get().getId());
        assertThat(studyInfo.getTopic()).isEqualTo(result.get().getTopic());
        assertThat(studyInfo.getScore()).isEqualTo(result.get().getScore());
        assertThat(studyInfo.getEndDate()).isEqualTo(result.get().getEndDate());
        assertThat(studyInfo.getInfo()).isEqualTo(result.get().getInfo());
        assertThat(studyInfo.getStatus()).isEqualTo(result.get().getStatus());
        assertThat(studyInfo.getJoinCode()).isEqualTo(result.get().getJoinCode());
        assertThat(studyInfo.getMaximumMember()).isEqualTo(result.get().getMaximumMember());
        assertThat(studyInfo.getCurrentMember()).isEqualTo(result.get().getCurrentMember());
        assertThat(studyInfo.getLastCommitDay()).isEqualTo(result.get().getLastCommitDay());
        assertThat(studyInfo.getProfileImageUrl()).isEqualTo(result.get().getProfileImageUrl());
        assertThat(studyInfo.getRepositoryInfo()).usingRecursiveComparison().isEqualTo(result.get().getRepositoryInfo());
        assertThat(studyInfo.getPeriodType()).isEqualTo(result.get().getPeriodType());
    }

    @Test
    @DisplayName("모든 스터디 반환 테스트")
    void testSelectStudyInfoList() {
        // given
        StudyInfo studyInfo1 = generateStudyInfo(1L);
        StudyInfo studyInfo2 = generateStudyInfo(1L);
        studyInfoRepository.save(studyInfo1);
        studyInfoRepository.save(studyInfo2);

        // when
        List<StudyInfo> result = studyInfoService.selectStudyInfoList();

        // then
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("StudyInfo 등록 테스트")
    void testRegisterStudy() {
        // given
        StudyInfo studyInfo = generateStudyInfo(1L);

        // when
        StudyInfo registeredStudy = studyInfoService.registerStudy(studyInfo);

        // then
        assertNotNull(registeredStudy.getId(), "스터디 등록 후 ID는 null이 아니어야 합니다.");
        assertThat(studyInfo.getTopic()).isEqualTo(registeredStudy.getTopic());
        assertThat(studyInfo.getScore()).isEqualTo(registeredStudy.getScore());
        assertThat(studyInfo.getEndDate()).isEqualTo(registeredStudy.getEndDate());
        assertThat(studyInfo.getInfo()).isEqualTo(registeredStudy.getInfo());
        assertThat(studyInfo.getStatus()).isEqualTo(registeredStudy.getStatus());
        assertThat(studyInfo.getJoinCode()).isEqualTo(registeredStudy.getJoinCode());
        assertThat(studyInfo.getMaximumMember()).isEqualTo(registeredStudy.getMaximumMember());
        assertThat(studyInfo.getCurrentMember()).isEqualTo(registeredStudy.getCurrentMember());
        assertThat(studyInfo.getLastCommitDay()).isEqualTo(registeredStudy.getLastCommitDay());
        assertThat(studyInfo.getProfileImageUrl()).isEqualTo(registeredStudy.getProfileImageUrl());
        assertThat(studyInfo.getRepositoryInfo()).usingRecursiveComparison().isEqualTo(registeredStudy.getRepositoryInfo());
        assertThat(studyInfo.getPeriodType()).isEqualTo(registeredStudy.getPeriodType());
    }
    @Test
    @DisplayName("StudyInfo 삭제 테스트")
    void testDeleteStudy() {
        // given
        StudyInfo studyInfo3 = generateStudyInfo(1L);
        studyInfoRepository.save(studyInfo3);

        // when
        boolean isDeleted = studyInfoService.deleteStudy(1L);

        // then
        assertTrue(isDeleted);
    }
    @Test
    @DisplayName("StudyInfo 삭제 예외 테스트")
    void testDeleteStudyException() {
        // given - 이미 존재하지 않는 studyInfoId를 사용하여 테스트
        Long nonExistingStudyInfoId = 999L;

        // when
        assertThrows(StudyInfoException.class, () -> studyInfoService.deleteStudy(nonExistingStudyInfoId),
                "데이터베이스에서 스터디정보를 찾을 수 없습니다.");
    }

}