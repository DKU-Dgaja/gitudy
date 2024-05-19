package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.backend.domain.define.study.todo.info.QStudyTodo.studyTodo;
import static com.example.backend.domain.define.study.todo.mapping.QStudyTodoMapping.studyTodoMapping;

@Component
@RequiredArgsConstructor
public class StudyTodoRepositoryImpl implements StudyTodoRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<StudyTodoResponse> findStudyTodoListByStudyInfoId_CursorPaging(Long studyInfoId, Long cursorIdx, Long limit) {
        // Querydsl 쿼리 생성
        JPAQuery<StudyTodoResponse> query = queryFactory
                .select(Projections.constructor(StudyTodoResponse.class,
                        studyTodo.id,
                        studyTodo.studyInfoId,
                        studyTodo.title,
                        studyTodo.detail,
                        studyTodo.todoLink,
                        studyTodo.todoDate))
                .from(studyTodo)
                .where(studyTodo.studyInfoId.eq(studyInfoId))
                .orderBy(studyTodo.id.desc()); // to do Id가 큰값부터 내림차순 (최신항목순)

        // cursorIdx가 null이 아닐때 해당 ID 이하의 데이터를 조회
        if (cursorIdx != null) {
            query = query.where(studyTodo.id.lt(cursorIdx));
        }

        // 정해진 limit만큼 데이터를 가져온다
        return query.limit(limit)
                .fetch();
    }

    @Override
    public void deleteTodoIdsByStudyInfoIdAndUserId(Long studyInfoId, Long userId) {

        // 삭제할 StudyTodoMapping의 ID 조회
        List<Long> todoMappingIds = queryFactory
                .select(studyTodoMapping.id)
                .from(studyTodoMapping)
                .join(studyTodo).on(studyTodoMapping.todoId.eq(studyTodo.id))
                .where(studyTodo.studyInfoId.eq(studyInfoId)
                        .and(studyTodo.todoDate.after(LocalDate.now()))
                        .and(studyTodoMapping.userId.eq(userId)))
                .fetch();

        // 찾은 ID를 사용하여 StudyTodoMapping 삭제
        if (!todoMappingIds.isEmpty()) {
            queryFactory
                    .delete(studyTodoMapping)
                    .where(studyTodoMapping.id.in(todoMappingIds))
                    .execute();
        }
    }

    @Override
    public Optional<StudyTodo> findStudyTodoByStudyInfoIdWithEarliestDueDate(Long studyInfoId) {
        LocalDate currentDate = LocalDate.now();

        StudyTodo result = queryFactory.selectFrom(studyTodo)
                .where(studyTodo.studyInfoId.eq(studyInfoId)
                        .and(studyTodo.todoDate.goe(currentDate)))
                .orderBy(studyTodo.todoDate.asc())
                .fetchFirst();

        return Optional.ofNullable(result);
    }
}
