package com.example.backend.study.api.controller.todo.response;


import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class StudyTodoResponse {

    private Long id;             // to doid

    private Long studyInfoId;    // 스터티 Id

    @Size(max = 20, message = "제목 20자 이내")
    private String title;        // To do 이름

    @Size(max = 50, message = "설명 50자 이내")
    private String detail;       // To do 설명

    private String todoLink;     // To do 링크

    private LocalDate todoDate;  // To do 날짜


    @Builder
    public StudyTodoResponse(Long id, Long studyInfoId, String title, String detail, String todoLink,
                             LocalDate todoDate) {
        this.id = id;
        this.studyInfoId = studyInfoId;
        this.title = title;
        this.detail = detail;
        this.todoLink = todoLink;
        this.todoDate = todoDate;

    }

    public static StudyTodoResponse of(StudyTodo studyTodo) {
        return StudyTodoResponse.builder()
                .id(studyTodo.getId())
                .studyInfoId(studyTodo.getStudyInfoId())
                .title(studyTodo.getTitle())
                .detail(studyTodo.getDetail())
                .todoLink(studyTodo.getTodoLink())
                .todoDate(studyTodo.getTodoDate())
                .build();
    }

}
