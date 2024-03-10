package com.example.backend.study.api.service.member;

import com.example.backend.auth.TestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.member.MemberException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.constant.StudyMemberStatus;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.member.response.StudyMembersResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class StudyMemberServiceTest extends TestConfig {

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private StudyMemberService studyMemberService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyMemberRepository studyMemberRepository;

    @Autowired
    private StudyTodoMappingRepository studyTodoMappingRepository;

    @Autowired
    private StudyTodoRepository studyTodoRepository;

    @Autowired
    private AuthService authService;

    @AfterEach
    void tearDown() {
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
        studyTodoMappingRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
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


    @Test
    @DisplayName("스터디원 강퇴 테스트")
    public void resignStudyMember() {
        // given

        User leaderuser = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        User user2 = UserFixture.generateKaKaoUser();

        userRepository.saveAll(List.of(leaderuser, user1, user2));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leaderuser.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember leader = StudyMemberFixture.createStudyMemberLeader(leaderuser.getId(), studyInfo.getId());
        StudyMember activeMember1 = StudyMemberFixture.createDefaultStudyMember(user1.getId(), studyInfo.getId());
        StudyMember activeMember2 = StudyMemberFixture.createStudyMemberResigned(user2.getId(), studyInfo.getId());
        studyMemberRepository.saveAll(List.of(leader, activeMember1, activeMember2));

        // when
        studyMemberService.resignStudyMember(studyInfo.getId(), activeMember1.getUserId());
        Optional<StudyMember> studyMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), activeMember1.getUserId());

        // then
        assertEquals(StudyMemberStatus.STUDY_RESIGNED, studyMember.get().getStatus());

    }

    @Test
    @DisplayName("스터디원 탈퇴 테스트")
    public void withdrawalMember() {
        // given

        User leaderuser = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        User user2 = UserFixture.generateKaKaoUser();

        userRepository.saveAll(List.of(leaderuser, user1, user2));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leaderuser.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember leader = StudyMemberFixture.createStudyMemberLeader(leaderuser.getId(), studyInfo.getId());
        StudyMember activeMember1 = StudyMemberFixture.createDefaultStudyMember(user1.getId(), studyInfo.getId());
        studyMemberRepository.saveAll(List.of(leader, activeMember1));

        // when
        studyMemberService.withdrawalStudyMember(studyInfo.getId(), activeMember1.getUserId());
        Optional<StudyMember> studyMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), activeMember1.getUserId());

        // then
        assertEquals(StudyMemberStatus.STUDY_WITHDRAWAL, studyMember.get().getStatus());
    }


    @Test
    @DisplayName("스터디원 강퇴 테스트 - Todo mappings 함께 삭제 테스트")
    public void resignStudyMember_todo() {
        // given

        User leaderuser = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();

        userRepository.saveAll(List.of(leaderuser, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leaderuser.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember leader = StudyMemberFixture.createStudyMemberLeader(leaderuser.getId(), studyInfo.getId());
        StudyMember activeMember = StudyMemberFixture.createDefaultStudyMember(user1.getId(), studyInfo.getId());
        studyMemberRepository.saveAll(List.of(leader, activeMember));

        // 현재 날짜 이후로 설정된 To do (마감기한 지나지 않은 To do)
        StudyTodo futureTodo1 = StudyTodoFixture.createDateStudyTodo(studyInfo.getId(), LocalDate.now().plusDays(3));
        StudyTodo futureTodo2 = StudyTodoFixture.createDateStudyTodo(studyInfo.getId(), LocalDate.now().plusDays(5));
        // 현재 날짜 이전으로 설정된 To do (마감기한 지난 To do)
        StudyTodo pastTodo1 = StudyTodoFixture.createDateStudyTodo(studyInfo.getId(), LocalDate.now().minusDays(3));
        studyTodoRepository.saveAll(List.of(futureTodo1, futureTodo2, pastTodo1));

        // activeMember에게 할당된 to do: 2개는 미래, 1개는 과거
        StudyTodoMapping mappingFuture1 = StudyTodoFixture.createStudyTodoMapping(futureTodo1.getId(), activeMember.getUserId());
        StudyTodoMapping mappingFuture2 = StudyTodoFixture.createStudyTodoMapping(futureTodo2.getId(), activeMember.getUserId());
        StudyTodoMapping mappingPast1 = StudyTodoFixture.createCompleteStudyTodoMapping(pastTodo1.getId(), activeMember.getUserId());
        studyTodoMappingRepository.saveAll(List.of(mappingFuture1, mappingFuture2, mappingPast1));

        // when
        studyMemberService.resignStudyMember(studyInfo.getId(), activeMember.getUserId());
        Optional<StudyMember> resignedMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), activeMember.getUserId());
        List<StudyTodoMapping> todoMappings = studyTodoMappingRepository.findByUserId(activeMember.getUserId());

        // then
        assertEquals(StudyMemberStatus.STUDY_RESIGNED, resignedMember.get().getStatus());

        // 마감 기한이 지난 To do는 삭제x
        assertTrue(todoMappings.stream()
                .anyMatch(mapping -> mapping.getId().equals(mappingPast1.getId())));

        // 마감 기한이 지나지 않은 To do 삭제되어야 함
        assertFalse(todoMappings.stream()
                .anyMatch(mapping -> mapping.getId().equals(mappingFuture1.getId())));
        assertFalse(todoMappings.stream()
                .anyMatch(mapping -> mapping.getId().equals(mappingFuture2.getId())));
    }


    @Test
    @DisplayName("스터디원 탈퇴 테스트 - Todo mappings 함께 삭제 테스트")
    public void withdrawalStudyMember_todo() {
        // given

        User leaderuser = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();

        userRepository.saveAll(List.of(leaderuser, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leaderuser.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember leader = StudyMemberFixture.createStudyMemberLeader(leaderuser.getId(), studyInfo.getId());
        StudyMember activeMember = StudyMemberFixture.createDefaultStudyMember(user1.getId(), studyInfo.getId());
        studyMemberRepository.saveAll(List.of(leader, activeMember));

        // 현재 날짜 이후로 설정된 To do (마감기한 지나지 않은 To do)
        StudyTodo futureTodo1 = StudyTodoFixture.createDateStudyTodo(studyInfo.getId(), LocalDate.now().plusDays(3));
        StudyTodo futureTodo2 = StudyTodoFixture.createDateStudyTodo(studyInfo.getId(), LocalDate.now().plusDays(5));
        // 현재 날짜 이전으로 설정된 To do (마감기한 지난 To do)
        StudyTodo pastTodo1 = StudyTodoFixture.createDateStudyTodo(studyInfo.getId(), LocalDate.now().minusDays(3));
        studyTodoRepository.saveAll(List.of(futureTodo1, futureTodo2, pastTodo1));

        // activeMember에게 할당된 to do: 2개는 미래, 1개는 과거
        StudyTodoMapping mappingFuture1 = StudyTodoFixture.createStudyTodoMapping(futureTodo1.getId(), activeMember.getUserId());
        StudyTodoMapping mappingFuture2 = StudyTodoFixture.createStudyTodoMapping(futureTodo2.getId(), activeMember.getUserId());
        StudyTodoMapping mappingPast1 = StudyTodoFixture.createCompleteStudyTodoMapping(pastTodo1.getId(), activeMember.getUserId());
        studyTodoMappingRepository.saveAll(List.of(mappingFuture1, mappingFuture2, mappingPast1));

        // when
        studyMemberService.withdrawalStudyMember(studyInfo.getId(), activeMember.getUserId());
        Optional<StudyMember> withdrawalMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), activeMember.getUserId());
        List<StudyTodoMapping> todoMappings = studyTodoMappingRepository.findByUserId(activeMember.getUserId());

        // then
        assertEquals(StudyMemberStatus.STUDY_WITHDRAWAL, withdrawalMember.get().getStatus());

        // 마감 기한이 지난 To do는 삭제x
        assertTrue(todoMappings.stream()
                .anyMatch(mapping -> mapping.getId().equals(mappingPast1.getId())));

        // 마감 기한이 지나지 않은 To do 삭제되어야 함
        assertFalse(todoMappings.stream()
                .anyMatch(mapping -> mapping.getId().equals(mappingFuture1.getId())));
        assertFalse(todoMappings.stream()
                .anyMatch(mapping -> mapping.getId().equals(mappingFuture2.getId())));

    }


    @Test
    @DisplayName("스터디 가입 신청 테스트")
    public void applyStudyMember() {
        // given
        String joinCode = null;

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        UserInfoResponse userInfo = authService.findUserInfo(user1);

        // when
        studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode);
        Optional<StudyMember> waitMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), user1.getId());

        // then
        assertEquals(StudyMemberStatus.STUDY_WAITING, waitMember.get().getStatus());

    }

    @Test
    @DisplayName("한번 강퇴된 스터디원 가입 신청 테스트")
    public void applyStudyMember_resigned() {
        // given
        String joinCode = null;

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember studyMember = StudyMemberFixture.createStudyMemberResigned(user1.getId(), studyInfo.getId()); // 강퇴 멤버 생성
        studyMemberRepository.save(studyMember);

        UserInfoResponse userInfo = authService.findUserInfo(user1);

        // then
        MemberException em = assertThrows(MemberException.class, () -> {
            studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode);
        });
        assertEquals(ExceptionMessage.STUDY_RESIGNED_MEMBER.getText(), em.getMessage());

    }


    @Test
    @DisplayName("이미 신청완료한 스터디에 가입 재신청 테스트")
    public void applyStudyMember_replay() {
        // given
        String joinCode = null;

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember studyMember = StudyMemberFixture.createStudyMemberWaiting(user1.getId(), studyInfo.getId()); // 승인 대기중 멤버 생성
        studyMemberRepository.save(studyMember);

        UserInfoResponse userInfo = authService.findUserInfo(user1);


        // then
        MemberException em = assertThrows(MemberException.class, () -> {
            studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode);
        });

        assertEquals(ExceptionMessage.STUDY_WAITING_MEMBER.getText(), em.getMessage());

    }

    @Test
    @DisplayName("비공개 스터디 가입 신청- 참여코드가 맞는 경우")
    public void applyStudyMember_privateStudy_joinCode_match() {
        // given
        String joinCode = "joinCode";

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createPrivateStudyInfo(leader.getId(), joinCode); // 비공개 스터디 생성
        studyInfoRepository.save(studyInfo);


        UserInfoResponse userInfo = authService.findUserInfo(user1);

        // when
        studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), studyInfo.getJoinCode());
        Optional<StudyMember> waitMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), user1.getId());

        // then
        assertEquals(StudyMemberStatus.STUDY_WAITING, waitMember.get().getStatus());
    }

    @Test
    @DisplayName("비공개 스터디 가입 신청- 참여코드가 null인 경우")
    public void applyStudyMember_privateStudy_joinCode_null() {
        // given
        String joinCode = null;

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createPrivateStudyInfo(leader.getId(), joinCode); // 비공개 스터디 생성
        studyInfoRepository.save(studyInfo);

        StudyMember studyMember = StudyMemberFixture.createStudyMemberWaiting(user1.getId(), studyInfo.getId()); // 승인 대기중 멤버 생성
        studyMemberRepository.save(studyMember);

        UserInfoResponse userInfo = authService.findUserInfo(user1);

        // then
        MemberException em = assertThrows(MemberException.class, () -> {
            studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode);
        });

        assertEquals(ExceptionMessage.STUDY_JOIN_CODE_FAIL.getText(), em.getMessage());
    }

    @Test
    @DisplayName("비공개 스터디 가입 신청- 참여코드가 10자 이상인 경우")
    public void applyStudyMember_privateStudy_joinCode_10() {
        // given
        String joinCode = "1234567891011";

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createPrivateStudyInfo(leader.getId(), joinCode); // 비공개 스터디 생성
        studyInfoRepository.save(studyInfo);

        StudyMember studyMember = StudyMemberFixture.createStudyMemberWaiting(user1.getId(), studyInfo.getId()); // 승인 대기중 멤버 생성
        studyMemberRepository.save(studyMember);

        UserInfoResponse userInfo = authService.findUserInfo(user1);

        // then
        MemberException em = assertThrows(MemberException.class, () -> {
            studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode);
        });

        assertEquals(ExceptionMessage.STUDY_JOIN_CODE_FAIL.getText(), em.getMessage());
    }


    @Test
    @DisplayName("이전에 탈퇴한 멤버가 가입 신청 테스트")
    public void applyStudyMember_withdrawal() {
        // given
        String joinCode = null;

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember withdrawalMember = StudyMemberFixture.createStudyMemberWithdrawal(user1.getId(), studyInfo.getId());
        studyMemberRepository.save(withdrawalMember);

        UserInfoResponse userInfo = authService.findUserInfo(user1);

        // when
        studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode);
        Optional<StudyMember> waitMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), user1.getId());

        // then
        assertEquals(StudyMemberStatus.STUDY_WAITING, waitMember.get().getStatus());
    }

    @Test
    @DisplayName("이전에 승인 거부된 멤버가 가입 신청 테스트")
    public void applyStudyMember_refused() {
        // given
        String joinCode = null;

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember refusedMember = StudyMemberFixture.createStudyMemberRefused(user1.getId(), studyInfo.getId());
        studyMemberRepository.save(refusedMember);

        UserInfoResponse userInfo = authService.findUserInfo(user1);

        // when
        studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode);
        Optional<StudyMember> waitMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), user1.getId());

        // then
        assertEquals(StudyMemberStatus.STUDY_WAITING, waitMember.get().getStatus());
    }


}
