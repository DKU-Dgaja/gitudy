package com.example.backend.study.api.service.convention;

import com.example.backend.auth.TestConfig;
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
import com.example.backend.study.api.service.member.StudyMemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.backend.auth.config.fixture.UserFixture.generateAuthUser;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
