package com.example.backend.study.api.service.info;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.constant.StudyMemberStatus;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


class StudyManagementServiceTest extends TestConfig {

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyInfoRepository studyInfoRepository;

    @Autowired
    StudyMemberRepository studyMemberRepository;

    @Autowired
    StudyManagementService studyManagementService;

    @AfterEach
    void tearDown() {
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
    }

    @Test
    void 유저_아이디에_해당하는_회원의_스터디_전부_비활성화() {
        // given
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));
        studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));
        studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));
        studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));
        studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));

        // when
        studyManagementService.closeStudiesOwnedByUser(savedUser.getId());
        List<StudyInfo> allByUserId = studyInfoRepository.findAllByUserId(savedUser.getId());

        // then
        assertEquals(5, allByUserId.size());
        for (StudyInfo studyInfo : allByUserId) {
            assertSame(studyInfo.getStatus(), StudyStatus.STUDY_INACTIVE);
        }
    }

    @Test
    void userId에_해당하는_스터디_멤버_상태를_전부_비활성화_시킨다() {
        // given
        User savedUser = userRepository.save(UserFixture.generateAuthUser());

        StudyInfo studyA = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(savedUser.getId(), studyA.getId()));

        StudyInfo studyB = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(savedUser.getId(), studyB.getId()));

        // when
        studyManagementService.inactiveUserFromAllStudies(savedUser.getId());
        StudyMember memberA = studyMemberRepository.findByStudyInfoIdAndUserId(studyA.getId(), savedUser.getId()).get();
        StudyMember memberB = studyMemberRepository.findByStudyInfoIdAndUserId(studyB.getId(), savedUser.getId()).get();

        // then
        assertSame(memberA.getStatus(), StudyMemberStatus.STUDY_WITHDRAWAL);
        assertSame(memberB.getStatus(), StudyMemberStatus.STUDY_WITHDRAWAL);
    }
}