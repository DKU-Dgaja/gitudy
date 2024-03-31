package com.example.backend.domain.define.study.info.event;

import com.example.backend.domain.define.study.info.listener.event.ApplyMemberEvent;

public class StudyEventFixture {


    // 가입신청 이벤트 fixture
    public static ApplyMemberEvent generateApplyMemberEvent(Long studyLeaderId) {
        return ApplyMemberEvent.builder()
                .studyLeaderId(studyLeaderId)
                .title("title")
                .message("message")
                .build();
    }

}
