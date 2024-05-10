package com.example.backend.domain.define.study.info.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyMemberEvent {

    private boolean isPushAlarmYn; // fcm 알림여부 확인

    private Long studyInfoId;  // 스터디 Id

    private Long studyLeaderId;  // 스터디 리더 Id

    private String studyTopic;  // 스터디 제목

    private String name;      // 가입신청자 이름

}
