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

    private Long studyInfoId;

    @Size(max = 20, message = "제목 20자 이내")
    private String title;

    @Size(max = 50, message = "설명 50자 이내")
    private String detail;

    private String todoLink;

    private LocalDate endTime;


    public StudyTodo registerStudyTodo() {
        return StudyTodo.builder()
                .studyInfoId(getStudyInfoId())
                .title(getTitle())
                .detail(getDetail())
                .todoLink(getTodoLink())
                .endTime(getEndTime())
                .build();
    }



}
