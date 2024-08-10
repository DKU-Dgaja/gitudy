package com.example.backend.domain.define.study.todo.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoRegisterMemberEvent {

    private List<Long> activesMemberIds; // 활동중인 멤버들 id

    private List<Long> pushAlarmYMemberIds;  // isPushAlarmY인 멤버

    private Long studyInfoId;

    private String studyTopic; // 스터디 Topic
}
