package com.example.backend.domain.define.study.todo;

import com.example.backend.domain.define.study.todo.event.TodoRegisterMemberEvent;
import com.example.backend.domain.define.study.todo.event.TodoUpdateMemberEvent;

import java.util.List;

public class TodoEventFixture {

    public static TodoRegisterMemberEvent generateTodoRegisterEvent(List<Long> userIds) {

        return TodoRegisterMemberEvent.builder()
                .pushAlarmYMemberIds(userIds)
                .activesMemberIds(userIds)
                .studyTopic("스터디제목")
                .build();
    }

    public static TodoUpdateMemberEvent generateTodoUpdateEvent(List<Long> userIds) {

        return TodoUpdateMemberEvent.builder()
                .pushAlarmYMemberIds(userIds)
                .activesMemberIds(userIds)
                .studyTopic("스터디제목")
                .todoTitle("투두 제목")
                .build();
    }
}
