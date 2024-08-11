package com.example.backend.domain.define.study.member.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyMemberEvent {

    private boolean isPushAlarmYn; // fcm 알림여부 확인

    private Long studyInfoId;

    private Long notifyUserId;   // 알림받는 UserId

    private String studyTopic;   // 알림보내는 스터디

    private String message;     // 전달할 메세지
}
