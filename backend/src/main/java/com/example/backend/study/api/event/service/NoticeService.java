package com.example.backend.study.api.event.service;


import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.domain.define.notice.Notice;
import com.example.backend.domain.define.notice.repository.NoticeRepository;
import com.example.backend.domain.define.study.info.event.ApplyApproveRefuseMemberEvent;
import com.example.backend.domain.define.study.info.event.ApplyMemberEvent;
import com.example.backend.domain.define.study.member.event.NotifyMemberEvent;
import com.example.backend.domain.define.study.member.event.ResignMemberEvent;
import com.example.backend.domain.define.study.member.event.WithdrawalMemberEvent;
import com.example.backend.domain.define.study.todo.event.TodoRegisterMemberEvent;
import com.example.backend.domain.define.study.todo.event.TodoUpdateMemberEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;


    // 가입신청 알림 생성 메서드
    @Transactional
    public void ApplyMemberNotice(ApplyMemberEvent event) {
        Notice notice = Notice.builder()
                .userId(event.getStudyLeaderId())
                .studyInfoId(event.getStudyInfoId())
                .title("[" + event.getStudyTopic() + "] 스터디 신청")
                .message(event.getName() + "님이 스터디를 신청했습니다.\n" + "프로필과 메시지를 확인 후, 수락해주세요!")
                .localDateTime(LocalDateTime.now())
                .build();
        noticeRepository.save(notice);
    }

    // 강퇴 알림 생성 메서드
    @Transactional
    public void ResignMemberNotice(ResignMemberEvent event) {
        Notice notice = Notice.builder()
                .userId(event.getResignMemberId())
                .title("알림 - 추후 변경예정")
                .message("[" + event.getStudyInfoTopic() + "] 스터디에서 강퇴 되었습니다.")
                .localDateTime(LocalDateTime.now())
                .build();
        noticeRepository.save(notice);
    }

    // 탈퇴 알림 생성 메서드
    @Transactional
    public void WithdrawalMemberNotice(WithdrawalMemberEvent event) {
        Notice notice = Notice.builder()
                .userId(event.getStudyLeaderId())
                .title("[" + event.getStudyInfoTopic() + "] 탈퇴")
                .message(event.getWithdrawalMemberName() + "님이 탈퇴 하셨습니다.")
                .localDateTime(LocalDateTime.now())
                .build();
        noticeRepository.save(notice);
    }

    // 가입승인/거절 알림 생성 메서드
    @Transactional
    public void ApplyApproveRefuseMemberNotice(ApplyApproveRefuseMemberEvent event) {

        Notice notice;
        String title;
        String message;
        if (event.isApprove()) { // 스터디장의 승인여부
            title = "[ " + event.getStudyTopic() + " ] 스터디 신청";
            message = String.format("축하합니다! '%s'님 가입이 승인되었습니다!", event.getName());

        } else {
            title = "[" + event.getStudyTopic() + "] 스터디 신청";
            message = String.format("안타깝게도 '%s'님은 가입이 거절되었습니다.", event.getName());

        }
        notice = Notice.builder()
                .userId(event.getApplyUserId())
                .title(title)
                .message(message)
                .localDateTime(LocalDateTime.now())
                .build();
        noticeRepository.save(notice);
    }

    // 스터디 투두 등록시 알림 생성 메서드
    @Transactional
    public void TodoRegisterMemberNotice(TodoRegisterMemberEvent event) {

        // 활동중인 멤버 리스트에 있는 각 ID에 대해 알림 생성
        for (Long memberId : event.getActivesMemberIds()) {
            Notice notice = Notice.builder()
                    .userId(memberId)
                    .title("[" + event.getStudyTopic() + "] 새로운 Todo")
                    .message("메세지 추후 변경 예정")
                    .localDateTime(LocalDateTime.now())
                    .build();
            noticeRepository.save(notice);
        }
    }

    // 스터디 투두 수정시 알림 생성 메서드
    @Transactional
    public void TodoUpdateMemberNotice(TodoUpdateMemberEvent event) {

        // 활동중인 멤버 리스트에 있는 각 ID에 대해 알림 생성
        for (Long memberId : event.getActivesMemberIds()) {
            Notice notice = Notice.builder()
                    .userId(memberId)
                    .title("[" + event.getStudyTopic() + "] 스터디의 Todo [" + event.getTodoTitle() + "]가 변경 되었습니다.")
                    .message("메세지 추후 변경 예정")
                    .localDateTime(LocalDateTime.now())
                    .build();
            noticeRepository.save(notice);
        }
    }

    // 팀장이 팀원에게 알림 생성 메서드
    @Transactional
    public void NotifyMemberNotice(NotifyMemberEvent event) {

        Notice notice = Notice.builder()
                .userId(event.getNotifyUserId())
                .title("[" + event.getStudyTopic() + "] 스터디 에서 알림")
                .message(event.getMessage())
                .localDateTime(LocalDateTime.now())
                .build();
        noticeRepository.save(notice);
    }
}
