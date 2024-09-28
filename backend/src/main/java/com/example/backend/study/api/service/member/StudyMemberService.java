package com.example.backend.study.api.service.member;


import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.auth.api.service.rank.event.UserScoreUpdateEvent;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.member.MemberException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.github.GithubApiToken;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.constant.RepositoryInfo;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.domain.define.study.info.event.ApplyApproveRefuseMemberEvent;
import com.example.backend.domain.define.study.info.event.ApplyMemberEvent;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.constant.StudyMemberStatus;
import com.example.backend.domain.define.study.member.event.NotifyLeaderEvent;
import com.example.backend.domain.define.study.member.event.NotifyMemberEvent;
import com.example.backend.domain.define.study.member.event.ResignMemberEvent;
import com.example.backend.domain.define.study.member.event.WithdrawalMemberEvent;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.member.request.MessageRequest;
import com.example.backend.study.api.controller.member.response.StudyMemberApplyListAndCursorIdxResponse;
import com.example.backend.study.api.controller.member.response.StudyMemberApplyResponse;
import com.example.backend.study.api.controller.member.response.StudyMembersResponse;
import com.example.backend.study.api.service.github.GithubApiService;
import com.example.backend.study.api.service.github.GithubApiTokenService;
import com.example.backend.study.api.service.info.StudyInfoService;
import com.example.backend.study.api.service.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.backend.domain.define.study.member.constant.StudyMemberStatus.STUDY_WAITING;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyMemberService {

    private final StudyMemberRepository studyMemberRepository;
    private final StudyTodoRepository studyTodoRepository;
    private final StudyTodoMappingRepository studyTodoMappingRepository;
    private final StudyInfoService studyInfoService;
    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;
    private final GithubApiService githubApiService;
    private final GithubApiTokenService githubApiTokenService;
    private final static int JOIN_CODE_LENGTH = 10;
    private final static Long MAX_LIMIT = 10L;

    // 스터디장 검증 메서드
    public UserInfoResponse isValidateStudyLeader(User userPrincipal, Long studyInfoId) {

        // platformId와 platformType을 이용하여 User 객체 조회
        User user = userService.findUserByPlatformIdAndPlatformTypeOrThrowException(userPrincipal);

        // 스터디장인지 확인
        if (!studyMemberRepository.isStudyLeaderByUserIdAndStudyInfoId(user.getId(), studyInfoId)) {
            throw new MemberException(ExceptionMessage.STUDY_MEMBER_NOT_LEADER);
        }

        return UserInfoResponse.of(user);
    }

    // 스터디장 확인 메서드
    public boolean isTrueStudyLeader(User user, Long studyInfoId) {
        return studyMemberRepository.isStudyLeaderByUserIdAndStudyInfoId(user.getId(), studyInfoId);
    }

    // 스터디 멤버인지 검증
    public UserInfoResponse isValidateStudyMember(User userPrincipal, Long studyInfoId) {

        // platformId와 platformType을 이용하여 User 객체 조회
        User user = userService.findUserByPlatformIdAndPlatformTypeOrThrowException(userPrincipal);

        // 스터디 멤버인지확인
        if (!studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(user.getId(), studyInfoId)) {
            throw new MemberException(ExceptionMessage.STUDY_NOT_MEMBER);
        }

        return UserInfoResponse.of(user);
    }


    // 스터디에 속한 스터디원 조회 (기여도별)
    public List<StudyMembersResponse> readStudyMembers(Long studyInfoId, boolean orderByScore) {

        // 스터디 조회 예외처리
        studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        return studyMemberRepository.findStudyMembersByStudyInfoIdOrderByScore(studyInfoId, orderByScore);
    }


    // 스터디원 강퇴 메서드
    @Transactional
    public void resignStudyMember(Long studyInfoId, Long resignUserId) {
        // 스터디 조회
        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        // 강퇴시킬 스터디원 조회
        StudyMember resignMember = findStudyMemberByStudyInfoIdAndUserIdOrThrowException(studyInfoId, resignUserId);

        // 강퇴 스터디원 상태 업데이트
        resignMember.updateStudyMemberStatus(StudyMemberStatus.STUDY_RESIGNED);

        // 스터디원 감소
        studyInfo.updateCurrentMember(-1);

        // 강퇴 스터디원에게 할당된 마감기한이 지나지 않은 To do 삭제
        studyTodoRepository.deleteTodoIdsByStudyInfoIdAndUserId(studyInfoId, resignUserId);

        // 강퇴할 유저 조회
        User resignUser = userService.findUserByStudyMemberOrThrowException(resignMember);

        // 알림 비동기처리
        eventPublisher.publishEvent(ResignMemberEvent.builder()
                .isPushAlarmYn(resignUser.isPushAlarmYn())
                .studyInfoId(studyInfo.getId())
                .resignMemberId(resignUserId)
                .studyInfoTopic(studyInfo.getTopic())
                .build());

    }


    // 스터디원 탈퇴 메서드
    @Transactional
    public void withdrawalStudyMember(Long studyInfoId, UserInfoResponse user) {
        // 스터디 조회
        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        // 탈퇴 스터디원 조회
        StudyMember withdrawalMember = findStudyMemberByStudyInfoIdAndUserIdOrThrowException(studyInfoId, user.getUserId());

        // 탈퇴 스터디원 상태 메서드
        withdrawalMember.updateStudyMemberStatus(StudyMemberStatus.STUDY_WITHDRAWAL);

        // 스터디원 감소
        studyInfo.updateCurrentMember(-1);

        // 탈퇴 스터디원에게 할당된 마감기한이 지나지 않은 To do 삭제
        studyTodoRepository.deleteTodoIdsByStudyInfoIdAndUserId(studyInfoId, user.getUserId());

        // 스터디장 조회
        User studyLeader = userService.findUserByIdOrThrowException(studyInfo.getUserId());

        // 알림 비동기처리
        eventPublisher.publishEvent(WithdrawalMemberEvent.builder()
                .isPushAlarmYn(studyLeader.isPushAlarmYn())
                .studyInfoId(studyInfo.getId())
                .studyLeaderId(studyInfo.getUserId())
                .withdrawalMemberName(user.getName())
                .studyInfoTopic(studyInfo.getTopic())
                .build());

    }


    // 스터디 가입 메서드
    @Transactional
    public void applyStudyMember(UserInfoResponse user, Long studyInfoId, String joinCode, MessageRequest messageRequest) {

        // 스터디 조회 예외처리
        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        // 비공개 스터디인 경우 joinCode 검증 (null, 맞지않을때, 10자를 넘겼을때)
        if (studyInfo.getStatus() == StudyStatus.STUDY_PRIVATE) {
            if (joinCode == null || !joinCode.equals(studyInfo.getJoinCode()) || joinCode.length() > JOIN_CODE_LENGTH) {
                log.warn(">>>> {} : {} <<<<", joinCode, ExceptionMessage.STUDY_JOIN_CODE_FAIL);
                throw new MemberException(ExceptionMessage.STUDY_JOIN_CODE_FAIL);
            }
        }

        User findUser = userService.findUserByIdOrThrowException(user.getUserId());

        // ACTIVE 상태 재가입 불가
        if (studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(user.getUserId(), studyInfoId)) {
            log.warn(">>>> {} : {} <<<<", user.getUserId(), ExceptionMessage.STUDY_ALREADY_MEMBER);
            throw new MemberException(ExceptionMessage.STUDY_ALREADY_MEMBER);
        }

        // WAITING, WITHDRAWAL, RESIGNED 상태는 재가입 불가
        if (studyMemberRepository.isMemberStatusByUserIdAndStudyInfoId(findUser.getId(), studyInfo.getId())) {
            log.warn(">>>> {} : {} <<<<", user.getUserId(), ExceptionMessage.STUDY_REAPPLY_MEMBER);
            throw new MemberException(ExceptionMessage.STUDY_REAPPLY_MEMBER);
        }

        // 승인 거부된 유저인지 확인
        Optional<StudyMember> existingMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfo.getId(), findUser.getId());
        if (existingMember.isEmpty()) {
            // 신규 가입자 StudyMember 생성
            studyMemberRepository.save(StudyMember.builder()
                    .studyInfoId(studyInfo.getId())
                    .userId(findUser.getId())
                    .status(STUDY_WAITING)
                    .signGreeting(messageRequest.getMessage())
                    .build());

        } else { // 스터디장이 승인 거부 했던 멤버
            existingMember.get().updateStudyMemberStatus(STUDY_WAITING);
            existingMember.get().updateSignGreeting(messageRequest.getMessage());
        }

        User leader = userService.findUserByIdOrThrowException(studyInfo.getUserId());

        // 알림 비동기처리
        eventPublisher.publishEvent(ApplyMemberEvent.builder()
                .isPushAlarmYn(leader.isPushAlarmYn())
                .studyInfoId(studyInfoId)
                .studyLeaderId(leader.getId())
                .studyTopic(studyInfo.getTopic())
                .name(user.getName())
                .build());
    }

    // 스터디 가입 취소 메서드
    @Transactional
    public void applyCancelStudyMember(UserInfoResponse user, Long studyInfoId) {

        // 스터디 조회 예외처리
        studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        // 대기중인 멤버인지 조회
        Optional<StudyMember> existingMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfoId, user.getUserId());

        // 멤버가 존재하지 않으면 예외 발생
        if (existingMember.isEmpty()) {
            log.warn(">>>> {} : {} <<<<", user.getUserId(), ExceptionMessage.USER_NOT_STUDY_MEMBER);
            throw new MemberException(ExceptionMessage.USER_NOT_STUDY_MEMBER);
        }

        // 멤버의 상태가 대기중이 아니면 예외 발생
        checkMemberStatusWaiting(existingMember.get());

        // 상태가 대기인 멤버 삭제
        studyMemberRepository.delete(existingMember.get());
    }

    // 스터디장의 가입 신청 승인/거부 메서드
    @Transactional
    public void leaderApproveRefuseMember(Long studyInfoId, Long applyUserId, boolean approve) {

        // 스터디 조회 예외처리
        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        // 승인/거부할 스터디원 조회
        StudyMember applyMember = findStudyMemberByStudyInfoIdAndUserIdOrThrowException(studyInfoId, applyUserId);

        // 신청대기중인 유저가 아닌경우 예외처리
        checkMemberStatusWaiting(applyMember);

        if (approve && studyInfo.isMaximumMember()) {

            // 스터디원 증가
            studyInfo.updateCurrentMember(1);

            applyMember.updateStudyMemberStatus(StudyMemberStatus.STUDY_ACTIVE);

            // 유저 점수 업데이트
            User findUser = updateUserScore(applyMember);

            // 레포지토리 초대 및 수락
            addCollaborator(studyInfo, findUser);

            // 활성화되어있는 Todo를 할당
            saveActiveTodoMapping(studyInfoId, findUser);

        } else {
            applyMember.updateStudyMemberStatus(StudyMemberStatus.STUDY_REFUSED);
        }

        User applyUser = userService.findUserByIdOrThrowException(applyUserId);

        // 알림 비동기처리
        eventPublisher.publishEvent(ApplyApproveRefuseMemberEvent.builder()
                .isPushAlarmYn(applyUser.isPushAlarmYn())
                .approve(approve)
                .studyInfoId(studyInfo.getId())
                .applyUserId(applyUserId)
                .studyTopic(studyInfo.getTopic())
                .name(applyUser.getName())
                .build());

    }

    private void saveActiveTodoMapping(Long studyInfoId, User findUser) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        List<StudyTodo> activeTodos = studyTodoRepository.findByStudyInfoIdAndTodoDateGreaterThanEqual(studyInfoId, today);

        List<StudyTodoMapping> studyTodoMappings = activeTodos.stream()
                .map(todo -> StudyTodoMapping.builder()
                        .userId(findUser.getId())
                        .todoId(todo.getId())
                        .status(StudyTodoStatus.TODO_INCOMPLETE)
                        .build())
                .toList();
        studyTodoMappingRepository.saveAll(studyTodoMappings);
    }

    private void addCollaborator(StudyInfo studyInfo, User findUser) {
        // 스터디장 레포지토리에 가입 성공 스터디원 Collaborator 추가
        RepositoryInfo repoInfo = studyInfo.getRepositoryInfo();

        // 스터디 레포지토리에 초대
        log.info("레포지토리에 초대하기 전 스터디장의 토큰 조회 중.. (userId: {})", studyInfo.getUserId());
        GithubApiToken leaderToken = githubApiTokenService.getToken(studyInfo.getUserId());
        log.info("레포지토리에 초대하기 전 스터디장의 토큰 조회 완료 (userId: {})", studyInfo.getUserId());

        githubApiService.addCollaborator(leaderToken.githubApiToken(),
                repoInfo, findUser.getGithubId());

        // 스터디원의 초대 수락
        log.info("레포지토리에 초대하기 전 스터디원의 토큰 조회 중.. (userId: {})", findUser.getId());
        GithubApiToken memberToken = githubApiTokenService.getToken(findUser.getId());
        log.info("레포지토리에 초대하기 전 스터디원의 토큰 조회 완료 (userId: {})", findUser.getId());

        githubApiService.acceptInvitation(memberToken.githubApiToken(), findUser.getGithubId());
    }

    @NonNull
    private User updateUserScore(StudyMember applyMember) {
        // User 조회
        User findUser = userService.findUserByStudyMemberOrThrowException(applyMember);

        // 스터디 가입 시 User +5점
        findUser.addUserScore(5);

        // 유저점수 이벤트 발생
        eventPublisher.publishEvent(UserScoreUpdateEvent.builder()
                .userid(findUser.getId())
                .score(5)
                .build());

        return findUser;
    }

    // 스터디 가입신청 목록 조회 메서드
    public StudyMemberApplyListAndCursorIdxResponse applyListStudyMember(Long studyInfoId, Long cursorIdx, Long limit) {

        // 스터디 조회 예외처리
        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        limit = Math.min(limit, MAX_LIMIT);

        // 대기중인 멤버들의 신청목록 조회
        List<StudyMemberApplyResponse> applyList = studyMemberRepository.findStudyApplyListByStudyInfoId_CursorPaging(studyInfoId, cursorIdx, limit);

        // 대기중인 멤버가 없는 경우 빈 배열 반환
        if (applyList.isEmpty()) {

            return StudyMemberApplyListAndCursorIdxResponse.builder()
                    .applyList(Collections.emptyList())
                    .studyTopic(studyInfo.getTopic())
                    .build();
        }

        StudyMemberApplyListAndCursorIdxResponse response = StudyMemberApplyListAndCursorIdxResponse.builder()
                .applyList(applyList)
                .studyTopic(studyInfo.getTopic())
                .build();

        response.setNextCursorIdx();

        return response;
    }


    // 스터디 멤버에게 알림 메서드
    public void notifyToStudyMember(Long studyInfoId, Long notifyUserId, MessageRequest messageRequest) {

        // 스터디 조회
        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        // 알림 받을 user 조회
        User notifyUser = userService.findUserByIdOrThrowException(notifyUserId);

        // 알림 비동기처리
        eventPublisher.publishEvent(NotifyMemberEvent.builder()
                .isPushAlarmYn(notifyUser.isPushAlarmYn())
                .notifyUserId(notifyUserId)
                .studyInfoId(studyInfo.getId())
                .studyTopic(studyInfo.getTopic())
                .message(messageRequest.getMessage())
                .build());

    }


    // 스터디 팀장에게 알림 메서드
    public void notifyToStudyLeader(Long studyInfoId, UserInfoResponse userInfo, MessageRequest messageRequest) {

        // 스터디 조회
        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        // 알림 받을 user 조회
        User notifyUser = userService.findUserByIdOrThrowException(studyInfo.getUserId());

        // 알림 비동기처리
        eventPublisher.publishEvent(NotifyLeaderEvent.builder()
                .isPushAlarmYn(notifyUser.isPushAlarmYn())
                .notifyUserId(studyInfo.getUserId())
                .studyInfoId(studyInfo.getId())
                .studyMemberName(userInfo.getName())
                .message(messageRequest.getMessage())
                .build());
    }


    // 대기중인 스터디원인지 확인 메서드
    private void checkMemberStatusWaiting(StudyMember studyMember) {

        if (studyMember.getStatus() != STUDY_WAITING) {
            log.warn(">>>> {} : {} <<<<", studyMember.getUserId(), ExceptionMessage.STUDY_WAITING_NOT_MEMBER);
            throw new MemberException(ExceptionMessage.STUDY_WAITING_NOT_MEMBER);
        }
    }

    public StudyMember findStudyMemberByStudyInfoIdAndUserIdOrThrowException(Long studyInfoId, Long userId) {
        return studyMemberRepository.findByStudyInfoIdAndUserId(studyInfoId, userId)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", userId, ExceptionMessage.USER_NOT_STUDY_MEMBER);
                    return new MemberException(ExceptionMessage.USER_NOT_STUDY_MEMBER);
                });
    }


}
