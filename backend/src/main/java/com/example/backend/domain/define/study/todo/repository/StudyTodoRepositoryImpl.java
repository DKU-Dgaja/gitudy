package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;
import com.example.backend.study.api.controller.todo.response.StudyTodoWithCommitsResponse;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.backend.domain.define.study.commit.QStudyCommit.studyCommit;
import static com.example.backend.domain.define.study.todo.info.QStudyTodo.studyTodo;
import static com.example.backend.domain.define.study.todo.mapping.QStudyTodoMapping.studyTodoMapping;
import static com.querydsl.core.group.GroupBy.groupBy;

@Component
@RequiredArgsConstructor
public class StudyTodoRepositoryImpl implements StudyTodoRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<StudyTodoWithCommitsResponse> findStudyTodoListByStudyInfoId_CursorPaging(Long studyInfoId, Long cursorIdx, Long limit) {

        // StudyTodo 기본 정보만 가져오는 쿼리
        JPAQuery<StudyTodo> todoQuery = queryFactory
                .selectFrom(studyTodo)
                .where(studyTodo.studyInfoId.eq(studyInfoId))
                .orderBy(studyTodo.id.desc());

        // cursorIdx가 null이 아닐 때 해당 ID 이하의 데이터를 조회
        if (cursorIdx != null) {
            todoQuery = todoQuery.where(studyTodo.id.lt(cursorIdx));
        }

        // 정해진 limit만큼 데이터를 가져온다
        List<StudyTodo> todos = todoQuery.limit(limit).fetch();

        // StudyTodo의 ID 리스트를 가져온다
        List<Long> todoIds = todos.stream().map(StudyTodo::getId).collect(Collectors.toList());

        // StudyCommit 리스트를 가져오는 쿼리
        List<StudyCommit> commits = queryFactory
                .selectFrom(studyCommit)
                .where(studyCommit.studyTodoId.in(todoIds))
                .fetch();

        // StudyCommit을 StudyTodo ID로 그룹화
        Map<Long, List<CommitInfoResponse>> commitMap = commits.stream()
                .map(CommitInfoResponse::of)
                .collect(Collectors.groupingBy(CommitInfoResponse::getStudyTodoId));

        // StudyTodo와 커밋 리스트를 조합하여 StudyTodoWithCommitsResponse 리스트를 생성
        List<StudyTodoWithCommitsResponse> responses = todos.stream()
                .map(todo -> StudyTodoWithCommitsResponse.of(todo, commitMap.getOrDefault(todo.getId(), List.of())))
                .collect(Collectors.toList());

        return responses;
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
