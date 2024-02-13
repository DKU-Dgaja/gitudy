package com.example.backend.domain.define.study.todo;

import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;

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
        StudyTodoRequest request = new StudyTodoRequest();

        request.setStudyInfoId(expectedStudyInfoId);
        request.setTitle(expectedTitle);
        request.setDetail(expectedDetail);
        request.setTodoLink(expectedTodoLink);
        request.setTodoDate(expectedTodoDate);

        return request;
    }

    // 테스트용 To do 수정
    public static StudyTodoUpdateRequest updateStudyTodoRequest() {
        StudyTodoUpdateRequest request = new StudyTodoUpdateRequest();

        request.setTitle(updatedTitle);
        request.setDetail(updatedDetail);
        request.setTodoLink(updatedTodoLink);
        request.setTodoDate(updatedTodoDate);
        request.setStatus(updatedStatus);

        return request;
    }

    // 테스트용 To do 조회
    public static StudyTodoResponse readStudyTodoResponse() {
        return StudyTodoResponse.of(StudyTodo.builder()
                .id(expectedTodoId)
                .studyInfoId(expectedStudyInfoId)
                .title(expectedTitle)
                .detail(expectedDetail)
                .todoLink(expectedTodoLink)
                .todoDate(expectedTodoDate)
                .build());
    }

}
