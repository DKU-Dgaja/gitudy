package com.example.backend.domain.define.study.member.repository;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.study.api.controller.info.response.StudyMemberWithUserInfoResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("NonAsciiCharacters")
class StudyMemberRepositoryTest extends TestConfig {
    @Autowired
    private StudyMemberRepository studyMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @AfterEach
    void tearDown() {
        studyMemberRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
    }

    @Test
    void 해당_유저가_스터디의_활동중인_스터디원인지_확인_스터디원일_경우() {
        // given
        Long userId = 1L;
        Long studyInfoId = 1L;

        StudyMember savedMember = studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(userId, studyInfoId));

        // when
        assertTrue(studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(savedMember.getUserId(), savedMember.getStudyInfoId()));
    }

    @Test
    void 해당_유저가_스터디의_활동중인_스터디원인지_확인_테스트_스터디원이_아닐_경우() {
        // given
        Long userId = 1L;
        Long studyInfoId = 1L;

        StudyMember savedMember = studyMemberRepository.save(StudyMemberFixture.createStudyMemberResigned(userId, studyInfoId));

        // when
        assertFalse(studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(savedMember.getUserId(), savedMember.getStudyInfoId()));
    }

    @Test
    void 해당_유저가_스터디장일_경우() {
        // given
        Long userId = 1L;
        Long studyInfoId = 1L;

        StudyMember savedMember = studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(userId, studyInfoId));

        // when
        assertTrue(studyMemberRepository.isStudyLeaderByUserIdAndStudyInfoId(savedMember.getUserId(), savedMember.getStudyInfoId()));
    }

    @Test
    void 해당_유저가_스터디장이_아닐_경우() {
        // given
        Long userId = 1L;
        Long studyInfoId = 1L;

        StudyMember savedMember = studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(userId, studyInfoId));

        // when
        assertFalse(studyMemberRepository.isStudyLeaderByUserIdAndStudyInfoId(savedMember.getUserId(), savedMember.getStudyInfoId()));
    }

    @Test
    void 해당_스터디의_활동중인_스터디원일_경우() {
        // given
        Long leaderId = 1L;
        Long withdrawalId = 2L;
        Long studyInfoId = 1L;

        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(leaderId, studyInfoId));
        studyMemberRepository.save(StudyMemberFixture.createStudyMemberWithdrawal(withdrawalId, studyInfoId));

        // when
        List<StudyMember> activeMembers = studyMemberRepository.findActiveMembersByStudyInfoId(studyInfoId);

        // then
        assertFalse(activeMembers.isEmpty());
        assertTrue(activeMembers.stream()
                .anyMatch(member -> member.getUserId().equals(leaderId) &&
                        member.getStudyInfoId().equals(studyInfoId)));
        assertFalse(activeMembers.stream()
                .anyMatch(member -> member.getUserId().equals(withdrawalId) &&
                        member.getStudyInfoId().equals(studyInfoId)));
    }
    @Test
    void studyInfoIdList를_통해_스터디들의_모든_멤버를_조회(){
        User user1 = userRepository.save(UserFixture.generateAuthUserByPlatformId("1"));
        User user2 = userRepository.save(UserFixture.generateAuthUserByPlatformId("2"));
        User user3 = userRepository.save(UserFixture.generateAuthUserByPlatformId("3"));
        User user4 = userRepository.save(UserFixture.generateAuthUserByPlatformId("4"));

        // Study 생성
        List<Long> studyInfoIds = new ArrayList<>();
        StudyInfo studyInfo1 = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user1.getId())); studyInfoIds.add(studyInfo1.getId());
        StudyInfo studyInfo2 = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user2.getId())); studyInfoIds.add(studyInfo2.getId());

        // Study 1 멤버 생성
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user1.getId(), studyInfo1.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user2.getId(), studyInfo1.getId()));

        // Study 2 멤버 생성
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user3.getId(), studyInfo2.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user4.getId(), studyInfo2.getId()));

        List<StudyMemberWithUserInfoResponse> members = studyMemberRepository.findStudyMemberListByStudyInfoListJoinUserInfo(studyInfoIds);

        // Then
        assertEquals(4, members.size());

        Set<Long> studyInfoIdsFromMembers = members.stream()
                .map(StudyMemberWithUserInfoResponse::getStudyInfoId)
                .collect(Collectors.toSet());
        assertTrue(studyInfoIdsFromMembers.containsAll(studyInfoIds));
    }
}