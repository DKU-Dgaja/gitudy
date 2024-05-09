package com.example.backend.domain.define.study.member;

import com.example.backend.domain.define.study.member.event.NotifyLeaderEvent;
import com.example.backend.domain.define.study.member.event.NotifyMemberEvent;
import com.example.backend.domain.define.study.member.event.ResignMemberEvent;
import com.example.backend.domain.define.study.member.event.WithdrawalMemberEvent;

public class MemberEventFixture {

    public static ResignMemberEvent generateApplyMemberEvent(Long userId) {
        return ResignMemberEvent.builder()
                .resignMemberId(userId)
                .isPushAlarmYn(true)
                .studyInfoTopic("스터디제목")
                .build();
    }

    public static WithdrawalMemberEvent generateWithdrawalMemberEvent(Long userId) {
        return WithdrawalMemberEvent.builder()
                .studyLeaderId(userId)
                .isPushAlarmYn(true)
                .studyInfoTopic("스터디제목")
                .withdrawalMemberName("이름")
                .build();
    }

    public static NotifyMemberEvent generateNotifyMemberEvent(Long userId) {
        return NotifyMemberEvent.builder()
                .notifyUserId(userId)
                .isPushAlarmYn(true)
                .studyTopic("스터디제목")
                .message("전달할 메세지")
                .build();
    }

    public static NotifyLeaderEvent generateNotifyLeaderEvent(Long userId) {
        return NotifyLeaderEvent.builder()
                .notifyUserId(userId)
                .isPushAlarmYn(true)
                .studyTopic("스터디제목")
                .message("전달할 메세지")
                .build();
    }

}
