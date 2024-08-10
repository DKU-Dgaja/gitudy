package com.example.backend.domain.define.study.member.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResignMemberEvent {

    private boolean isPushAlarmYn;  // fcm 알림여부

    private Long studyInfoId;

    private Long resignMemberId;  // 강퇴당한 멤버

    private String studyInfoTopic; // 강퇴당한 스터디
}
