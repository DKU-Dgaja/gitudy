package com.example.backend.study.api.controller.todo.request;

import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
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


    @JsonProperty("title")
    private String title;        // To do 이름

    @JsonProperty("detail")
    private String detail;       // To do 설명

    @JsonProperty("totoLink")
    private String todoLink;     // To do 링크

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonProperty("endTime")
    private LocalDate endTime;  // To do 날짜

    @JsonProperty("status")
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
