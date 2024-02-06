package com.example.backend.study.api.controller.todo.request;

import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class StudyTodoUpdateRequest {


    private String title;        // To do 이름

    private String detail;       // To do 설명

    private String todoLink;     // To do 링크

    private LocalDate endTime;  // To do 날짜

    private StudyTodoStatus status; // To do 진행상황

    @Builder
    public StudyTodoUpdateRequest(String title, String detail, String todoLink,
                                  LocalDate endTime, StudyTodoStatus status) {

        this.title = title;
        this.detail = detail;
        this.todoLink = todoLink;
        this.endTime = endTime;
        this.status = status;
    }


}
