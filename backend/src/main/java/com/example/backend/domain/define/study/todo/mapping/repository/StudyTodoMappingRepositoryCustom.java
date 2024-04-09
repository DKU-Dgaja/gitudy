package com.example.backend.domain.define.study.todo.mapping.repository;

import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;

import java.util.List;

public interface StudyTodoMappingRepositoryCustom {

    // 스터디 멤버들의 userId와 todoId로 해당 StudyTodoMapping 객체를 조회한다.
    List<StudyTodoMapping> findByTodoIdAndUserIds(Long todoId, List<Long> userIds);
}
