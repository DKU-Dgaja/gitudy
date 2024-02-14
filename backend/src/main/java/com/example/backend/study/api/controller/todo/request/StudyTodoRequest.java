package com.example.backend.study.api.controller.todo.request;

import com.example.backend.domain.define.study.todo.info.StudyTodo;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyTodoRequest {

    @Size(max = 20, message = "제목 20자 이내")
    private String title;

    @Size(max = 50, message = "설명 50자 이내")
    private String detail;

    private String todoLink;

    private LocalDate todoDate;


    public StudyTodo StudyTodoRegister(Long studyInfoId) {
        return StudyTodo.builder()
                .studyInfoId(studyInfoId)
                .title(getTitle())
                .detail(getDetail())
                .todoLink(getTodoLink())
                .todoDate(getTodoDate())
                .build();
    }



}
