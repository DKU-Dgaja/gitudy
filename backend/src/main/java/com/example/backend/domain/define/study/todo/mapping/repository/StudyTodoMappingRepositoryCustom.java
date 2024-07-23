package com.example.backend.domain.define.study.todo.mapping.repository;

import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;

import java.util.List;

public interface StudyTodoMappingRepositoryCustom {

    // 스터디 멤버들의 userId와 todoId로 해당 StudyTodoMapping 객체를 조회한다.
    List<StudyTodoMapping> findByTodoIdAndUserIds(Long todoId, List<Long> userIds);

    // todoId로 해당 To-do를 완료한 인원수를 조회한다.
    int findCompleteTodoMappingCountByTodoId(Long todoId);

    // userId와 todoId로 StudyTodoMapping 상태를 업데이트한다.
    boolean updateByUserIdAndTodoId(Long userId, Long todoId, StudyTodoStatus updateStatus);

}
