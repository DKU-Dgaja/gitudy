package com.example.backend.study.api.service.convention;

import com.example.backend.auth.TestConfig;
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
import com.example.backend.study.api.service.member.StudyMemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
}
