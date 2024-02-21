package com.example.backend.study.api.controller.todo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@AllArgsConstructor
@Getter
@Builder
public class StudyTodoPageResponse {

    private List<StudyTodoResponse> todos;  // To do 정보

    private Long nextCursorIdx;    // 다음위치 커서

}
