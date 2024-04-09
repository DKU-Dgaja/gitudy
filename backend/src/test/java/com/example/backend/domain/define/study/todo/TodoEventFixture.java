package com.example.backend.domain.define.study.todo;

import com.example.backend.domain.define.study.todo.event.TodoRegisterMemberEvent;

import java.util.List;

public class TodoEventFixture {

    public static TodoRegisterMemberEvent generateTodoRegisterEvent(List<Long> userIds) {

        return TodoRegisterMemberEvent.builder()
                .userIds(userIds)
                .studyTopic("스터디제목")
                .build();
    }
}
