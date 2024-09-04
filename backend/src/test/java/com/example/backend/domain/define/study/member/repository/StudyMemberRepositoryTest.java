package com.example.backend.domain.define.study.member.repository;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.constant.StudyMemberStatus;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
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

    @Autowired
    StudyTodoRepository studyTodoRepository;

    @Autowired
    StudyTodoMappingRepository studyTodoMappingRepository;

    @Autowired
    StudyCommitRepository studyCommitRepository;

    @AfterEach
    void tearDown() {
        studyMemberRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
        studyTodoMappingRepository.deleteAllInBatch();
        studyCommitRepository.deleteAllInBatch();
    }

    @Test
    void 해당_유저가_스터디의_활동중인_스터디원인지_확인_스터디원일_경우() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        StudyMember savedMember = studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));

        // when
        assertTrue(studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(savedMember.getUserId(), savedMember.getStudyInfoId()));
    }

    @Test
    void 해당_유저가_스터디의_활동중인_스터디원인지_확인_테스트_스터디원이_아닐_경우() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        StudyMember savedMember = studyMemberRepository.save(StudyMemberFixture.createStudyMemberResigned(user.getId(), study.getId()));

        // when
        assertFalse(studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(savedMember.getUserId(), savedMember.getStudyInfoId()));
    }

    @Test
    void 해당_유저가_스터디장일_경우() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        StudyMember savedMember = studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(user.getId(), study.getId()));

        // when
        assertTrue(studyMemberRepository.isStudyLeaderByUserIdAndStudyInfoId(savedMember.getUserId(), savedMember.getStudyInfoId()));
    }

    @Test
    void 해당_유저가_스터디장이_아닐_경우() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));

        StudyMember savedMember = studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));

        // when
        assertFalse(studyMemberRepository.isStudyLeaderByUserIdAndStudyInfoId(savedMember.getUserId(), savedMember.getStudyInfoId()));
    }

    @Test
    void 해당_스터디의_활동중인_스터디원일_경우() {
        // given
        User leader = userRepository.save(UserFixture.generateAuthUser());
        User withdrawal = userRepository.save(UserFixture.generateKaKaoUser());

        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(leader.getId()));

        studyMemberRepository.save(StudyMemberFixture.createStudyMemberLeader(leader.getId(), study.getId()));
        studyMemberRepository.save(StudyMemberFixture.createStudyMemberWithdrawal(withdrawal.getId(), study.getId()));

        // when
        List<StudyMember> activeMembers = studyMemberRepository.findActiveMembersByStudyInfoId(study.getId());

        // then
        assertFalse(activeMembers.isEmpty());
        assertTrue(activeMembers.stream()
                .anyMatch(member -> member.getUserId().equals(leader.getId()) &&
                        member.getStudyInfoId().equals(study.getId())));
        assertFalse(activeMembers.stream()
                .anyMatch(member -> member.getUserId().equals(withdrawal.getId()) &&
                        member.getStudyInfoId().equals(study.getId())));
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

    @Test
    void GitHubId와_StudyInfoId를_통해_사용자가_활동중인_멤버인지_판단한다_성공_케이스() {
        // given
        User savedUser = userRepository.save(UserFixture.generateAuthJusung());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateDeletedStudyInfo(savedUser.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(savedUser.getId(), study.getId()));

        // when
        boolean result = studyMemberRepository.existsStudyMemberByGithubIdAndStudyInfoId(savedUser.getGithubId(), study.getId());

        // then
        assertTrue(result);
    }

    @Test
    void GitHubId와_StudyInfoId를_통해_사용자가_활동중인_멤버인지_판단한다_실패_케이스() {
        // given
        User savedUser = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(savedUser.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(savedUser.getId(), study.getId()));

        String invalidGithubId = "invalid";

        // when
        boolean result = studyMemberRepository.existsStudyMemberByGithubIdAndStudyInfoId(invalidGithubId, study.getUserId());

        // then
        assertFalse(result);
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
        studyMemberRepository.inActiveFromAllStudiesByUserId(savedUser.getId());
        StudyMember memberA = studyMemberRepository.findByStudyInfoIdAndUserId(studyA.getId(), savedUser.getId()).get();
        StudyMember memberB = studyMemberRepository.findByStudyInfoIdAndUserId(studyB.getId(), savedUser.getId()).get();

        // then
        assertSame(memberA.getStatus(), StudyMemberStatus.STUDY_WITHDRAWAL);
        assertSame(memberB.getStatus(), StudyMemberStatus.STUDY_WITHDRAWAL);
    }
}