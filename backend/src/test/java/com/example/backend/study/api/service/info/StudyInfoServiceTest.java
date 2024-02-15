package com.example.backend.study.api.service.info;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.api.service.jwt.JwtService;
import com.example.backend.auth.config.fixture.UserFixture;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;


import static com.example.backend.auth.config.fixture.UserFixture.*;
import static com.example.backend.domain.define.study.info.StudyInfoFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class StudyInfoServiceTest extends TestConfig {
    private final static int DATA_SIZE = 10;
    private final static Long LIMIT = 10L;
    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyInfoService studyInfoService;
    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void tearDown() {
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @AfterEach
    void tearDown1() {
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("하나의 스터디 상세정보 테스트")
    void testSelectStudyInfo() {
        // given
        User user = userRepository.save(generateAuthUser());
        StudyInfo studyInfo = studyInfoRepository.save(generateStudyInfo(user.getId()));
        // when
        Optional<StudyInfoResponse> result = studyInfoService.selectStudyInfo(studyInfo.getId());

        // then
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
        User user1 = userRepository.save(generateAuthUser());
        User user2 = userRepository.save(generateGoogleUser());
        StudyInfo studyInfo1 = generateStudyInfo(user1.getId());
        StudyInfo studyInfo2 = generateStudyInfo(user2.getId());
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
        User user = userRepository.save(generateAuthUser());
        StudyInfoRegisterRequest request = generateStudyInfoRegisterRequest(user.getId());

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
        StudyInfo studyInfo = studyInfoRepository.save(generateStudyInfo(user.getId()));

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
        User user = userRepository.save(generateAuthUser());
        StudyInfoRegisterRequest request = StudyInfoRegisterRequest.builder()
                .userId(user.getId())
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
        User user = userRepository.save(generateAuthUser());
        StudyInfoRegisterRequest request = StudyInfoRegisterRequest.builder()
                .userId(user.getId())
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

    // @Test : 테스트 어노테이션 임시 제거
    // studyInfoList 전체 테스트시 studyInfoList를 데이터 베이스에 저장할 때 strategy = GenerationType.IDENTITY로 인하여
    // 다른 테스트에서 증가한 인덱스가 그대로 적용되어 오류가 뜹니다. (단위 테스트는 성공)
    void 커서가_null이_아닌_경우_마이_스터디_조회_테스트() {
        // given
        User user = UserFixture.generateAuthUser();

        User savedUser = userRepository.save(user);
        System.out.println(savedUser.getId());
        Random random = new Random();
        Long cursorIdx = random.nextLong(LIMIT) + 1L;

        List<StudyInfo> studyInfoList = createDefaultStudyInfoList(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfoList);

        // when
        List<StudyInfoResponse> studyInfoPage = studyInfoService.selectUserStudyInfoList(savedUser.getId(), cursorIdx, LIMIT);

        // then
        assertEquals(cursorIdx <= LIMIT ? cursorIdx - 1 : LIMIT, studyInfoPage.size());
    }

    @Test
    void 커서가_null인_경우_마이_스터디_조회_테스트() {
        // given
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        List<StudyInfo> studyInfos = createDefaultStudyInfoList(DATA_SIZE, savedUser.getId());
        studyInfoRepository.saveAll(studyInfos);

        // when
        List<StudyInfoResponse> studyInfoList = studyInfoService.selectUserStudyInfoList(savedUser.getId(), null, LIMIT);

        // then
        assertEquals(LIMIT, studyInfoList.size());
    }

    @Test
    void lastCommitDay_기준_정렬된_모든_스터디_조회_테스트() {
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
        List<AllStudyInfoResponse> studyInfoList = studyInfoService.selectStudyInfoListbyParameter(savedUser.getId(), null, LIMIT, sortBy);

//        System.out.println("---------After sort by lastCommitDay----------");
//        for(AllStudyInfoResponse x: studyInfoList){
//            System.out.println(x.getLastCommitDay());
//        }
//        System.out.println("----------------------------------------------");

        // then
        assertEquals(LIMIT, studyInfoList.size());
        // then
        assertEquals(LIMIT, studyInfoList.size());
        LocalDate previousCommitDay = null;
        for (AllStudyInfoResponse studyInfo : studyInfoList) {
            LocalDate currentCommitDay = studyInfo.getLastCommitDay();
            if (previousCommitDay != null) {
                assertTrue(currentCommitDay.isBefore(previousCommitDay) || currentCommitDay.isEqual(previousCommitDay));
            }
            previousCommitDay = currentCommitDay;
        }
    }

    @Test
    void score_기준_정렬된_모든_스터디_조회_테스트() {
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
        List<AllStudyInfoResponse> studyInfoList = studyInfoService.selectStudyInfoListbyParameter(savedUser.getId(), null, LIMIT, sortBy);

//        System.out.println("---------After sort by Score----------");
//        for(AllStudyInfoResponse x: studyInfoList){
//            System.out.println(x.getScore());
//        }
//        System.out.println("--------------------------------------");


        // then
        assertEquals(LIMIT, studyInfoList.size());
        // then
        int previousScore = Integer.MAX_VALUE;
        for (AllStudyInfoResponse studyInfo : studyInfoList) {
            int currentScore = studyInfo.getScore();
            assertTrue(currentScore <= previousScore);
            previousScore = currentScore;
        }
    }

    void 스터디조회_커서기반_페이지네이션_누락_테스트() {
        // given
        String sortBy = "score";
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        List<StudyInfo> list = new ArrayList<>();
        list.add(testStudyCursorPaginationWithMissingData(savedUser.getId(), 10));
        list.add(testStudyCursorPaginationWithMissingData(savedUser.getId(), 30));
        list.add(testStudyCursorPaginationWithMissingData(savedUser.getId(), 20));
        list.add(testStudyCursorPaginationWithMissingData(savedUser.getId(), 70));
        list.add(testStudyCursorPaginationWithMissingData(savedUser.getId(), 100));

        list.add(testStudyCursorPaginationWithMissingData(savedUser.getId(), 10));
        list.add(testStudyCursorPaginationWithMissingData(savedUser.getId(), 50));
        list.add(testStudyCursorPaginationWithMissingData(savedUser.getId(), 40));
        list.add(testStudyCursorPaginationWithMissingData(savedUser.getId(), 30));
        list.add(testStudyCursorPaginationWithMissingData(savedUser.getId(), 30));
        studyInfoRepository.saveAll(list);

//        System.out.println("---------Before sort by Score---------");
//        for(StudyInfo x: list){
//            System.out.println("cursorIdx : "+ x.getId()+"  score : "+x.getScore());
//        }
//        System.out.println("--------------------------------------");

        // when
        List<AllStudyInfoResponse> studyInfoList = studyInfoService.selectStudyInfoListbyParameter(savedUser.getId(), null, LIMIT, sortBy);

//        System.out.println("---------After sort by Score----------");
//        for(AllStudyInfoResponse x: studyInfoList){
//            System.out.println("cursorIdx : "+ x.getId()+"  score : "+x.getScore());
//        }
//        System.out.println("--------------------------------------");

        // when
        List<AllStudyInfoResponse> studyInfoList1 = studyInfoService.selectStudyInfoListbyParameter(savedUser.getId(), 4L, 3L, sortBy);

//        System.out.println("---- cursorIdx : 4, limit : 3 ---------");
//        for(AllStudyInfoResponse x: studyInfoList1){
//            System.out.println("cursorIdx : "+ x.getId()+"  score : "+x.getScore());
//        }
//        System.out.println("--------------------------------------");

        // when
        List<AllStudyInfoResponse> studyInfoList2 = studyInfoService.selectStudyInfoListbyParameter(savedUser.getId(), 10L, 3L, sortBy);

//        System.out.println("---- cursorIdx : 10, limit : 3 ---------");
//        for(AllStudyInfoResponse x: studyInfoList2){
//            System.out.println("cursorIdx : "+ x.getId()+"  score : "+x.getScore());
//        }
//        System.out.println("--------------------------------------");
    }
}