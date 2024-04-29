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

    private List<Long> userIds;  // isPushAlarmY인 멤버

    private String studyTopic; // 스터디 Topic
}
