package com.example.backend.study.api.controller.todo.request;

import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class StudyTodoRequest {

    @NotNull
    @JsonProperty("studyInfoId")
    private Long studyInfoId;

    @Size(max = 20, message = "제목 20자 이내")
    @JsonProperty("title")
    private String title;

    @Size(max = 50, message = "설명 50자 이내")
    @JsonProperty("detail")
    private String detail;

    @JsonProperty("todoLink")
    private String todoLink;

    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonProperty("endTime")
   // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endTime;


    @NotNull
    @JsonProperty("todoId")
    private Long todoId;

    @NotNull
    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("status")
    @Enumerated(EnumType.STRING)
    private StudyTodoStatus status;


    public StudyTodo registerStudyTodo() {
        return StudyTodo.builder()
                .id(getTodoId())
                .title(getTitle())
                .detail(getDetail())
                .todoLink(getTodoLink())
                .endTime(getEndTime())
                .build();
    }

    public StudyTodoMapping registerStudyTodoMapping() {
        return StudyTodoMapping.builder()
                .todoId(getTodoId())
                .userId(getUserId())
                .status(getStatus())
                .build();
    }

}
