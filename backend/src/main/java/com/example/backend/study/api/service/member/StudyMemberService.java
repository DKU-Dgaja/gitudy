package com.example.backend.study.api.service.member;


import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.member.MemberException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.constant.StudyStatus;
import com.example.backend.domain.define.study.info.listener.event.ApplyMemberEvent;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.constant.StudyMemberStatus;
import com.example.backend.domain.define.study.member.listener.event.ResignMemberEvent;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.member.request.ApplyMemberMessageRequest;
import com.example.backend.study.api.controller.member.response.StudyMemberApplyListAndCursorIdxResponse;
import com.example.backend.study.api.controller.member.response.StudyMemberApplyResponse;
import com.example.backend.study.api.controller.member.response.StudyMembersResponse;
import com.example.backend.study.api.service.info.StudyInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyMemberService {

    private final UserRepository userRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final StudyTodoRepository studyTodoRepository;
    private final StudyInfoService studyInfoService;
    private final ApplicationEventPublisher eventPublisher;
    private final static int JOIN_CODE_LENGTH = 10;
    private final static Long MAX_LIMIT = 10L;

    // 스터디장 검증 메서드
    public UserInfoResponse isValidateStudyLeader(User userPrincipal, Long studyInfoId) {

        // platformId와 platformType을 이용하여 User 객체 조회
        User user = findByIdAndPlatformTypeOrThrowUserException(userPrincipal);

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
        User user = findByIdAndPlatformTypeOrThrowUserException(userPrincipal);

        // 스터디 멤버인지확인
        if (!studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(user.getId(), studyInfoId)) {
            throw new MemberException(ExceptionMessage.STUDY_NOT_MEMBER);
        }

        return UserInfoResponse.of(user);
    }


    // 스터디에 속한 스터디원 조회 (기여도별)
    public List<StudyMembersResponse> readStudyMembers(Long studyInfoId, boolean orderByScore) {

        // 스터디 조회 예외처리
        studyInfoService.findByIdOrThrowStudyInfoException(studyInfoId);

        return studyMemberRepository.findStudyMembersByStudyInfoIdOrderByScore(studyInfoId, orderByScore);
    }


    // 스터디원 강퇴 메서드
    @Transactional
    public void resignStudyMember(Long studyInfoId, Long resignUserId) {
        // 스터디 조회
        StudyInfo studyInfo = studyInfoService.findByIdOrThrowStudyInfoException(studyInfoId);

        // 강퇴시킬 스터디원 조회
        StudyMember resignMember = findByIdOrThrowMemberException(studyInfoId, resignUserId);

        // 강퇴 스터디원 상태 업데이트
        resignMember.updateStudyMemberStatus(StudyMemberStatus.STUDY_RESIGNED);

        // 강퇴 스터디원에게 할당된 마감기한이 지나지 않은 To do 삭제
        studyTodoRepository.deleteTodoIdsByStudyInfoIdAndUserId(studyInfoId, resignUserId);

        // 강퇴할 유저 조회
        User resignUser = findByIdOrThrowUserException(resignMember);

        // 강퇴 알림
        if(resignUser.isPushAlarmYn()){
            eventPublisher.publishEvent(ResignMemberEvent.builder()
                    .resignMemberId(resignUserId)
                    .studyInfoTopic(studyInfo.getTopic())
                    .build());
        }
    }


    // 스터디원 탈퇴 메서드
    @Transactional
    public void withdrawalStudyMember(Long studyInfoId, Long userId) {

        // 탈퇴 스터디원 조회
        StudyMember withdrawalMember = findByIdOrThrowMemberException(studyInfoId, userId);

        // 탈퇴 스터디원 상태 메서드
        withdrawalMember.updateStudyMemberStatus(StudyMemberStatus.STUDY_WITHDRAWAL);

        // 탈퇴 스터디원에게 할당된 마감기한이 지나지 않은 To do 삭제
        studyTodoRepository.deleteTodoIdsByStudyInfoIdAndUserId(studyInfoId, userId);

    }


    // 스터디 가입 메서드
    @Transactional
    public void applyStudyMember(UserInfoResponse user, Long studyInfoId, String joinCode, ApplyMemberMessageRequest memberMessageRequest) {

        // 스터디 조회 예외처리
        StudyInfo studyInfo = studyInfoService.findByIdOrThrowStudyInfoException(studyInfoId);

        // 비공개 스터디인 경우 joinCode 검증 (null, 맞지않을때, 10자를 넘겼을때)
        if (studyInfo.getStatus() == StudyStatus.STUDY_PRIVATE) {
            if (joinCode == null || !joinCode.equals(studyInfo.getJoinCode()) || joinCode.length() > JOIN_CODE_LENGTH) {
                log.warn(">>>> {} : {} <<<<", joinCode, ExceptionMessage.STUDY_JOIN_CODE_FAIL);
                throw new MemberException(ExceptionMessage.STUDY_JOIN_CODE_FAIL);
            }
        }
        // 스터디 멤버인지확인
        if (studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(user.getUserId(), studyInfoId)) {
            log.warn(">>>> {} : {} <<<<", user.getUserId(), ExceptionMessage.STUDY_ALREADY_MEMBER);
            throw new MemberException(ExceptionMessage.STUDY_ALREADY_MEMBER);
        }

        // 스터디 가입 신청후 이미 대기중인 멤버인지 확인
        if (studyMemberRepository.isWaitingStudyMemberByUserIdAndStudyInfoId(user.getUserId(), studyInfoId)) {
            log.warn(">>>> {} : {} <<<<", user.getUserId(), ExceptionMessage.STUDY_WAITING_MEMBER);
            throw new MemberException(ExceptionMessage.STUDY_WAITING_MEMBER);
        }

        // 강퇴되었던 멤버인지 확인
        if (studyMemberRepository.isResignedStudyMemberByUserIdAndStudyInfoId(user.getUserId(), studyInfoId)) {
            log.warn(">>>> {} : {} <<<<", user.getUserId(), ExceptionMessage.STUDY_RESIGNED_MEMBER);
            throw new MemberException(ExceptionMessage.STUDY_RESIGNED_MEMBER);
        }

        // Todo: 팀장에게 한마디 저장하는 로직 memberMessageRequest
        // Todo: 알림 페이지에 보여줄 정보 반환 로직

        // 알림 여부
        boolean notifyLeader = false;

        // 탈퇴한 멤버인지 확인, 승인 거부된 유저인지 확인
        Optional<StudyMember> existingMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfoId, user.getUserId());
        if (existingMember.isPresent()) {
            if (existingMember.get().getStatus() == StudyMemberStatus.STUDY_WITHDRAWAL || existingMember.get().getStatus() == StudyMemberStatus.STUDY_REFUSED) {
                existingMember.get().updateStudyMemberStatus(StudyMemberStatus.STUDY_WAITING); // 상태변경 후 종료
                notifyLeader = true;  // 알림설정
            }

        } else {

            // '스터디 승인 대기중인 유저' 로 생성
            StudyMember studyMember = StudyMember.waitingStudyMember(studyInfoId, user.getUserId());
            studyMemberRepository.save(studyMember);
            notifyLeader = true;

        }

        // fcm 백그라운드 알림
        if (notifyLeader) {

            User leader = userRepository.findById(studyInfo.getUserId()).orElseThrow(() -> {
                log.warn(">>>> {} : {} <<<<", studyInfo.getUserId(), ExceptionMessage.USER_NOT_STUDY_MEMBER);
                return new UserException(ExceptionMessage.USER_NOT_STUDY_MEMBER);
            });

            // 알림여부 확인
            if (leader.isPushAlarmYn()) {

                eventPublisher.publishEvent(ApplyMemberEvent.builder()
                        .studyLeaderId(leader.getId())
                        .studyTopic(studyInfo.getTopic())
                        .name(user.getName())
                        .build());
            }
        }
    }

    // 스터디 가입 취소 메서드
    @Transactional
    public void applyCancelStudyMember(UserInfoResponse user, Long studyInfoId) {

        // 스터디 조회 예외처리
        studyInfoService.findByIdOrThrowStudyInfoException(studyInfoId);

        // 대기중인 멤버인지 조회
        Optional<StudyMember> existingMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfoId, user.getUserId());

        // 멤버가 존재하지 않으면 예외 발생
        if (existingMember.isEmpty()) {
            log.warn(">>>> {} : {} <<<<", user.getUserId(), ExceptionMessage.USER_NOT_STUDY_MEMBER);
            throw new MemberException(ExceptionMessage.USER_NOT_STUDY_MEMBER);
        }

        // 멤버의 상태가 대기중이 아니면 예외 발생
        if (existingMember.get().getStatus() != StudyMemberStatus.STUDY_WAITING) {
            log.warn(">>>> {} : {} <<<<", user.getUserId(), ExceptionMessage.STUDY_WAITING_NOT_MEMBER);
            throw new MemberException(ExceptionMessage.STUDY_WAITING_NOT_MEMBER);
        }

        // 상태가 대기인 멤버 삭제
        studyMemberRepository.delete(existingMember.get());
    }

    // 스터디장의 가입 신청 승인/거부 메서드
    @Transactional
    public void leaderApproveRefuseMember(Long studyInfoId, Long applyUserId, boolean approve) {

        // 승인/거부할 스터디원 조회
        StudyMember applyMember = findByIdOrThrowMemberException(studyInfoId, applyUserId);

        // 신청대기중인 유저가 아닌경우 예외처리
        if (applyMember.getStatus() != StudyMemberStatus.STUDY_WAITING) {
            log.warn(">>>> {} : {} <<<<", applyUserId, ExceptionMessage.USER_NOT_STUDY_MEMBER);
            throw new MemberException(ExceptionMessage.USER_NOT_STUDY_MEMBER);
        }

        if (approve) {
            applyMember.updateStudyMemberStatus(StudyMemberStatus.STUDY_ACTIVE);

            // User 조회
            User findUser = findByIdOrThrowUserException(applyMember);

            // 스터디 가입 시 User +5점
            findUser.addUserScore(5);

            /*
                알림 메서드 추가
            */

        } else {
            applyMember.updateStudyMemberStatus(StudyMemberStatus.STUDY_REFUSED);

            /*
                알림 메서드 추가
             */
        }
    }

    // 스터디 가입신청 목록 조회 메서드 //Todo: 팀장에게 한마디 필드 보여줘야함 (팝업)
    public StudyMemberApplyListAndCursorIdxResponse applyListStudyMember(Long studyInfoId, Long cursorIdx, Long limit) {

        // 스터디 조회 예외처리
        studyInfoService.findByIdOrThrowStudyInfoException(studyInfoId);

        limit = Math.min(limit, MAX_LIMIT);

        // 대기중인 멤버들의 신청목록 조회
        List<StudyMemberApplyResponse> applyList = studyMemberRepository.findStudyApplyListByStudyInfoId_CursorPaging(studyInfoId, cursorIdx, limit);

        // 대기중인 멤버가 없는 경우(가입 신청x) 예외처리
        if (applyList.isEmpty()) {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_NOT_APPLY_LIST);
            throw new MemberException(ExceptionMessage.STUDY_NOT_APPLY_LIST);
        }

        StudyMemberApplyListAndCursorIdxResponse response = StudyMemberApplyListAndCursorIdxResponse.builder()
                .applyList(applyList)
                .build();

        response.setNextCursorIdx();

        return response;
    }

    public StudyMember findByIdOrThrowMemberException(Long studyInfoId, Long userId) {
        StudyMember withdrawalMember = studyMemberRepository.findByStudyInfoIdAndUserId(studyInfoId, userId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", userId, ExceptionMessage.USER_NOT_STUDY_MEMBER);
            return new MemberException(ExceptionMessage.USER_NOT_STUDY_MEMBER);
        });
        return withdrawalMember;
    }

    public User findByIdOrThrowUserException(StudyMember applyMember) {
        User findUser = userRepository.findById(applyMember.getUserId()).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", applyMember.getUserId(), ExceptionMessage.USER_NOT_FOUND);
            return new UserException(ExceptionMessage.USER_NOT_FOUND);
        });
        return findUser;
    }

    public User findByIdAndPlatformTypeOrThrowUserException(User userPrincipal) {
        User user = userRepository.findByPlatformIdAndPlatformType(userPrincipal.getPlatformId(), userPrincipal.getPlatformType()).orElseThrow(() -> {
            log.warn(">>>> {},{} : {} <<<<", userPrincipal.getPlatformId(), userPrincipal.getPlatformType(), ExceptionMessage.USER_NOT_FOUND);
            return new UserException(ExceptionMessage.USER_NOT_FOUND);
        });
        return user;
    }
}
