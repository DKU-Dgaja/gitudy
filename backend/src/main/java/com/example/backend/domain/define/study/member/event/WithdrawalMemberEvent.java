package com.example.backend.domain.define.study.member.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalMemberEvent {

    private boolean isPushAlarmYn;  // fcm 알림 여부

    private Long studyInfoId;

    private Long studyLeaderId;  // 스터디장 id

    private String withdrawalMemberName; // 탈퇴한 멤버 이름

    private String studyInfoTopic; // 스터디 제목
}