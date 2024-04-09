package com.example.backend.study.api.controller.todo.response;

import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudyTodoStatusResponse {

    private Long userId;  // 스터디멤버 Id

    private StudyTodoStatus status;  // to do 진행 상황

}
