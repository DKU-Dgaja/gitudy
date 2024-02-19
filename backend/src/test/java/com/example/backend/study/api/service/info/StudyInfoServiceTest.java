package com.example.backend.study.api.service.info;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.study.StudyInfoException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.category.info.repository.StudyCategoryRepository;
import com.example.backend.domain.define.study.category.mapping.StudyCategoryMapping;
import com.example.backend.domain.define.study.category.mapping.repository.StudyCategoryMappingRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.constant.StudyMemberStatus;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.response.StudyInfoRegisterResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static com.example.backend.domain.define.study.StudyCategory.StudyCategoryFixture.CATEGORY_SIZE;
import static com.example.backend.domain.define.study.StudyCategory.StudyCategoryFixture.createDefaultPublicStudyCategories;
import static com.example.backend.domain.define.study.info.StudyInfo.JOIN_CODE_LENGTH;
import static com.example.backend.domain.define.study.info.StudyInfoFixture.generateStudyInfo;
import static com.example.backend.domain.define.study.info.StudyInfoFixture.generateStudyInfoRegisterRequestWithCategory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class StudyInfoServiceTest extends TestConfig {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudyInfoService studyInfoService;
    @Autowired
    private StudyCategoryMappingRepository studyCategoryMappingRepository;
    @Autowired
    private StudyMemberRepository studyMemberRepository;
    @Autowired
    private StudyCategoryRepository studyCategoryRepository;
    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyCategoryMappingRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
        studyCategoryRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("StudyInfo 등록 테스트")
    void testRegisterStudy() {
        // given
        User user = userRepository.save(generateAuthUser());

        List<StudyCategory> studyCategories = createDefaultPublicStudyCategories(CATEGORY_SIZE);

        studyCategoryRepository.saveAll(studyCategories);

        StudyInfoRegisterRequest studyInfoRegisterRequest = generateStudyInfoRegisterRequestWithCategory(user.getId(), studyCategories);
        // when
        StudyInfoRegisterResponse registeredStudy = studyInfoService.registerStudy(studyInfoRegisterRequest);
        List<StudyCategoryMapping> studyCategoryMapping = studyCategoryMappingRepository.findAll();
        List<StudyMember> studyMember = studyMemberRepository.findAll();

        // then

        // 스더디 등록시 멤버는 등록한 사람 한명이다
        assertEquals(studyMember.size(), 1);

        // studyCategoryMapping와 studyMember가 잘 저장 되었는지 검증
        IntStream.range(0, studyCategories.size())
                .forEach(i -> {
                    assertEquals(studyCategoryMapping.get(i).getStudyInfoId(), studyMember.get(0).getStudyInfoId());
                });

        // response가 잘 되었는지 검증
        assertAll("registeredStudy",
                () -> assertEquals(studyInfoRegisterRequest.getUserId(), registeredStudy.getUserId()),
                () -> assertEquals(studyInfoRegisterRequest.getTopic(), registeredStudy.getTopic()),
                () -> assertEquals(studyInfoRegisterRequest.getEndDate(), registeredStudy.getEndDate()),
                () -> assertEquals(studyInfoRegisterRequest.getInfo(), registeredStudy.getInfo()),
                () -> assertEquals(studyInfoRegisterRequest.getStatus(), registeredStudy.getStatus()),
                () -> assertEquals(studyInfoRegisterRequest.getMaximumMember(), registeredStudy.getMaximumMember()),
                () -> assertEquals(studyInfoRegisterRequest.getProfileImageUrl(), registeredStudy.getProfileImageUrl()),
                () -> assertEquals(studyInfoRegisterRequest.getRepositoryInfo(), registeredStudy.getRepositoryInfo()),
                () -> assertEquals(studyInfoRegisterRequest.getPeriodType(), registeredStudy.getPeriodType()),
                () -> assertIterableEquals(studyInfoRegisterRequest.getCategoriesId(), registeredStudy.getCategoriesId())
        );

        // joinCode 10자리가 잘 생성되었는지 검증
        assertEquals(registeredStudy.getJoinCode().length(), JOIN_CODE_LENGTH);
    }

    @Test
    void 삭제_성공_테스트() {
        // given

        // 유저생성
        User leaderUser = userRepository.save(UserFixture.generateAuthUserByPlatformId("a"));
        User user1 = userRepository.save(UserFixture.generateAuthUserByPlatformId("b"));
        User user2 = userRepository.save(UserFixture.generateAuthUserByPlatformId("c"));
        User user3 = userRepository.save(UserFixture.generateAuthUserByPlatformId("d"));

        // 스터디 생성
        StudyInfo studyInfo = studyInfoRepository.save(generateStudyInfo(leaderUser.getId()));

        // 스터디 멤버 생성
        List<StudyMember> studyMembers = new ArrayList<>();
        studyMembers.add(StudyMemberFixture.createStudyMemberLeader(leaderUser.getId(), studyInfo.getId()));
        studyMembers.add(StudyMemberFixture.createDefaultStudyMember(user1.getId(), studyInfo.getId()));
        studyMembers.add(StudyMemberFixture.createDefaultStudyMember(user2.getId(), studyInfo.getId()));
        studyMembers.add(StudyMemberFixture.createDefaultStudyMember(user3.getId(), studyInfo.getId()));
        studyMemberRepository.saveAll(studyMembers);

        // when
        studyInfoService.deleteStudy(studyInfo.getId());


        // then
        // 멤버는 지워지면 안된다
        List<StudyMember> withdrawalMembers = studyMemberRepository.findByStudyInfoId(studyInfo.getId());
        assertThat(withdrawalMembers).isNotNull().isNotEmpty();

        // 멤버 상태는 STUDY_WITHDRAWAL이다.
        for (StudyMember member : withdrawalMembers) {
            assertThat(member.getStatus()).isEqualTo(StudyMemberStatus.STUDY_WITHDRAWAL);
        }

        // 스터디의 상태는 STUDY_DELETED이다.
        assertEquals(studyInfoRepository.findById(studyInfo.getId()).get().getStatus(), StudyStatus.STUDY_DELETED);

    }

    @Test
    void 스터디가_없을_경우_스터디_삭제_실패_테스트() {
        Long invalidStudyInfoId = 987654321L;
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(generateStudyInfo(user.getId()));
        StudyMember studyMember = studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(user.getId(), studyInfo.getId()));

        // then
        assertThrows(StudyInfoException.class, () -> {
            studyInfoService.deleteStudy(invalidStudyInfoId);
        }, "해당 스터디정보를 찾을 수 없습니다.");
    }
}