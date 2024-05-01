package com.example.backend.study.api.service.convention;

import com.example.backend.TestConfig;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.convention.ConventionException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.convention.StudyConvention;
import com.example.backend.domain.define.study.convention.StudyConventionFixture;
import com.example.backend.domain.define.study.convention.repository.StudyConventionRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.convention.request.StudyConventionRequest;
import com.example.backend.study.api.controller.convention.request.StudyConventionUpdateRequest;
import com.example.backend.study.api.controller.convention.response.StudyConventionListAndCursorIdxResponse;
import com.example.backend.study.api.service.member.StudyMemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.junit.jupiter.api.Assertions.*;

public class StudyConventionServiceTest extends TestConfig {

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyConventionRepository studyConventionRepository;

    @Autowired
    private StudyMemberRepository studyMemberRepository;

    @Autowired
    private StudyConventionService studyConventionService;

    @Autowired
    private StudyMemberService studyMemberService;

    private final static Long LIMIT = 4L;


    @AfterEach
    void tearDown() {
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        studyConventionRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("컨벤션 등록 테스트")
    public void registerConvention() {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember leader = StudyMemberFixture.createStudyMemberLeader(savedUser.getId(), studyInfo.getId());
        studyMemberRepository.save(leader);

        StudyConventionRequest request = StudyConventionFixture.generateStudyConventionRequest();

        //when
        studyMemberService.isValidateStudyLeader(savedUser, studyInfo.getId());
        studyConventionService.registerStudyConvention(request, studyInfo.getId());
        StudyConvention findConvention = studyConventionRepository.findByStudyInfoId(studyInfo.getId());

        //then
        assertEquals("컨벤션", findConvention.getName());
        assertEquals("설명", findConvention.getDescription());
        assertEquals("정규식", findConvention.getContent());

    }

    @Test
    @DisplayName("컨벤션 수정 테스트")
    public void updateStudyConvention() {
        //given
        User savedUser = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember leader = StudyMemberFixture.createStudyMemberLeader(savedUser.getId(), studyInfo.getId());
        studyMemberRepository.save(leader);

        StudyConvention studyConvention = StudyConventionFixture.createStudyDefaultConvention(studyInfo.getId());
        studyConventionRepository.save(studyConvention);

        StudyConventionUpdateRequest updateRequest = StudyConventionFixture.generateStudyConventionUpdateRequest();

        //when
        studyMemberService.isValidateStudyLeader(savedUser, studyInfo.getId());
        studyConventionService.updateStudyConvention(updateRequest, studyConvention.getId());
        StudyConvention updateConvention = studyConventionRepository.findById(studyConvention.getId())
                .orElseThrow(() -> new ConventionException(ExceptionMessage.CONVENTION_NOT_FOUND));

        //then
        assertEquals("컨벤션 수정", updateConvention.getName());
        assertEquals("설명 수정", updateConvention.getDescription());
        assertEquals("정규식 수정", updateConvention.getContent());
    }

    @Test
    @DisplayName("컨벤션 삭제 테스트")
    public void deleteStudyConvention() {

        //given
        User savedUser = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember leader = StudyMemberFixture.createStudyMemberLeader(savedUser.getId(), studyInfo.getId());
        studyMemberRepository.save(leader);

        StudyConvention studyConvention = StudyConventionFixture.createStudyDefaultConvention(studyInfo.getId());
        studyConventionRepository.save(studyConvention);


        //when
        studyMemberService.isValidateStudyLeader(savedUser, studyInfo.getId());
        studyConventionService.deleteStudyConvention(studyConvention.getId());

        // then
        assertThrows(ConventionException.class, () -> {
            studyConventionService.deleteStudyConvention(studyConvention.getId());
        }, ExceptionMessage.CONVENTION_NOT_FOUND.getText());

        assertFalse(studyConventionRepository.existsById(studyConvention.getId()));
    }

    @Test
    @DisplayName("컨벤션 단일 조회 테스트")
    public void readStudyConvention() {

        //given
        User savedUser = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember member = StudyMemberFixture.createDefaultStudyMember(savedUser.getId(), studyInfo.getId());
        studyMemberRepository.save(member);

        StudyConvention studyConvention = StudyConventionFixture.createStudyDefaultConvention(studyInfo.getId());
        studyConventionRepository.save(studyConvention);

        //when
        studyMemberService.isValidateStudyMember(savedUser, studyInfo.getId());
        studyConventionService.readStudyConvention(studyConvention.getId());

        //then
        assertEquals("컨벤션", studyConvention.getName());
        assertEquals("설명", studyConvention.getDescription());
        assertEquals("정규식", studyConvention.getContent());
        assertEquals(studyInfo.getId(), studyConvention.getStudyInfoId());
        assertTrue(studyConvention.isActive());

    }

    @Test
    @DisplayName("컨벤션 전체 조회 테스트")
    public void readStudyConventionList() {
        // given
        Random random = new Random();
        Long cursorIdx = Math.abs(random.nextLong()) + LIMIT;  // Limit 이상 랜덤값

        User savedUser = userRepository.save(generateAuthUser());

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(savedUser.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember member = StudyMemberFixture.createDefaultStudyMember(savedUser.getId(), studyInfo.getId());
        studyMemberRepository.save(member);

        StudyConvention studyConvention1 = StudyConventionFixture.createStudyConventionName(studyInfo.getId(), "1번째 컨벤션");
        StudyConvention studyConvention2 = StudyConventionFixture.createStudyConventionName(studyInfo.getId(), "2번째 컨벤션");
        StudyConvention studyConvention3 = StudyConventionFixture.createStudyConventionName(studyInfo.getId(), "3번째 컨벤션");
        StudyConvention studyConvention4 = StudyConventionFixture.createStudyConventionName(studyInfo.getId(), "4번째 컨벤션");
        studyConventionRepository.saveAll(List.of(studyConvention1, studyConvention2, studyConvention3, studyConvention4));

        //when
        studyMemberService.isValidateStudyMember(savedUser, studyInfo.getId());
        StudyConventionListAndCursorIdxResponse responses = studyConventionService.readStudyConventionList(studyInfo.getId(), cursorIdx, LIMIT);

        //then
        assertNotNull(responses);
        assertEquals(4, responses.getStudyConventionList().size());
        assertEquals("4번째 컨벤션", responses.getStudyConventionList().get(0).getName()); // 최신 컨벤션 확인

    }

    @Test
    void 정규식_통과_테스트() {
        // given
        String conventionName = "커밋 메세지 규칙";
        String convention = "^\\[[A-Za-z가-힣0-9]+\\] [A-Za-z가-힣]+: .+$";
        String conventionDescription = "커밋 메세지 규칙: [이름] 플랫폼 \":\" + \" \" + 문제 이름 \n" +
                "예시 1) [이주성] 백준: 크리스마스 트리 \n" +
                "예시 2) [이주성] 프로그래머스: 두 수의 곱";

        StudyConvention sc = StudyConvention.builder()
                .studyInfoId(1L)
                .name(conventionName)
                .description(conventionDescription)
                .content(convention)
                .isActive(true)
                .build();

        String commitMag = "[이주성] 백준: 크리스마스 트리";

        // when
        boolean result = studyConventionService.checkConvention(convention, commitMag);

        // then
        assertTrue(result);

    }

    @Test
    void 정규식_실패_테스트() {
        // given
        String conventionName = "커밋 메세지 규칙";
        String convention = "^\\[[A-Za-z가-힣0-9]+\\] [A-Za-z가-힣]+: .+$";
        String conventionDescription = "커밋 메세지 규칙: [이름] 플랫폼 \":\" + \" \" + 문제 이름 \n" +
                "예시 1) [이주성] 백준: 크리스마스 트리 \n" +
                "예시 2) [이주성] 프로그래머스: 두 수의 곱";

        StudyConvention sc = StudyConvention.builder()
                .studyInfoId(1L)
                .name(conventionName)
                .description(conventionDescription)
                .content(convention)
                .isActive(true)
                .build();

        String invalidCommitMag = "[이주성] 백준:크리스마스 트리";

        // when
        boolean result = studyConventionService.checkConvention(convention, invalidCommitMag);

        // then
        assertFalse(result);
    }
}
