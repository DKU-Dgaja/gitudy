package com.example.backend.study.api.controller.todo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@AllArgsConstructor
@Getter
@Builder
public class StudyTodoListAndCursorIdxResponse {

    private List<StudyTodoWithCommitsResponse> todoList;  // To do 정보

    private Long cursorIdx;    // 다음위치 커서


    public void setNextCursorIdx() {
        cursorIdx = todoList == null || todoList.isEmpty() ?
                0L : todoList.get(todoList.size() - 1).getId();

    }
}
