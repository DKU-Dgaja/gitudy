package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.study.api.controller.todo.response.StudyTodoWithCommitsResponse;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.backend.domain.define.account.user.QUser.user;
import static com.example.backend.domain.define.study.commit.QStudyCommit.studyCommit;
import static com.example.backend.domain.define.study.todo.info.QStudyTodo.studyTodo;
import static com.example.backend.domain.define.study.todo.mapping.QStudyTodoMapping.studyTodoMapping;

@Component
@RequiredArgsConstructor
public class StudyTodoRepositoryImpl implements StudyTodoRepositoryCustom {
    private final static ZoneId KOREA_SEOUL = ZoneId.of("Asia/Seoul");
    private final JPAQueryFactory queryFactory;

    @Override
    public List<StudyTodoWithCommitsResponse> findStudyTodoListByStudyInfoId_CursorPaging(Long studyInfoId, Long cursorIdx, Long limit) {

        // StudyTodo 조회
        List<StudyTodo> todos = queryFactory
                .selectFrom(studyTodo)
                .where(studyTodo.studyInfoId.eq(studyInfoId))
                .where(cursorIdx != null ? studyTodo.id.lt(cursorIdx) : null)
                .orderBy(studyTodo.id.desc())
                .limit(limit)
                .fetch();

        // StudyTodo의 ID 리스트를 가져온다
        List<Long> todoIds = todos.stream().map(StudyTodo::getId).collect(Collectors.toList());

        // StudyCommit 리스트 조회 (User 정보 포함)
        List<Tuple> commitResults = queryFactory
                .select(studyCommit, user.name, user.profileImageUrl)
                .from(studyCommit)
                .join(user).on(user.id.eq(studyCommit.userId))
                .where(studyCommit.studyTodoId.in(todoIds))
                .fetch();

        // StudyCommit을 StudyTodo ID로 그룹화
        Map<Long, List<CommitInfoResponse>> commitMap = new HashMap<>();

        for (Tuple tuple : commitResults) {
            StudyCommit commit = tuple.get(studyCommit);
            String username = tuple.get(user.name);
            String profileImageUrl = tuple.get(user.profileImageUrl);

            commitMap.computeIfAbsent(commit.getStudyTodoId(), k -> new ArrayList<>())
                    .add(CommitInfoResponse.of(commit, username, profileImageUrl));
        }

        // StudyTodo와 커밋 리스트를 조합하여 StudyTodoWithCommitsResponse 리스트를 생성
        return todos.stream()
                .map(todo -> StudyTodoWithCommitsResponse.of(todo, commitMap.getOrDefault(todo.getId(), new ArrayList<>())))
                .collect(Collectors.toList());
    }


    @Override
    public void deleteTodoIdsByStudyInfoIdAndUserId(Long studyInfoId, Long userId) {

        // 삭제할 StudyTodoMapping의 ID 조회
        List<Long> todoMappingIds = queryFactory
                .select(studyTodoMapping.id)
                .from(studyTodoMapping)
                .join(studyTodo).on(studyTodoMapping.todoId.eq(studyTodo.id))
                .where(studyTodo.studyInfoId.eq(studyInfoId)
                        .and(studyTodo.todoDate.after(LocalDate.now(KOREA_SEOUL)))
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
        LocalDate currentDate = LocalDate.now(KOREA_SEOUL);

        StudyTodo result = queryFactory.selectFrom(studyTodo)
                .where(studyTodo.studyInfoId.eq(studyInfoId)
                        .and(studyTodo.todoDate.goe(currentDate)))
                .orderBy(studyTodo.todoDate.asc())
                .fetchFirst();

        return Optional.ofNullable(result);
    }
}
