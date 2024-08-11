package com.example.backend.domain.define.study.info.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyApproveRefuseMemberEvent {

    private boolean isPushAlarmYn; // fcm 알림 여부

    private Long studyInfoId;

    private boolean approve;   // 승인 여부

    private Long applyUserId;  // 가입신청자 Id

    private String studyTopic;  // 스터디 제목

    private String name;      // 가입신청자 이름
}
