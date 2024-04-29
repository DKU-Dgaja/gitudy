package com.example.backend.domain.define.study.member;

import com.example.backend.domain.define.study.member.event.NotifyMemberEvent;
import com.example.backend.domain.define.study.member.event.ResignMemberEvent;

public class MemberEventFixture {

    public static ResignMemberEvent generateApplyMemberEvent(Long userId) {
        return ResignMemberEvent.builder()
                .resignMemberId(userId)
                .studyInfoTopic("스터디제목")
                .build();
    }

    public static NotifyMemberEvent generateNotifyMemberEvent(Long userId) {
        return NotifyMemberEvent.builder()
                .notifyUserId(userId)
                .studyTopic("스터디제목")
                .message("전달할 메세지")
                .build();
    }

}
