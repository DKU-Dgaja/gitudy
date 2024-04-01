package com.example.backend.domain.define.study.info;

import com.example.backend.domain.define.study.info.listener.event.ApplyMemberEvent;

public class StudyEventFixture {


    // 가입신청 이벤트 fixture
    public static ApplyMemberEvent generateApplyMemberEvent(Long studyLeaderId) {
        return ApplyMemberEvent.builder()
                .studyLeaderId(studyLeaderId)
                .studyTopic("스터디제목")
                .name("가입자이름")
                .build();
    }

}
