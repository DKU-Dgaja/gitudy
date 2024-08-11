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
public class TodoUpdateMemberEvent {

    private List<Long> activesMemberIds; // 활동중인 멤버들 id

    private List<Long> pushAlarmYMemberIds;  // isPushAlarmY인 멤버

    private Long studyInfoId;

    private String studyTopic; // 스터디 Topic

    private String todoTitle; // 변경 전 투두 이름
}
