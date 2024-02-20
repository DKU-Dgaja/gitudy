package com.example.backend.domain.define.study.todo;

import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;

import java.time.LocalDate;

import static com.example.backend.study.api.service.todo.StudyTodoServiceTest.*;

public class StudyTodoFixture {


    //테스트용 studyTodo 생성
    public static StudyTodo createStudyTodo(Long studyInfoId) {
        return StudyTodo.builder()
                .studyInfoId(studyInfoId)
                .title(expectedTitle)
                .detail(expectedDetail)
                .todoLink(expectedTodoLink)
                .todoDate(expectedTodoDate)
                .build();
    }

    // 테스트용  studyTodoMapping 생성
    public static StudyTodoMapping createStudyTodoMapping(Long todoId, Long userId) {
        return StudyTodoMapping.builder()
                .todoId(todoId)
                .userId(userId)
                .status(expectedStatus)
                .build();
    }


    // 테스트용 To do 등록
    public static StudyTodoRequest generateStudyTodoRequest() {
        return StudyTodoRequest.builder()
                .title(expectedTitle)
                .detail(expectedDetail)
                .todoLink(expectedTodoLink)
                .todoDate(expectedTodoDate)
                .build();
    }

    // 테스트용 To do 수정
    public static StudyTodoUpdateRequest updateStudyTodoRequest(String title, String detail, String todoLink, LocalDate todoDate) {
        return StudyTodoUpdateRequest.builder()
                .title(title)
                .detail(detail)
                .todoLink(todoLink)
                .todoDate(todoDate)
                .build();
    }

    // 테스트용 To do 조회
    public static StudyTodoResponse createStudyTodoResponse(Long id, Long studyInfoId, String title, String detail, String todoLink, LocalDate todoDate) {
        return new StudyTodoResponse(id, studyInfoId, title, detail, todoLink, todoDate);
    }


    //테스트용 studyTodo List 생성
    public static StudyTodo createStudyTodoList(Long studyInfoId, String title, String detail, String todoLink, LocalDate todoDate) {

        return StudyTodo.builder()
                .studyInfoId(studyInfoId)
                .title(title)
                .detail(detail)
                .todoLink(todoLink)
                .todoDate(todoDate)
                .build();
    }




}