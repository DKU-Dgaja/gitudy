package com.example.backend.study.api.service.info;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.study.StudyInfoException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.StudyCategory.mapping.StudyCategoryMappingFixture;
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
import com.example.backend.study.api.controller.info.request.StudyInfoUpdateRequest;
import com.example.backend.study.api.controller.info.response.MyStudyInfoListResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoRegisterResponse;
import com.example.backend.study.api.controller.info.response.UpdateStudyInfoPageResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static com.example.backend.domain.define.study.StudyCategory.info.StudyCategoryFixture.CATEGORY_SIZE;
import static com.example.backend.domain.define.study.StudyCategory.info.StudyCategoryFixture.createDefaultPublicStudyCategories;
import static com.example.backend.domain.define.study.info.StudyInfo.JOIN_CODE_LENGTH;
import static com.example.backend.domain.define.study.info.StudyInfoFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class StudyInfoServiceTest extends TestConfig {
    private final static int DATA_SIZE = 10;
    private final static Long LIMIT = 10L;
    private final static String sortBy = "score";
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

    @Test
    public void 마이스터디_조회_테스트() {
        // given
        int expectedResponseSize = 3;

        // 유저 생성
        User user = userRepository.save(UserFixture.generateAuthUserByPlatformId("a"));
        User other = userRepository.save(UserFixture.generateAuthUserByPlatformId("b"));
        // 스터디 생성
        List<StudyInfo> studyInfos = new ArrayList<>();
        StudyInfo savedStudyInfo1 = studyInfoRepository.save(generateStudyInfo(user.getId()));
        StudyInfo savedStudyInfo2 = studyInfoRepository.save(generateStudyInfo(user.getId()));
        StudyInfo savedStudyInfo3 = studyInfoRepository.save(generateStudyInfo(user.getId()));
        StudyInfo otherSavedStudyInfo1 = studyInfoRepository.save(generateStudyInfo(other.getId()));
        StudyInfo otherSavedStudyInfo2 = studyInfoRepository.save(generateStudyInfo(other.getId()));

        studyInfos.add(savedStudyInfo1);
        studyInfos.add(savedStudyInfo2);
        studyInfos.add(savedStudyInfo3);
        studyInfos.add(otherSavedStudyInfo1);
        studyInfos.add(otherSavedStudyInfo2);
        studyInfoRepository.saveAll(studyInfos);

        // when
        List<MyStudyInfoListResponse> response = studyInfoService.selectMyStudyInfoList(user.getId(), null, LIMIT, sortBy);

        // then
        assertEquals(expectedResponseSize, response.size());
    }

    @Test
    void lastCommitDay_기준_정렬된_마이_스터디_조회_테스트() {
        // given
        String sortBy = "lastCommitDay";
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        List<StudyInfo> studyInfos = createDefaultStudyInfoListRandomScoreAndLastCommitDay(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);

//        System.out.println("---------Before sort by lastCommitDay---------");
//        for(StudyInfo x: studyInfos){
//            System.out.println(x.getLastCommitDay());
//        }
//        System.out.println("----------------------------------------------");

        // when
        List<MyStudyInfoListResponse> studyInfoList = studyInfoService.selectMyStudyInfoList(savedUser.getId(), null, LIMIT, sortBy);

//        System.out.println("---------After sort by lastCommitDay----------");
//        for(AllStudyInfoResponse x: studyInfoList){
//            System.out.println(x.getLastCommitDay());
//        }
//        System.out.println("----------------------------------------------");


        assertEquals(LIMIT, studyInfoList.size());
        LocalDate previousCommitDay = null;
        for (MyStudyInfoListResponse studyInfo : studyInfoList) {
            LocalDate currentCommitDay = studyInfo.getLastCommitDay();
            if (previousCommitDay != null) {
                assertTrue(currentCommitDay.isBefore(previousCommitDay) || currentCommitDay.isEqual(previousCommitDay));
            }
            previousCommitDay = currentCommitDay;
        }
    }

    @Test
    void score_기준_정렬된_마이_스터디_조회_테스트() {
        // given
        String sortBy = "score";
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        List<StudyInfo> studyInfos = createDefaultStudyInfoListRandomScoreAndLastCommitDay(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);

//        System.out.println("---------Before sort by Score---------");
//        for(StudyInfo x: studyInfos){
//            System.out.println(x.getScore());
//        }
//        System.out.println("--------------------------------------");

        // when
        List<MyStudyInfoListResponse> studyInfoList = studyInfoService.selectMyStudyInfoList(savedUser.getId(), null, LIMIT, sortBy);

//        System.out.println("---------After sort by Score----------");
//        for(AllStudyInfoResponse x: studyInfoList){
//            System.out.println(x.getScore());
//        }
//        System.out.println("--------------------------------------");

        // then
        assertEquals(LIMIT, studyInfoList.size());

        int previousScore = Integer.MAX_VALUE;
        for (MyStudyInfoListResponse studyInfo : studyInfoList) {
            int currentScore = studyInfo.getScore();
            assertTrue(currentScore <= previousScore);
            previousScore = currentScore;
        }
    }

    @Test
    void createdDateTime_기준_정렬된_마이_스터디_조회_테스트() {
        // given
        String sortBy = "createdDateTime";
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        List<StudyInfo> studyInfos = createDefaultStudyInfoListRandomScoreAndLastCommitDay(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);

//        System.out.println("---------Before sort by createdDateTime---------");
//        for(StudyInfo x: studyInfos){
//            System.out.println(x.getCreatedDateTime());
//        }
//        System.out.println("--------------------------------------");

        // when
        List<MyStudyInfoListResponse> studyInfoList = studyInfoService.selectMyStudyInfoList(savedUser.getId(), null, LIMIT, sortBy);

//        System.out.println("---------After sort by createdDateTime----------");
//        for(MyStudyInfoListResponse x: studyInfoList){
//            System.out.println(x.getCreatedDateTime());
//        }
//        System.out.println("--------------------------------------");

        // then
        assertEquals(LIMIT, studyInfoList.size());

        LocalDateTime previousCreatedDateTime = studyInfoList.get(0).getCreatedDateTime();
        for (MyStudyInfoListResponse studyInfo : studyInfoList) {
            LocalDateTime currentCreatedDateTime = studyInfo.getCreatedDateTime();
            assertTrue(currentCreatedDateTime.compareTo(previousCreatedDateTime) <= 0);
            previousCreatedDateTime = currentCreatedDateTime;
        }
    }
}