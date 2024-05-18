package com.example.backend.domain.define.study.todo.mapping.repository;

import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.study.todo.mapping.QStudyTodoMapping.studyTodoMapping;

@Component
@RequiredArgsConstructor
public class StudyTodoMappingRepositoryImpl implements StudyTodoMappingRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<StudyTodoMapping> findByTodoIdAndUserIds(Long todoId, List<Long> userIds) {

        return queryFactory.selectFrom(studyTodoMapping)
                .where(studyTodoMapping.todoId.eq(todoId)
                        .and(studyTodoMapping.userId.in(userIds)))
                .fetch();
    }

    @Override
    public int findCompleteTodoMappingCountByTodoId(Long todoId) {
        return queryFactory
                .select(studyTodoMapping.count())
                .from(studyTodoMapping)
                .where(studyTodoMapping.todoId.eq(todoId)
                        .and(studyTodoMapping.status.eq(StudyTodoStatus.TODO_COMPLETE)))
                .fetchOne()
                .intValue();
    }
}
