package com.example.backend.study.api.controller.todo.response;


import com.example.backend.domain.define.study.todo.info.StudyTodo;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class StudyTodoResponse {

    private Long id;             // to doid

    private Long studyInfoId;    // 스터티 Id

    @Size(max = 20, message = "제목 20자 이내")
    private String title;        // To do 이름

    @Size(max = 50, message = "설명 50자 이내")
    private String detail;       // To do 설명

    private String todoLink;     // To do 링크

    private LocalDate todoDate;  // To do 날짜

    private String todoCode;     // To do를 식별할 6자리 코드

    private LocalDateTime createdDateTime; // To do 생성 시간

    public static StudyTodoResponse of(StudyTodo studyTodo) {
        return StudyTodoResponse.builder()
                .id(studyTodo.getId())
                .studyInfoId(studyTodo.getStudyInfoId())
                .title(studyTodo.getTitle())
                .detail(studyTodo.getDetail())
                .todoLink(studyTodo.getTodoLink())
                .todoDate(studyTodo.getTodoDate())
                .todoCode(studyTodo.getTodoCode())
                .createdDateTime(studyTodo.getCreatedDateTime())
                .build();
    }
}
