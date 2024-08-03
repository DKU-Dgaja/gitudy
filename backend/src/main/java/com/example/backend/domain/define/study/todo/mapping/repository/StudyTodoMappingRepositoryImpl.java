package com.example.backend.domain.define.study.todo.mapping.repository;

import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.backend.domain.define.study.todo.mapping.QStudyTodoMapping.studyTodoMapping;
import static com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus.TODO_COMPLETE;
import static com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus.TODO_INCOMPLETE;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyTodoMappingRepositoryImpl implements StudyTodoMappingRepositoryCustom {
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
                        .and(studyTodoMapping.status.eq(TODO_COMPLETE)))
                .fetchOne()
                .intValue();
    }

    @Override
    @Transactional
    public boolean updateByUserIdAndTodoId(Long userId, Long todoId, StudyTodoStatus updateStatus) {
        StudyTodoMapping todoMapping = queryFactory.selectFrom(studyTodoMapping)
                .where(studyTodoMapping.userId.eq(userId)
                        .and(studyTodoMapping.todoId.eq(todoId)))
                .fetchOne();

        if (todoMapping == null) return false;

        // 같은 투두에 두번째 업데이트일 경우 첫번째 업데이트 시의 Status를 유지한다.
        if (todoMapping.getStatus() != TODO_INCOMPLETE) return true;

        // 투두 지각 여부 업데이트
        long updatedRows = queryFactory.update(studyTodoMapping)
                .set(studyTodoMapping.status, updateStatus)
                .where(studyTodoMapping.id.eq(todoMapping.getId()))
                .execute();

        return updatedRows > 0;
    }
}
