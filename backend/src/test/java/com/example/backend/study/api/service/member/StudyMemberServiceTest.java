package com.example.backend.study.api.service.member;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.study.api.controller.member.response.StudyMembersResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StudyMemberServiceTest extends TestConfig {

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyMemberService studyMemberService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyMemberRepository studyMemberRepository;

    @AfterEach
    void tearDown() {
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
    }


    @Test
    @DisplayName("스터디에 속한 스터디원 조회(기여도별) 테스트")
    public void readStudyMembers_score() {
        // given
        boolean orderByScore = true;

        User leader = UserFixture.generatePlatfomIdAndNameAndProfile("1", "이정우", "이정우프로필사진");
        User activeMember1 = UserFixture.generatePlatfomIdAndNameAndProfile("2", "구영민", "구영민프로필사진");
        User activeMember2 = UserFixture.generatePlatfomIdAndNameAndProfile("3", "이주성", "이주성프로필사진");
        User activeMember3 = UserFixture.generatePlatfomIdAndNameAndProfile("4", "탁세하", "탁세하프로필사진");
        userRepository.saveAll(List.of(leader, activeMember1, activeMember2, activeMember3));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        studyMemberRepository.saveAll(List.of(
                StudyMemberFixture.createStudyActiveMembersByScore(leader.getId(), studyInfo.getId(), 77),
                StudyMemberFixture.createStudyActiveMembersByScore(activeMember1.getId(), studyInfo.getId(), 77),
                StudyMemberFixture.createStudyActiveMembersByScore(activeMember2.getId(), studyInfo.getId(), 99),
                StudyMemberFixture.createStudyActiveMembersByScore(activeMember3.getId(), studyInfo.getId(), 88)
        ));


        // when
        List<StudyMembersResponse> responses = studyMemberService.readStudyMembers(studyInfo.getId(), orderByScore);

        // then
        assertNotNull(responses);
        assertEquals(4, responses.size());

        assertEquals(99, responses.get(0).getScore());
        assertEquals("이주성", responses.get(0).getName());

        assertEquals(88, responses.get(1).getScore());
        assertEquals("탁세하", responses.get(1).getName());

        assertEquals(77, responses.get(2).getScore());
        assertEquals("이정우", responses.get(2).getName());

        assertEquals(77, responses.get(3).getScore());
        assertEquals("구영민", responses.get(3).getName());  // 점수 동일시 userId 낮은순

    }

    @Test
    @DisplayName("스터디에 속한 스터디원 조회(가입순) 테스트")
    public void readStudyMembers_userId() {
        // given
        boolean orderByScore = false;

        User leader = UserFixture.generatePlatfomIdAndNameAndProfile("1", "이정우", "이정우프로필사진");
        User activeMember1 = UserFixture.generatePlatfomIdAndNameAndProfile("2", "구영민", "구영민프로필사진");
        User activeMember2 = UserFixture.generatePlatfomIdAndNameAndProfile("3", "이주성", "이주성프로필사진");
        User activeMember3 = UserFixture.generatePlatfomIdAndNameAndProfile("4", "탁세하", "탁세하프로필사진");
        userRepository.saveAll(List.of(leader, activeMember1, activeMember2, activeMember3));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        studyMemberRepository.saveAll(List.of(
                StudyMemberFixture.createStudyActiveMembersByScore(leader.getId(), studyInfo.getId(), 77),
                StudyMemberFixture.createStudyActiveMembersByScore(activeMember1.getId(), studyInfo.getId(), 77),
                StudyMemberFixture.createStudyActiveMembersByScore(activeMember2.getId(), studyInfo.getId(), 99),
                StudyMemberFixture.createStudyActiveMembersByScore(activeMember3.getId(), studyInfo.getId(), 88)
        ));

        // when
        List<StudyMembersResponse> responses = studyMemberService.readStudyMembers(studyInfo.getId(), orderByScore);

        // then
        assertNotNull(responses);
        assertEquals(4, responses.size());

        assertEquals(77, responses.get(0).getScore());
        assertEquals("이정우", responses.get(0).getName());

        assertEquals(77, responses.get(1).getScore());
        assertEquals("구영민", responses.get(1).getName());

        assertEquals(99, responses.get(2).getScore());
        assertEquals("이주성", responses.get(2).getName());

        assertEquals(88, responses.get(3).getScore());
        assertEquals("탁세하", responses.get(3).getName());

    }


    @Test
    @DisplayName("스터디에 속한 스터디원 조회 - 활동중이지 않은 멤버가 있는 경우 테스트")
    public void readStudyWithdrawalMembers_userId() {
        // given
        boolean orderByScore = true;

        User leader = UserFixture.generatePlatfomIdAndNameAndProfile("1", "이정우", "이정우프로필사진");
        User activeMember1 = UserFixture.generatePlatfomIdAndNameAndProfile("2", "구영민", "구영민프로필사진");
        User activeMember2 = UserFixture.generatePlatfomIdAndNameAndProfile("3", "이주성", "이주성프로필사진");
        User activeMember3 = UserFixture.generatePlatfomIdAndNameAndProfile("4", "탁세하", "탁세하프로필사진");
        userRepository.saveAll(List.of(leader, activeMember1, activeMember2, activeMember3));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        // 영민,세하 활동중이지 않은 멤버 지정
        studyMemberRepository.saveAll(List.of(
                StudyMemberFixture.createStudyActiveMembersByScore(leader.getId(), studyInfo.getId(), 77),
                StudyMemberFixture.createStudyWithdrawalMembersByScore(activeMember1.getId(), studyInfo.getId(), 77),
                StudyMemberFixture.createStudyActiveMembersByScore(activeMember2.getId(), studyInfo.getId(), 99),
                StudyMemberFixture.createStudyWithdrawalMembersByScore(activeMember3.getId(), studyInfo.getId(), 88)
        ));

        // when
        List<StudyMembersResponse> responses = studyMemberService.readStudyMembers(studyInfo.getId(), orderByScore);

        // then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(99, responses.get(0).getScore());
        assertEquals("이주성", responses.get(0).getName());

        assertEquals(77, responses.get(1).getScore());
        assertEquals("이정우", responses.get(1).getName());

    }

}
