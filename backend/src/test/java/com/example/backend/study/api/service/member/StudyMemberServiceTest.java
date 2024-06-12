package com.example.backend.study.api.service.member;

import com.example.backend.MockTestConfig;
import com.example.backend.TestConfig;
import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.auth.AuthService;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.member.MemberException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.event.FcmFixture;
import com.example.backend.domain.define.fcm.FcmToken;
import com.example.backend.domain.define.fcm.listener.*;
import com.example.backend.domain.define.fcm.repository.FcmTokenRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.event.ApplyApproveRefuseMemberEvent;
import com.example.backend.domain.define.study.info.event.ApplyMemberEvent;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.constant.StudyMemberStatus;
import com.example.backend.domain.define.study.member.event.NotifyLeaderEvent;
import com.example.backend.domain.define.study.member.event.NotifyMemberEvent;
import com.example.backend.domain.define.study.member.event.ResignMemberEvent;
import com.example.backend.domain.define.study.member.event.WithdrawalMemberEvent;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.member.request.MessageRequest;
import com.example.backend.study.api.controller.member.response.StudyMemberApplyListAndCursorIdxResponse;
import com.example.backend.study.api.controller.member.response.StudyMembersResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class StudyMemberServiceTest extends MockTestConfig {

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


    @MockBean
    private ResignMemberListener resignMemberListener;

    @MockBean
    private WithdrawalMemberListener withdrawalMemberListener;

    @Autowired
    private FcmTokenRepository fcmTokenRepository;

    @MockBean
    private ApplyMemberListener applyMemberListener;

    @MockBean
    private ApplyApproveRefuseMemberListener applyApproveRefuseMemberListener;

    @MockBean
    private NotifyMemberListener notifyMemberListener;

    @MockBean
    private NotifyLeaderListener notifyLeaderListener;


    public final static Long CursorIdx = null;
    public final static Long Limit = 3L;

    @AfterEach
    void tearDown() {
        studyInfoRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
        studyTodoMappingRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
        fcmTokenRepository.deleteAll();
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
    @DisplayName("스터디원 강퇴 성공 테스트- 알람 true일 때")
    public void resignStudyMemberWhenIsAlarmTrue() throws FirebaseMessagingException {
        // given

        User leaderuser = UserFixture.generateAuthUserByPlatformId("leader");
        User user1 = UserFixture.generateAuthUserPushAlarmY(); // 알람 여부 true
        User user2 = UserFixture.generateKaKaoUser();

        userRepository.saveAll(List.of(leaderuser, user1, user2));
        fcmTokenRepository.save(FcmFixture.generateDefaultFcmToken(user1.getId()));

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

        // event 발생 검증
        verify(resignMemberListener).resignMemberListener(any(ResignMemberEvent.class));
    }


    @Test
    @DisplayName("스터디원 탈퇴 성공 테스트 - 알람 true일 때")
    public void withdrawalMemberWhenIsAlarmTrue() throws FirebaseMessagingException {
        // given

        User leaderuser = UserFixture.generateAuthUserPushAlarmY();
        User user1 = UserFixture.generateGoogleUser();
        User user2 = UserFixture.generateKaKaoUser();

        userRepository.saveAll(List.of(leaderuser, user1, user2));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leaderuser.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember leader = StudyMemberFixture.createStudyMemberLeader(leaderuser.getId(), studyInfo.getId());
        StudyMember activeMember1 = StudyMemberFixture.createDefaultStudyMember(user1.getId(), studyInfo.getId());
        studyMemberRepository.saveAll(List.of(leader, activeMember1));

        // when
        studyMemberService.withdrawalStudyMember(studyInfo.getId(), UserInfoResponse.of(user1));
        Optional<StudyMember> studyMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), activeMember1.getUserId());

        // then
        assertEquals(StudyMemberStatus.STUDY_WITHDRAWAL, studyMember.get().getStatus());

        // event 발생 검증
        verify(withdrawalMemberListener).withdrawalMemberListener(any(WithdrawalMemberEvent.class));
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

        fcmTokenRepository.save(FcmFixture.generateDefaultFcmToken(activeMember.getUserId()));

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
        studyMemberService.withdrawalStudyMember(studyInfo.getId(), UserInfoResponse.of(user1));
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

        MessageRequest request = StudyMemberFixture.generateMessageRequest();

//        System.out.println("studyInfo.getCurrentMember() = " + studyInfo.getCurrentMember());
        
        // when
        studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode, request);
        Optional<StudyMember> waitMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), user1.getId());
        StudyInfo saveStudy = studyInfoRepository.findById(studyInfo.getId()).get();

//        System.out.println("saveStudy.getCurrentMember() = " + saveStudy.getCurrentMember());

        // then
        assertEquals(StudyMemberStatus.STUDY_WAITING, waitMember.get().getStatus());
        assertEquals(request.getMessage(), waitMember.get().getSignGreeting()); // 스터디장에게 한마디 저장 확인
        assertEquals(saveStudy.getCurrentMember(), 2);
    }

    @Test
    @DisplayName("스터디 가입신청 알림 테스트 - 알림여부 true")
    void apply_notify_test_true() throws FirebaseMessagingException {
        // given
        String joinCode = null;

        User leader = UserFixture.generateAuthUserPushAlarmY();  // 알람여부 true 추가
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        UserInfoResponse userInfo = authService.findUserInfo(user1);

        FcmToken fcmToken = FcmFixture.generateDefaultFcmToken(leader.getId());
        fcmTokenRepository.save(fcmToken);

        MessageRequest request = StudyMemberFixture.generateMessageRequest();

        // when
        studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode, request);

        // then
        verify(applyMemberListener).applyMemberListener(any(ApplyMemberEvent.class)); // applyMemberListener 호출 검증
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

        MessageRequest request = StudyMemberFixture.generateMessageRequest();

        // then
        MemberException em = assertThrows(MemberException.class, () -> {
            studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode, request);
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

        MessageRequest request = StudyMemberFixture.generateMessageRequest();

        // then
        MemberException em = assertThrows(MemberException.class, () -> {
            studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode, request);
        });

        assertEquals(ExceptionMessage.STUDY_WAITING_MEMBER.getText(), em.getMessage());

    }

    @Test
    @DisplayName("비공개 스터디 가입 신청- 참여코드가 맞는 경우")
    public void applyStudyMember_privateStudy_joinCode_match() throws FirebaseMessagingException {
        // given
        String joinCode = "joinCode";

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createPrivateStudyInfo(leader.getId(), joinCode); // 비공개 스터디 생성
        studyInfoRepository.save(studyInfo);


        UserInfoResponse userInfo = authService.findUserInfo(user1);

        MessageRequest request = StudyMemberFixture.generateMessageRequest();

        // when
        studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), studyInfo.getJoinCode(), request);
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

        MessageRequest request = StudyMemberFixture.generateMessageRequest();

        // then
        MemberException em = assertThrows(MemberException.class, () -> {
            studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode, request);
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

        MessageRequest request = StudyMemberFixture.generateMessageRequest();

        // then
        MemberException em = assertThrows(MemberException.class, () -> {
            studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode, request);
        });

        assertEquals(ExceptionMessage.STUDY_JOIN_CODE_FAIL.getText(), em.getMessage());
    }


    @Test
    @DisplayName("이전에 탈퇴한 멤버가 가입 신청 테스트")
    public void applyStudyMember_withdrawal() throws FirebaseMessagingException {
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

        MessageRequest request = StudyMemberFixture.generateMessageRequest();

        // when
        studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode, request);
        Optional<StudyMember> waitMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), user1.getId());

        // then
        assertEquals(StudyMemberStatus.STUDY_WAITING, waitMember.get().getStatus());
    }

    @Test
    @DisplayName("이전에 승인 거부된 멤버가 가입 신청 테스트")
    public void applyStudyMember_refused() throws FirebaseMessagingException {
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

        MessageRequest request = StudyMemberFixture.generateMessageRequest();

        // when
        studyMemberService.applyStudyMember(userInfo, studyInfo.getId(), joinCode, request);
        Optional<StudyMember> waitMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), user1.getId());

        // then
        assertEquals(StudyMemberStatus.STUDY_WAITING, waitMember.get().getStatus());
    }

    @Test
    @DisplayName("스터디장의 가입신청 승인 테스트")
    public void leaderApplyApproveTest() {
        // given
        boolean approve = true;

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();

        int beforeUser1Score = user1.getScore();

        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        int beforeCurrentMember = studyInfo.getCurrentMember();

        StudyMember waitingMember = StudyMemberFixture.createStudyMemberWaiting(user1.getId(), studyInfo.getId());  // 승인 대기중 멤버 생성
        studyMemberRepository.save(waitingMember);

        // when
        studyMemberService.leaderApproveRefuseMember(studyInfo.getId(), waitingMember.getUserId(), approve);
        Optional<StudyMember> findStudyMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), waitingMember.getUserId());

        // then
        assertTrue(findStudyMember.isPresent());
        assertEquals(findStudyMember.get().getStatus(), StudyMemberStatus.STUDY_ACTIVE);  // 활동 상태로 변경

        // 스터디 가입 시 User score +5점
        assertEquals(beforeUser1Score + 5, userRepository.findById(user1.getId()).get().getScore());

        // 스터디 가입 시 현재인원 증가
        assertEquals(beforeCurrentMember + 1, studyInfoRepository.findById(studyInfo.getId()).get().getCurrentMember());
    }

    @Test
    @DisplayName("스터디장의 가입신청 승인 테스트 - 승인 알림")
    public void leader_apply_approve_notify() throws FirebaseMessagingException {

        // given
        boolean approve = true;

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateAuthUserPushAlarmY();


        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember waitingMember = StudyMemberFixture.createStudyMemberWaiting(user1.getId(), studyInfo.getId());  // 승인 대기중 멤버 생성
        studyMemberRepository.save(waitingMember);

        // when
        studyMemberService.leaderApproveRefuseMember(studyInfo.getId(), waitingMember.getUserId(), approve);

        // then
        verify(applyApproveRefuseMemberListener, times(1)).applyApproveRefuseMemberListener(any(ApplyApproveRefuseMemberEvent.class));
    }

    @Test
    @DisplayName("스터디장의 가입신청 거부 테스트")
    public void leaderApplyRefuseTest() {
        // given
        boolean approve = false;

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember waitingMember = StudyMemberFixture.createStudyMemberWaiting(user1.getId(), studyInfo.getId());  // 승인 대기중 멤버 생성
        studyMemberRepository.save(waitingMember);

        // when
        studyMemberService.leaderApproveRefuseMember(studyInfo.getId(), waitingMember.getUserId(), approve);
        Optional<StudyMember> findStudyMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), waitingMember.getUserId());

        // then
        assertTrue(findStudyMember.isPresent());
        assertEquals(findStudyMember.get().getStatus(), StudyMemberStatus.STUDY_REFUSED);  // 거부 상태로 변경
    }


    @Test
    @DisplayName("스터디장의 가입신청 거부 테스트 - 거부 알림")
    public void leader_apply_refuse_notify() throws FirebaseMessagingException {

        // given
        boolean approve = false;

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateAuthUserPushAlarmY();


        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember waitingMember = StudyMemberFixture.createStudyMemberWaiting(user1.getId(), studyInfo.getId());  // 승인 대기중 멤버 생성
        studyMemberRepository.save(waitingMember);

        // when
        studyMemberService.leaderApproveRefuseMember(studyInfo.getId(), waitingMember.getUserId(), approve);

        // then
        verify(applyApproveRefuseMemberListener, times(1)).applyApproveRefuseMemberListener(any(ApplyApproveRefuseMemberEvent.class));
    }

    @Test
    @DisplayName("스터디장의 가입신청 테스트 - 대기중인 유저가 아닐때")
    public void leaderApplyRefuseTest_waiting() {
        // given
        boolean approve = true;

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember refusedMember = StudyMemberFixture.createStudyMemberRefused(user1.getId(), studyInfo.getId());  // 거부된 멤버 생성
        studyMemberRepository.save(refusedMember);


        // then
        MemberException em = assertThrows(MemberException.class, () -> {
            studyMemberService.leaderApproveRefuseMember(studyInfo.getId(), refusedMember.getUserId(), approve);
        });

        assertEquals(ExceptionMessage.STUDY_WAITING_NOT_MEMBER.getText(), em.getMessage());
    }

    @Test
    @DisplayName("스터디 가입 신청 취소 테스트")
    public void applyCancelStudyMember() {
        // given
        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember waitingMember = StudyMemberFixture.createStudyMemberWaiting(user1.getId(), studyInfo.getId());
        studyMemberRepository.save(waitingMember);

        UserInfoResponse userInfo = authService.findUserInfo(user1);

        // when
        studyMemberService.applyCancelStudyMember(userInfo, studyInfo.getId());
        Optional<StudyMember> cancelledMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), userInfo.getUserId());

        // then
        assertFalse(cancelledMember.isPresent());
    }

    @Test
    @DisplayName("스터디 가입 신청 실패 테스트- 대기중인 멤버가 아닐때")
    public void applyCancelStudyMember_fail() {
        // given
        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember notWaitingMember = StudyMemberFixture.createStudyMemberResigned(user1.getId(), studyInfo.getId());
        studyMemberRepository.save(notWaitingMember);

        UserInfoResponse userInfo = authService.findUserInfo(user1);

        // then
        MemberException em = assertThrows(MemberException.class, () -> {
            studyMemberService.applyCancelStudyMember(userInfo, studyInfo.getId());
        });

        assertEquals(ExceptionMessage.STUDY_WAITING_NOT_MEMBER.getText(), em.getMessage());
    }

    @Test
    @DisplayName("스터디 가입 신청 실패 테스트- 멤버를 찾을 수 없을때")
    public void applyCancelStudyMember_fail_2() {
        // given
        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateGoogleUser();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        UserInfoResponse userInfo = authService.findUserInfo(user1);

        // then
        MemberException em = assertThrows(MemberException.class, () -> {
            studyMemberService.applyCancelStudyMember(userInfo, studyInfo.getId());
        });

        assertEquals(ExceptionMessage.USER_NOT_STUDY_MEMBER.getText(), em.getMessage());
    }


    @Test
    @DisplayName("스터디 가입신청 목록 조회 테스트")
    void applyListStudyMember() {
        // given
        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateDefaultUser("1", "Lee");
        User user2 = UserFixture.generateDefaultUser("2", "Koo");
        User user3 = UserFixture.generateDefaultUser("3", "Tak");
        User user4 = UserFixture.generateDefaultUser("4", "Joo");
        userRepository.saveAll(List.of(leader, user1, user2, user3, user4));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember waitStudyMember1 = StudyMemberFixture.createStudyMemberWaiting(user1.getId(), studyInfo.getId());
        StudyMember waitStudyMember2 = StudyMemberFixture.createStudyMemberWaiting(user2.getId(), studyInfo.getId());
        StudyMember waitStudyMember3 = StudyMemberFixture.createStudyMemberWaiting(user3.getId(), studyInfo.getId());
        StudyMember waitStudyMember4 = StudyMemberFixture.createStudyMemberWaiting(user4.getId(), studyInfo.getId());
        studyMemberRepository.saveAll(List.of(waitStudyMember1, waitStudyMember2, waitStudyMember3, waitStudyMember4));

        // when
        StudyMemberApplyListAndCursorIdxResponse responses = studyMemberService.applyListStudyMember(studyInfo.getId(), CursorIdx, Limit);

        // then
        assertNotNull(responses);
        assertEquals(3, responses.getApplyList().size());
        assertEquals("Lee", responses.getApplyList().get(0).getName());
        assertEquals("Tak", responses.getApplyList().get(2).getName());
    }

    @Test
    @DisplayName("가입신청 목록 커서 기반 페이징 로직 검증")
    void applyList_CursorPaging_Success() {
        // given
        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateDefaultUser("1", "Lee");
        User user2 = UserFixture.generateDefaultUser("2", "Koo");
        User user3 = UserFixture.generateDefaultUser("3", "Tak");
        User user4 = UserFixture.generateDefaultUser("4", "Joo");
        userRepository.saveAll(List.of(leader, user1, user2, user3, user4));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember waitStudyMember1 = StudyMemberFixture.createStudyMemberWaiting(user1.getId(), studyInfo.getId());
        StudyMember waitStudyMember2 = StudyMemberFixture.createStudyMemberWaiting(user2.getId(), studyInfo.getId());
        StudyMember waitStudyMember3 = StudyMemberFixture.createStudyMemberWaiting(user3.getId(), studyInfo.getId());
        StudyMember waitStudyMember4 = StudyMemberFixture.createStudyMemberWaiting(user4.getId(), studyInfo.getId());
        studyMemberRepository.saveAll(List.of(waitStudyMember1, waitStudyMember2, waitStudyMember3, waitStudyMember4));

        // when1
        StudyMemberApplyListAndCursorIdxResponse firstPageResponse = studyMemberService.applyListStudyMember(studyInfo.getId(), CursorIdx, Limit);

        // then1
        assertNotNull(firstPageResponse);
        assertEquals(3, firstPageResponse.getApplyList().size());
        assertEquals("Lee", firstPageResponse.getApplyList().get(0).getName());

        // when2
        Long newCursorIdx = firstPageResponse.getCursorIdx();
        StudyMemberApplyListAndCursorIdxResponse secondPageResponse = studyMemberService.applyListStudyMember(studyInfo.getId(), newCursorIdx, Limit);

        // then2
        assertNotNull(secondPageResponse);
        assertEquals(1, secondPageResponse.getApplyList().size());
        assertEquals("Joo", secondPageResponse.getApplyList().get(0).getName());
    }

    @Test
    @DisplayName("가입신청 목록 테스트- 멤버가 섞여있을때")
    void applyList_MemberMix() {
        // given
        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateDefaultUser("1", "Lee");
        User user2 = UserFixture.generateDefaultUser("2", "Koo");
        User user3 = UserFixture.generateDefaultUser("3", "Tak");
        User user4 = UserFixture.generateDefaultUser("4", "Joo");
        userRepository.saveAll(List.of(leader, user1, user2, user3, user4));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        StudyMember waitStudyMember1 = StudyMemberFixture.createStudyMemberWaiting(user1.getId(), studyInfo.getId());
        StudyMember resignedStudyMember2 = StudyMemberFixture.createStudyMemberResigned(user2.getId(), studyInfo.getId());  // 강퇴된 멤버
        StudyMember waitStudyMember3 = StudyMemberFixture.createStudyMemberWaiting(user3.getId(), studyInfo.getId());
        StudyMember refusedStudyMember4 = StudyMemberFixture.createStudyMemberResigned(user4.getId(), studyInfo.getId());  // 거부된 멤버
        studyMemberRepository.saveAll(List.of(waitStudyMember1, resignedStudyMember2, waitStudyMember3, refusedStudyMember4));

        // when
        StudyMemberApplyListAndCursorIdxResponse responses = studyMemberService.applyListStudyMember(studyInfo.getId(), CursorIdx, Limit);

        // then
        assertNotNull(responses);
        assertEquals(2, responses.getApplyList().size());
        assertEquals("Lee", responses.getApplyList().get(0).getName());
        assertEquals("Tak", responses.getApplyList().get(1).getName());
    }


    @Test
    @DisplayName("가입신청 목록 테스트 - 가입신청이 없는경우")
    void applyList_empty() {
        // given
        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateDefaultUser("1", "Lee");
        User user2 = UserFixture.generateDefaultUser("2", "Koo");
        User user3 = UserFixture.generateDefaultUser("3", "Tak");
        User user4 = UserFixture.generateDefaultUser("4", "Joo");
        userRepository.saveAll(List.of(leader, user1, user2, user3, user4));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        // 전부 활동중인 멤버로 생성
        StudyMember activeStudyMember1 = StudyMemberFixture.createDefaultStudyMember(user1.getId(), studyInfo.getId());
        StudyMember activeStudyMember2 = StudyMemberFixture.createDefaultStudyMember(user2.getId(), studyInfo.getId());
        StudyMember activeStudyMember3 = StudyMemberFixture.createDefaultStudyMember(user3.getId(), studyInfo.getId());
        StudyMember activeStudyMember4 = StudyMemberFixture.createDefaultStudyMember(user4.getId(), studyInfo.getId());
        studyMemberRepository.saveAll(List.of(activeStudyMember1, activeStudyMember2, activeStudyMember3, activeStudyMember4));

        // then
        MemberException em = assertThrows(MemberException.class, () -> {
            studyMemberService.applyListStudyMember(studyInfo.getId(), CursorIdx, Limit);
        });

        assertEquals(ExceptionMessage.STUDY_NOT_APPLY_LIST.getText(), em.getMessage());
    }

    @Test
    @DisplayName("스터디 멤버에게 알림 테스트 - 알림여부 true")
    void notify_member_test_true() throws FirebaseMessagingException {
        // given

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateAuthUserPushAlarmY();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        FcmToken fcmToken = FcmFixture.generateDefaultFcmToken(leader.getId());
        fcmTokenRepository.save(fcmToken);

        MessageRequest request = StudyMemberFixture.generateMessageRequest();

        // when
        studyMemberService.notifyToStudyMember(studyInfo.getId(), user1.getId(), request);

        // then
        verify(notifyMemberListener).notifyMemberListener(any(NotifyMemberEvent.class)); // notifyMemberListener 호출 검증
    }

    @Test
    @DisplayName("스터디 멤버가 팀장에게 알림 테스트 - 알림여부 true")
    void notify_leader_test_true() throws FirebaseMessagingException {
        // given

        User leader = UserFixture.generateAuthUser();
        User user1 = UserFixture.generateAuthUserPushAlarmY();
        userRepository.saveAll(List.of(leader, user1));

        StudyInfo studyInfo = StudyInfoFixture.createDefaultPublicStudyInfo(leader.getId());
        studyInfoRepository.save(studyInfo);

        FcmToken fcmToken = FcmFixture.generateDefaultFcmToken(leader.getId());
        fcmTokenRepository.save(fcmToken);

        UserInfoResponse userInfo = UserInfoResponse.of(user1);

        MessageRequest request = StudyMemberFixture.generateMessageRequest();

        // when
        studyMemberService.notifyToStudyLeader(studyInfo.getId(), userInfo, request);

        // then
        verify(notifyLeaderListener).notifyLeaderListener(any(NotifyLeaderEvent.class)); // notifyLeaderListener 호출 검증
    }

}
