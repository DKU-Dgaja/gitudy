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
import com.example.backend.study.api.controller.info.response.MyStudyInfoListAndCursorIdxResponse;
import com.example.backend.study.api.controller.info.response.MyStudyInfoListResponse;
import com.example.backend.study.api.controller.info.response.StudyInfoRegisterResponse;
import com.example.backend.study.api.controller.info.response.UpdateStudyInfoPageResponse;
import com.example.backend.study.api.service.info.response.UserNameAndProfileImageResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
    private final static String SORTBY = "score";
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
        User MyUser = userRepository.save(UserFixture.generateAuthUserByPlatformId("a"));
        User otherUser = userRepository.save(UserFixture.generateAuthUserByPlatformId("b"));
        // 스터디 생성
        List<StudyInfo> studyInfos = new ArrayList<>();
        studyInfos.add(generateStudyInfo(MyUser.getId()));
        studyInfos.add(generateStudyInfo(MyUser.getId()));
        studyInfos.add(generateStudyInfo(MyUser.getId()));
        studyInfos.add(generateStudyInfo(otherUser.getId()));
        studyInfos.add(generateStudyInfo(otherUser.getId()));
        studyInfoRepository.saveAll(studyInfos);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos));
        // when
        MyStudyInfoListAndCursorIdxResponse response = studyInfoService.selectMyStudyInfoList(MyUser.getId(), null, LIMIT, SORTBY);

        // response.getStudyInfoList()를 id순으로 정렬
        response.getStudyInfoList().sort(Comparator.comparingLong(MyStudyInfoListResponse::getId));

        // then
        IntStream.range(0, expectedResponseSize)
                .forEach(i -> {
                    StudyInfo expected = studyInfos.get(i);
                    MyStudyInfoListResponse actual = response.getStudyInfoList().get(i);

                    assertAll(
                            () -> assertEquals(expected.getId(), actual.getId()),
                            () -> assertEquals(expected.getUserId(), actual.getUserId()),
                            () -> assertEquals(expected.getTopic(), actual.getTopic()),
                            () -> assertEquals(expected.getScore(), actual.getScore()),
                            () -> assertEquals(expected.getInfo(), actual.getInfo()),
                            () -> assertEquals(expected.getMaximumMember(), actual.getMaximumMember()),
                            () -> assertEquals(expected.getCurrentMember(), actual.getCurrentMember()),
                            () -> assertEquals(expected.getLastCommitDay(), actual.getLastCommitDay()),
                            () -> assertEquals(expected.getProfileImageUrl(), actual.getProfileImageUrl()),
                            () -> assertEquals(expected.getPeriodType(), actual.getPeriodType())
                    );
                });
    }

    @Test
    public void 마이스터디_조회_테스트_스터디_카테고리_name_반환_테스트() {
        // given
        int expectedTeamACategorySize = 4;
        int expectedTeamBCategorySize = 3;
        // My 스터디 A 생성
        User myUser = userRepository.save(UserFixture.generateAuthUserByPlatformId("a"));
        StudyInfo myStudyA = studyInfoRepository.save(generateStudyInfo(myUser.getId()));
        List<StudyCategory> myStudyCategoriesA = studyCategoryRepository.saveAll(createDefaultPublicStudyCategories(expectedTeamACategorySize));
        studyCategoryMappingRepository.saveAll(StudyCategoryMappingFixture.generateStudyCategoryMappings(myStudyA, myStudyCategoriesA));
        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(myUser.getId(), myStudyA.getId()));

        // My 스터디 B 생성
        StudyInfo myStudyB = studyInfoRepository.save(generateStudyInfo(myUser.getId()));
        List<StudyCategory> myStudyCategoriesB = studyCategoryRepository.saveAll(createDefaultPublicStudyCategories(expectedTeamBCategorySize));
        studyCategoryMappingRepository.saveAll(StudyCategoryMappingFixture.generateStudyCategoryMappings(myStudyB, myStudyCategoriesB));
        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(myUser.getId(), myStudyB.getId()));

        // other 스터디 생성
        User otherUser = userRepository.save(UserFixture.generateAuthUserByPlatformId("b"));
        StudyInfo otherStudyInfo = studyInfoRepository.save(generateStudyInfo(otherUser.getId()));
        List<StudyCategory> otherStudyCategories = studyCategoryRepository.saveAll(createDefaultPublicStudyCategories(CATEGORY_SIZE));
        studyCategoryMappingRepository.saveAll(StudyCategoryMappingFixture.generateStudyCategoryMappings(otherStudyInfo, otherStudyCategories));
        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(otherUser.getId(), otherStudyInfo.getId()));

        // when
        MyStudyInfoListAndCursorIdxResponse response = studyInfoService.selectMyStudyInfoList(myUser.getId(), null, LIMIT, SORTBY);
        Map<Long, List<String>> studyCategoryMappingMap = response.getStudyCategoryMappingMap();

        // then
        assertEquals(studyCategoryMappingMap.size(), 2);
        // My 스터디 A 검증
        assertEquals(studyCategoryMappingMap.get(myStudyA.getId()).size(), expectedTeamACategorySize);
        for (int i = 0; i < studyCategoryMappingMap.get(myStudyA.getId()).size(); i++) {
            assertEquals(studyCategoryMappingMap.get(myStudyA.getId()).get(i), myStudyCategoriesA.get(expectedTeamACategorySize - i - 1).getName());
        }

        // My 스터디 B 검증
        assertEquals(studyCategoryMappingMap.get(myStudyB.getId()).size(), expectedTeamBCategorySize);
        for (int i = 0; i < studyCategoryMappingMap.get(myStudyB.getId()).size(); i++) {
            assertEquals(studyCategoryMappingMap.get(myStudyB.getId()).get(i), myStudyCategoriesB.get(expectedTeamBCategorySize - i - 1).getName());
        }
    }


    @Test
    public void 마이스터디_조회_테스트_스터디_멤버_정보_반환_테스트() {
        // given
        int expectedTeamASize = 3;
        int expectedTeamBSize = 4;

        User myLeaderUser = userRepository.save(UserFixture.generateAuthUserByPlatformId("a"));
        User otherLeaderUser = userRepository.save(UserFixture.generateAuthUserByPlatformId("b"));
        User user1 = userRepository.save(UserFixture.generateAuthUserByPlatformId("c"));
        User user2 = userRepository.save(UserFixture.generateAuthUserByPlatformId("d"));
        User user3 = userRepository.save(UserFixture.generateAuthUserByPlatformId("e"));
        User user4 = userRepository.save(UserFixture.generateAuthUserByPlatformId("f"));

        // StudyInfo 생성
        StudyInfo myStudyA = studyInfoRepository.save(generateStudyInfo(myLeaderUser.getId()));
        StudyInfo myStudyB = studyInfoRepository.save(generateStudyInfo(myLeaderUser.getId()));
        StudyInfo OtherStudy = studyInfoRepository.save(generateStudyInfo(otherLeaderUser.getId()));

        // myStudyMemberA 생성
        List<StudyMember> studyMembers = new ArrayList<>();
        studyMembers.add(StudyMemberFixture.createStudyMemberLeader(myLeaderUser.getId(), myStudyA.getId()));
        studyMembers.add(StudyMemberFixture.createDefaultStudyMember(user1.getId(), myStudyA.getId()));
        studyMembers.add(StudyMemberFixture.createDefaultStudyMember(user2.getId(), myStudyA.getId()));


        // myStudyMemberB 생성
        studyMembers.add(StudyMemberFixture.createStudyMemberLeader(myLeaderUser.getId(), myStudyB.getId()));
        studyMembers.add(StudyMemberFixture.createDefaultStudyMember(user2.getId(), myStudyB.getId()));
        studyMembers.add(StudyMemberFixture.createDefaultStudyMember(user3.getId(), myStudyB.getId()));
        studyMembers.add(StudyMemberFixture.createDefaultStudyMember(user4.getId(), myStudyB.getId()));

        // otherStudyMember 생성
        studyMembers.add(StudyMemberFixture.createStudyMemberLeader(otherLeaderUser.getId(), OtherStudy.getId()));
        studyMembers.add(StudyMemberFixture.createDefaultStudyMember(user1.getId(), OtherStudy.getId()));

        studyMembers.add(StudyMemberFixture.createDefaultStudyMember(user3.getId(), OtherStudy.getId()));
        studyMemberRepository.saveAll(studyMembers);

        // when
        MyStudyInfoListAndCursorIdxResponse response = studyInfoService.selectMyStudyInfoList(myLeaderUser.getId(), null, LIMIT, SORTBY);
        Map<Long, List<UserNameAndProfileImageResponse>> studyUserInfoMap = response.getStudyUserInfoMap();

        // then
        // 내 스터디 정보 검증
        Long myStudyAId = myStudyA.getId();
        List<UserNameAndProfileImageResponse> myStudyAUserList = studyUserInfoMap.get(myStudyAId);
        assertEquals(expectedTeamASize, myStudyAUserList.size());
//        for(int i=0;i<myStudyAUserList.size();i++){
//            System.out.println(myStudyAUserList.get(i).getId());
//        }

        Long myStudyBId = myStudyB.getId();
        List<UserNameAndProfileImageResponse> myStudyBUserList = studyUserInfoMap.get(myStudyBId);
        assertEquals(expectedTeamBSize, myStudyBUserList.size());
//        for(int i=0;i<myStudyBUserList.size();i++){
//            System.out.println(myStudyBUserList.get(i).getId());
//        }
    }

    @Test
    void lastCommitDay_기준_정렬된_마이_스터디_조회_테스트() {
        // given
        String sortBy = "lastCommitDay";
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        List<StudyInfo> studyInfos = createDefaultStudyInfoListRandomScoreAndLastCommitDay(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos));

        // when
        MyStudyInfoListAndCursorIdxResponse response = studyInfoService.selectMyStudyInfoList(savedUser.getId(), null, LIMIT, sortBy);

        assertEquals(LIMIT, response.getStudyInfoList().size());
        LocalDate previousCommitDay = null;
        for (MyStudyInfoListResponse studyInfo : response.getStudyInfoList()) {
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
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos));
        // when
        MyStudyInfoListAndCursorIdxResponse response = studyInfoService.selectMyStudyInfoList(savedUser.getId(), null, LIMIT, sortBy);

        // then
        assertEquals(LIMIT, response.getStudyInfoList().size());

        int previousScore = Integer.MAX_VALUE;
        for (MyStudyInfoListResponse studyInfo : response.getStudyInfoList()) {
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
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(studyInfos));

        // when
        MyStudyInfoListAndCursorIdxResponse response = studyInfoService.selectMyStudyInfoList(savedUser.getId(), null, LIMIT, sortBy);

        // then
        assertEquals(LIMIT, response.getStudyInfoList().size());

        LocalDateTime previousCreatedDateTime = response.getStudyInfoList().get(0).getCreatedDateTime();
        for (MyStudyInfoListResponse studyInfo : response.getStudyInfoList()) {
            LocalDateTime currentCreatedDateTime = studyInfo.getCreatedDateTime();
            assertTrue(currentCreatedDateTime.compareTo(previousCreatedDateTime) <= 0);
            previousCreatedDateTime = currentCreatedDateTime;
        }
    }

    @Test
    void 스코어_정렬로_스터디조회_커서기반_페이지네이션_중복_데이터_누락_제거_테스트() {
        // [문제]
        // score: 100
        // score: 70
        // score: 50
        // score: 40
        // score: 30
        // score: 30 <- cursorIdx
        // score: 30
        // score: 20 <- 여기부터 조회됨
        // score: 10
        // score: 5
        // 중복 데이터 누락 발생!

        // [해결]
        // score가 30으로 동일한 스터디가 3개가 있을 때 데이터 누락 되면 안되는 테스트
        // 데이터가 누락 안되게 올바른 cursorIdx가 반환되는지 Test한다.

        // given
        String sortBy = "score";
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        List<StudyInfo> list = new ArrayList<>();
        StudyInfo score100 = testSortScoreStudyCursorPaginationWithoutMissingData(savedUser.getId(), 100);
        StudyInfo score70 = testSortScoreStudyCursorPaginationWithoutMissingData(savedUser.getId(), 70);
        StudyInfo score50 = testSortScoreStudyCursorPaginationWithoutMissingData(savedUser.getId(), 50);
        StudyInfo score40 = testSortScoreStudyCursorPaginationWithoutMissingData(savedUser.getId(), 40);
        StudyInfo score30_1 = testSortScoreStudyCursorPaginationWithoutMissingData(savedUser.getId(), 30);
        StudyInfo score30_2 = testSortScoreStudyCursorPaginationWithoutMissingData(savedUser.getId(), 30);
        StudyInfo score30_3 = testSortScoreStudyCursorPaginationWithoutMissingData(savedUser.getId(), 30);
        StudyInfo score20 = testSortScoreStudyCursorPaginationWithoutMissingData(savedUser.getId(), 20);
        StudyInfo score10 = testSortScoreStudyCursorPaginationWithoutMissingData(savedUser.getId(), 10);
        StudyInfo score5 = testSortScoreStudyCursorPaginationWithoutMissingData(savedUser.getId(), 5);


        list.add(score100);list.add(score70);list.add(score50);list.add(score40);list.add(score30_1);
        list.add(score30_2);list.add(score30_3);list.add(score20);list.add(score10);list.add(score5);

        studyInfoRepository.saveAll(list);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(list));
        // when
        MyStudyInfoListAndCursorIdxResponse response1 = studyInfoService.selectMyStudyInfoList(savedUser.getId()
                , null
                , LIMIT
                , sortBy);

        List<MyStudyInfoListResponse> studyInfoList1 = response1.getStudyInfoList();
        System.out.println("---------After sort by Score----------");
        for(MyStudyInfoListResponse x: studyInfoList1){
            System.out.println("cursorIdx : "+ x.getId()+"  score : "+x.getScore());
        }
        System.out.println("--------------------------------------");

        // when
        MyStudyInfoListAndCursorIdxResponse response2 = studyInfoService.selectMyStudyInfoList(savedUser.getId()
                , score50.getId()
                , 3L
                , sortBy);
        List<MyStudyInfoListResponse> studyInfoList2 = response2.getStudyInfoList();
        System.out.println("-------------------------------------------");
        System.out.println("request ->[cursorIdx : " + score50.getId() + ", limit : 3]");
        for(MyStudyInfoListResponse x: studyInfoList2){
            System.out.println("cursorIdx : "+ x.getId()+"  score : "+x.getScore());
        }
        System.out.println("response ->[cursorIdx : " + response2.getCursorIdx() +"]");
        System.out.println("--------------------------------------------");

        // then
        assertEquals(response2.getCursorIdx(), score30_2.getId());

        // when
        MyStudyInfoListAndCursorIdxResponse response3 = studyInfoService.selectMyStudyInfoList(savedUser.getId()
                , response2.getCursorIdx()
                , 3L
                , sortBy);
        List<MyStudyInfoListResponse> studyInfoList3 = response3.getStudyInfoList();
        System.out.println("-------------------------------------------");
        System.out.println("request ->[cursorIdx : " +response2.getCursorIdx() + ", limit : 3]");
        for(MyStudyInfoListResponse x: studyInfoList3){
            System.out.println("cursorIdx : "+ x.getId()+"  score : "+x.getScore());
        }
        System.out.println("response ->[cursorIdx : " + response3.getCursorIdx() +"]");
        System.out.println("--------------------------------------------");

        // then
        assertEquals(response3.getCursorIdx(), score10.getId());
    }
    @Test
    void 마지막_커밋_정렬로_스터디조회_커서기반_페이지네이션_중복_데이터_누락_제거_테스트() {
        // [문제]
        // 중복 데이터 누락 발생!

        // [해결]
        // lastCommitDay가 동일한 스터디가 여러 개 있을 때 데이터 누락이 발생하지 않는지 테스트
        // 데이터가 누락되지 않게 올바른 cursorIdx가 반환되는지 테스트

        // given
        String sortBy = "lastCommitDay";
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        List<StudyInfo> list = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        // 시간을 조정하여 스터디 정보 생성
        StudyInfo studyInfo1 = testSortLastCommitDayStudyCursorPaginationWithoutMissingData(savedUser.getId(), currentDate);
        StudyInfo studyInfo2 = testSortLastCommitDayStudyCursorPaginationWithoutMissingData(savedUser.getId(), currentDate.minusDays(1));
        StudyInfo studyInfo3 = testSortLastCommitDayStudyCursorPaginationWithoutMissingData(savedUser.getId(), currentDate.minusDays(2));
        StudyInfo studyInfo4 = testSortLastCommitDayStudyCursorPaginationWithoutMissingData(savedUser.getId(), currentDate.minusDays(3));
        StudyInfo studyInfo5 = testSortLastCommitDayStudyCursorPaginationWithoutMissingData(savedUser.getId(), currentDate.minusDays(4));
        StudyInfo studyInfo6 = testSortLastCommitDayStudyCursorPaginationWithoutMissingData(savedUser.getId(), currentDate.minusDays(4));
        StudyInfo studyInfo7 = testSortLastCommitDayStudyCursorPaginationWithoutMissingData(savedUser.getId(), currentDate.minusDays(4));
        StudyInfo studyInfo8 = testSortLastCommitDayStudyCursorPaginationWithoutMissingData(savedUser.getId(), currentDate.minusDays(5));
        StudyInfo studyInfo9 = testSortLastCommitDayStudyCursorPaginationWithoutMissingData(savedUser.getId(), currentDate.minusDays(6));
        StudyInfo studyInfo10 = testSortLastCommitDayStudyCursorPaginationWithoutMissingData(savedUser.getId(), currentDate.minusDays(7));

        list.add(studyInfo1); list.add(studyInfo2); list.add(studyInfo3); list.add(studyInfo4); list.add(studyInfo5);
        list.add(studyInfo6); list.add(studyInfo7); list.add(studyInfo8); list.add(studyInfo9); list.add(studyInfo10);

        studyInfoRepository.saveAll(list);
        studyMemberRepository.saveAll(StudyMemberFixture.createDefaultStudyMemberList(list));

        // when
        MyStudyInfoListAndCursorIdxResponse response1 = studyInfoService.selectMyStudyInfoList(savedUser.getId(), null, LIMIT, sortBy);
        List<MyStudyInfoListResponse> studyInfoList1 = response1.getStudyInfoList();
        System.out.println("---------lastCommitDay 기준으로 정렬 후----------");
        for (MyStudyInfoListResponse x : studyInfoList1) {
            System.out.println("cursorIdx : " + x.getId() + "  lastCommitDay : " + x.getLastCommitDay());
        }
        System.out.println("--------------------------------------");

        // when
        MyStudyInfoListAndCursorIdxResponse response2
                = studyInfoService.selectMyStudyInfoList(savedUser.getId(), studyInfo3.getId(), 3L, sortBy);
        List<MyStudyInfoListResponse> studyInfoList2 = response2.getStudyInfoList();
        System.out.println("-------------------------------------------");
        System.out.println("request ->[cursorIdx : " + studyInfo3.getId() + ", limit : 3]");
        for (MyStudyInfoListResponse x : studyInfoList2) {
            System.out.println("cursorIdx : " + x.getId() + "  lastCommitDay : " + x.getLastCommitDay());
        }
        System.out.println("response ->[cursorIdx : " + response2.getCursorIdx() + "]");
        System.out.println("--------------------------------------------");

        // then
        assertEquals(response2.getCursorIdx(), studyInfo6.getId());

        // when
        MyStudyInfoListAndCursorIdxResponse response3
                = studyInfoService.selectMyStudyInfoList(savedUser.getId(), response2.getCursorIdx(), 3L, sortBy);
        List<MyStudyInfoListResponse> studyInfoList3 = response3.getStudyInfoList();
        System.out.println("-------------------------------------------");
        System.out.println("request ->[cursorIdx : " + response2.getCursorIdx() + ", limit : 3]");
        for (MyStudyInfoListResponse x : studyInfoList3) {
            System.out.println("cursorIdx : " + x.getId() + "  lastCommitDay : " + x.getLastCommitDay());
        }
        System.out.println("response ->[cursorIdx : " + response3.getCursorIdx() + "]");
        System.out.println("--------------------------------------------");

        // then
        assertEquals(response3.getCursorIdx(), studyInfo9.getId());
    }
}