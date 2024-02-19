package com.example.backend.study.api.service.info;

import com.example.backend.auth.TestConfig;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.StudyCategory.mapping.StudyCategoryMappingFixture;
import com.example.backend.domain.define.study.category.info.StudyCategory;
import com.example.backend.domain.define.study.category.info.repository.StudyCategoryRepository;
import com.example.backend.domain.define.study.category.mapping.StudyCategoryMapping;
import com.example.backend.domain.define.study.category.mapping.repository.StudyCategoryMappingRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.info.request.StudyInfoRegisterRequest;
import com.example.backend.study.api.controller.info.request.StudyInfoUpdateRequest;
import com.example.backend.study.api.controller.info.response.StudyInfoRegisterResponse;
import com.example.backend.study.api.controller.info.response.UpdateStudyInfoPageResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static com.example.backend.domain.define.study.StudyCategory.info.StudyCategoryFixture.CATEGORY_SIZE;
import static com.example.backend.domain.define.study.StudyCategory.info.StudyCategoryFixture.createDefaultPublicStudyCategories;
import static com.example.backend.domain.define.study.info.StudyInfo.JOIN_CODE_LENGTH;
import static com.example.backend.domain.define.study.info.StudyInfoFixture.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class StudyInfoServiceTest extends TestConfig {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudyInfoService studyInfoService;
    @Autowired
    private StudyInfoRepository studyInfoRepository;
    @Autowired
    private StudyCategoryMappingRepository studyCategoryMappingRepository;
    @Autowired
    private StudyMemberRepository studyMemberRepository;
    @Autowired
    private StudyCategoryRepository studyCategoryRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyCategoryMappingRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
        studyCategoryRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
    }

    @Test
    void StudyInfo_등록_테스트() {
        // given
        User user = userRepository.save(generateAuthUser());

        List<StudyCategory> studyCategories = studyCategoryRepository.saveAll(createDefaultPublicStudyCategories(CATEGORY_SIZE));

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
    public void 스터디_수정_테스트() {
        // given

        // 스터디, 유저 생성
        User user = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(generateStudyInfo(user.getId()));
        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(user.getId(), studyInfo.getId()));

        // 카테고리, 카테고리 매핑 생성
        List<StudyCategory> studyCategories = studyCategoryRepository.saveAll(createDefaultPublicStudyCategories(CATEGORY_SIZE));
        studyCategoryMappingRepository.saveAll(StudyCategoryMappingFixture.generateStudyCategoryMappings(studyInfo, studyCategories));

        // when
        List<StudyCategory> updatedStudyCategories = studyCategoryRepository.saveAll(createDefaultPublicStudyCategories(CATEGORY_SIZE));
        StudyInfoUpdateRequest studyInfoUpdateRequest = generateUpdatedStudyInfoUpdateRequestWithCategory(user.getId(), updatedStudyCategories);
        studyInfoService.updateStudyInfo(studyInfoUpdateRequest, studyInfo.getId());

        // then
        Optional<StudyInfo> updatedStudyInfo = studyInfoRepository.findById(studyInfo.getId());

        // 스터디 정보 업데이트 확인
        assertAll(
                // 업데이트한 값으로 바뀌었는 지 확인
                () -> assertEquals(studyInfoUpdateRequest.getUserId(), updatedStudyInfo.get().getUserId()),
                () -> assertEquals(studyInfoUpdateRequest.getTopic(), updatedStudyInfo.get().getTopic()),
                () -> assertEquals(studyInfoUpdateRequest.getEndDate(), updatedStudyInfo.get().getEndDate()),
                () -> assertEquals(studyInfoUpdateRequest.getInfo(), updatedStudyInfo.get().getInfo()),
                () -> assertEquals(studyInfoUpdateRequest.getStatus(), updatedStudyInfo.get().getStatus()),
                () -> assertEquals(studyInfoUpdateRequest.getMaximumMember(), updatedStudyInfo.get().getMaximumMember()),
                () -> assertEquals(studyInfoUpdateRequest.getProfileImageUrl(), updatedStudyInfo.get().getProfileImageUrl()),
                () -> assertEquals(studyInfoUpdateRequest.getRepositoryInfo().getBranchName(), updatedStudyInfo.get().getRepositoryInfo().getBranchName()),
                () -> assertEquals(studyInfoUpdateRequest.getRepositoryInfo().getName(), updatedStudyInfo.get().getRepositoryInfo().getName()),
                () -> assertEquals(studyInfoUpdateRequest.getRepositoryInfo().getOwner(), updatedStudyInfo.get().getRepositoryInfo().getOwner()),
                () -> assertEquals(studyInfoUpdateRequest.getPeriodType(), updatedStudyInfo.get().getPeriodType()),

                // 바뀌지 않아야하는 값들이 바뀌지 않았는지 확인
                () -> assertEquals(studyInfo.getScore(), updatedStudyInfo.get().getScore()),
                () -> assertEquals(studyInfo.getJoinCode(), updatedStudyInfo.get().getJoinCode()),
                () -> assertEquals(studyInfo.getCurrentMember(), updatedStudyInfo.get().getCurrentMember())
        );

        // 카테고리 매핑 업데이트 확인
        List<StudyCategoryMapping> updatedStudyCategoryMappings = studyCategoryMappingRepository.findAll();
        assertEquals(studyInfoUpdateRequest.getCategoriesId().size(), updatedStudyCategoryMappings.size());

        IntStream.range(0, studyInfoUpdateRequest.getCategoriesId().size())
                .forEach(i -> assertEquals(studyInfoUpdateRequest.getCategoriesId().get(i), updatedStudyCategoryMappings.get(i).getStudyCategoryId()));
    }


    @Test
    public void 스터디_수정_페이지_요청_메소드_테스트() {
        // given

        // 스터디, 유저 생성
        User user = userRepository.save(generateAuthUser());
        StudyInfo savedStudyInfo = studyInfoRepository.save(generateStudyInfo(user.getId()));
        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(user.getId(), savedStudyInfo.getId()));

        // 카테고리, 카테고리 매핑 생성
        List<StudyCategory> studyCategories = studyCategoryRepository.saveAll(createDefaultPublicStudyCategories(CATEGORY_SIZE));
        studyCategoryMappingRepository.saveAll(StudyCategoryMappingFixture.generateStudyCategoryMappings(savedStudyInfo, studyCategories));

        // when
        UpdateStudyInfoPageResponse response = studyInfoService.updateStudyInfoPage(savedStudyInfo.getId());

        // then
        assertAll(
                () -> assertEquals(savedStudyInfo.getUserId(), response.getUserId()),
                () -> assertEquals(savedStudyInfo.getTopic(), response.getTopic()),
                () -> assertEquals(savedStudyInfo.getEndDate(), response.getEndDate()),
                () -> assertEquals(savedStudyInfo.getInfo(), response.getInfo()),
                () -> assertEquals(savedStudyInfo.getStatus(), response.getStatus()),
                () -> assertEquals(savedStudyInfo.getJoinCode(), response.getJoinCode()),
                () -> assertEquals(savedStudyInfo.getMaximumMember(), response.getMaximumMember()),
                () -> assertEquals(savedStudyInfo.getProfileImageUrl(), response.getProfileImageUrl()),
                () -> assertEquals(savedStudyInfo.getRepositoryInfo().getBranchName(), response.getRepositoryInfo().getBranchName()),
                () -> assertEquals(savedStudyInfo.getRepositoryInfo().getName(), response.getRepositoryInfo().getName()),
                () -> assertEquals(savedStudyInfo.getRepositoryInfo().getOwner(), response.getRepositoryInfo().getOwner()),
                () -> assertEquals(savedStudyInfo.getPeriodType(), response.getPeriodType())
        );

        // 카테고리 매핑 response 확인
        List<StudyCategoryMapping> savedStudyCategoryMappings = studyCategoryMappingRepository.findAll();
        assertEquals(savedStudyCategoryMappings.size(), response.getCategoriesId().size());

        IntStream.range(0, response.getCategoriesId().size())
                .forEach(i -> assertEquals(savedStudyCategoryMappings.get(i).getStudyCategoryId(), response.getCategoriesId().get(i)));
    }
}