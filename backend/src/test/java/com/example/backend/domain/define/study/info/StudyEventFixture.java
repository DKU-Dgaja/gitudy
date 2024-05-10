package com.example.backend.domain.define.study.info;

import com.example.backend.domain.define.study.info.event.ApplyApproveRefuseMemberEvent;
import com.example.backend.domain.define.study.info.event.ApplyMemberEvent;

public class StudyEventFixture {


    // 가입신청 이벤트 fixture
    public static ApplyMemberEvent generateApplyMemberEvent(Long studyLeaderId) {
        return ApplyMemberEvent.builder()
                .studyLeaderId(studyLeaderId)
                .isPushAlarmYn(true)
                .studyTopic("스터디제목")
                .name("가입자이름")
                .build();
    }

    public static ApplyApproveRefuseMemberEvent generateApplyApproveRefuseMemberEvent(Long applyUserId) {
        return ApplyApproveRefuseMemberEvent.builder()
                .applyUserId(applyUserId)
                .studyTopic("스터디제목")
                .isPushAlarmYn(true)
                .approve(true)
                .name("가입자이름")
                .build();
    }

}
