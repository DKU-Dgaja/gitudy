package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.example.backend.domain.define.study.todo.info.QStudyTodo.studyTodo;

@Component
@RequiredArgsConstructor
public class StudyTodoRepositoryImpl implements StudyTodoRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<StudyTodoResponse> findStudyTodoListByStudyInfoId(Long studyInfoId, Long cursorIdx, Long limit) {
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
}
