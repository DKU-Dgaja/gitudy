package com.example.backend.study.api.controller.todo.request;

import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.validation.constraints.Size;
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


    @Size(max = 20, message = "제목 20자 이내")
    private String title;        // To do 이름

    @Size(max = 50, message = "설명 50자 이내")
    private String detail;       // To do 설명


    private String todoLink;     // To do 링크


    private LocalDate todoDate;  // To do 날짜


    private StudyTodoStatus status; // To do 진행상황

    @Builder
    public StudyTodoUpdateRequest(String title, String detail, String todoLink,
                                  LocalDate todoDate, StudyTodoStatus status) {

        this.title = title;
        this.detail = detail;
        this.todoLink = todoLink;
        this.todoDate = todoDate;
        this.status = status;
    }


}
