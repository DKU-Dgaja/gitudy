package com.example.backend.study.api.event.service;


import com.example.backend.auth.api.controller.auth.response.UserInfoResponse;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.notice.NoticeException;
import com.example.backend.domain.define.notice.Notice;
import com.example.backend.domain.define.notice.repository.NoticeRepository;
import com.example.backend.domain.define.study.commit.event.CommitApproveEvent;
import com.example.backend.domain.define.study.commit.event.CommitRefuseEvent;
import com.example.backend.domain.define.study.info.event.ApplyApproveRefuseMemberEvent;
import com.example.backend.domain.define.study.info.event.ApplyMemberEvent;
import com.example.backend.domain.define.study.member.event.NotifyLeaderEvent;
import com.example.backend.domain.define.study.member.event.NotifyMemberEvent;
import com.example.backend.domain.define.study.member.event.ResignMemberEvent;
import com.example.backend.domain.define.study.member.event.WithdrawalMemberEvent;
import com.example.backend.domain.define.study.todo.event.TodoRegisterMemberEvent;
import com.example.backend.domain.define.study.todo.event.TodoUpdateMemberEvent;
import com.example.backend.study.api.event.controller.response.UserNoticeList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;


    // 알림 목록 조회
    public List<UserNoticeList> ReadNoticeList(UserInfoResponse userInfo, LocalDateTime time, Long limit) {

        // 현재 시간을 기본값으로 설정
        if (time == null) {
            time = LocalDateTime.now();
        }

        List<Notice> noticeList = noticeRepository.findUserNoticeListByUserId(userInfo.getUserId(), time, limit);

        return convertNoticeListToUserNoticeList(noticeList);
    }

    private List<UserNoticeList> convertNoticeListToUserNoticeList(List<Notice> noticeList) {
        return noticeList.stream()
                .map(notice -> UserNoticeList.builder()
                        .id(notice.getId())
                        .studyInfoId(notice.getStudyInfoId())
                        .title(notice.getTitle())
                        .message(notice.getMessage())
                        .localDateTime(notice.getLocalDateTime())
                        .build())
                .toList();
    }

    // 특정알림 삭제
    @Transactional
    public void DeleteNotice(String id) {

        // 알림 예외처리
        Notice notice = findByIdOrThrowNoticeException(id);

        noticeRepository.deleteById(notice.getId());
    }

    // 유저 알림 모두 삭제
    @Transactional
    public void DeleteNoticeAll(Long userId) {

        // 알림 예외처리
        findByIdOrThrowNoticesException(userId);

        noticeRepository.deleteAllByUserId(userId);
    }


    // 가입신청 알림 생성 메서드
    @Transactional
    public void ApplyMemberNotice(ApplyMemberEvent event) {
        Notice notice = Notice.builder()
                .userId(event.getStudyLeaderId())
                .studyInfoId(event.getStudyInfoId())
                .title("[" + event.getStudyTopic() + "] 스터디 가입 신청")
                .message("새로운 스터디 가입 신청자가 있습니다. 가입 목록 확인 후 , 수락해주세요!")
                .localDateTime(LocalDateTime.now())
                .build();
        noticeRepository.save(notice);
    }

    // 강퇴 알림 생성 메서드 *ver2*
    @Transactional
    public void ResignMemberNotice(ResignMemberEvent event) {
        Notice notice = Notice.builder()
                .userId(event.getResignMemberId())
                .studyInfoId(event.getStudyInfoId())
                .title("[" + event.getStudyInfoTopic() + "] 스터디 알림")
                .message("[" + event.getStudyInfoTopic() + "]에서 강퇴 되었습니다.")
                .localDateTime(LocalDateTime.now())
                .build();
        noticeRepository.save(notice);
    }

    // 탈퇴 알림 생성 메서드  *ver2*
    @Transactional
    public void WithdrawalMemberNotice(WithdrawalMemberEvent event) {
        Notice notice = Notice.builder()
                .userId(event.getStudyLeaderId())
                .studyInfoId(event.getStudyInfoId())
                .title("[" + event.getStudyInfoTopic() + "] 스터디 알림")
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
            title = "[ " + event.getStudyTopic() + " ] 스터디 가입 완료";
            message = "스터디 가입이 완료되었습니다. 바로 스터디 활동을 시작해보세요!";

        } else {
            title = "[" + event.getStudyTopic() + "] 스터디 가입 실패";
            message = String.format("'%s'님은 가입이 거절되었습니다.", event.getName());

        }
        notice = Notice.builder()
                .userId(event.getApplyUserId())
                .studyInfoId(event.getStudyInfoId())
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
                    .studyInfoId(event.getStudyInfoId())
                    .title("[" + event.getStudyTopic() + "] TO-DO 업데이트")
                    .message("새로운 TO-DO가 업데이트 되었습니다. 지금 확인해보세요!")
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
                    .studyInfoId(event.getStudyInfoId())
                    .title("[" + event.getStudyTopic() + "] TO-DO 업데이트")
                    .message("TO-DO가 업데이트 되었습니다. 지금 확인해보세요!")
                    .localDateTime(LocalDateTime.now())
                    .build();
            noticeRepository.save(notice);
        }
    }

    // 팀장이 팀원에게 알림 생성 메서드  *ver2*
    @Transactional
    public void NotifyMemberNotice(NotifyMemberEvent event) {

        Notice notice = Notice.builder()
                .userId(event.getNotifyUserId())
                .studyInfoId(event.getStudyInfoId())
                .title("[" + event.getStudyTopic() + "] 스터디 에서 알림")
                .message(event.getMessage())
                .localDateTime(LocalDateTime.now())
                .build();
        noticeRepository.save(notice);
    }

    // 팀원이 팀장에게 알림 생성 메서드  *ver2*
    @Transactional
    public void NotifyLeaderNotice(NotifyLeaderEvent event) {

        Notice notice = Notice.builder()
                .userId(event.getNotifyUserId())
                .studyInfoId(event.getStudyInfoId())
                .title("[" + event.getStudyTopic() + "] 스터디 에서 알림")
                .message(event.getStudyMemberName() + "님의 알림" + event.getMessage())
                .localDateTime(LocalDateTime.now())
                .build();
        noticeRepository.save(notice);
    }

    // 팀장의 커밋 승인 알림 생성 메서드
    @Transactional
    public void StudyCommitApproveNotice(CommitApproveEvent event) {

        Notice notice = Notice.builder()
                .userId(event.getUserId())
                .studyInfoId(event.getStudyInfoId())
                .title("[" + event.getStudyTopic() + "] 커밋 승인")
                .message("TO-DO [" + event.getStudyTodoTopic() + "]에 대한 커밋이 승인되었습니다.\n팀장의 커밋 리뷰를 확인해보세요!")
                .localDateTime(LocalDateTime.now())
                .build();
        noticeRepository.save(notice);
    }

    @Transactional
    public void StudyCommitRefuseNotice(CommitRefuseEvent event) {

        Notice notice = Notice.builder()
                .userId(event.getUserId())
                .studyInfoId(event.getStudyInfoId())
                .title("[" + event.getStudyTopic() + "] 커밋 반려")
                .message("TO-DO [" + event.getStudyTodoTopic() + "]에 대한 커밋이 반려되었습니다.\n팀장의 커밋 리뷰를 확인해보세요!")
                .localDateTime(LocalDateTime.now())
                .build();
        noticeRepository.save(notice);
    }


    public Notice findByIdOrThrowNoticeException(String id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", id, ExceptionMessage.NOTICE_NOT_FOUND);
                    return new NoticeException(ExceptionMessage.NOTICE_NOT_FOUND);
                });

    }

    public void findByIdOrThrowNoticesException(Long userId) {
        List<Notice> notices = noticeRepository.findByUserId(userId);
        if (notices.isEmpty()) {
            log.warn(">>>> {} : {} <<<<", userId, ExceptionMessage.NOTICE_NOT_FOUND);
            throw new NoticeException(ExceptionMessage.NOTICE_NOT_FOUND);
        }
    }

}
