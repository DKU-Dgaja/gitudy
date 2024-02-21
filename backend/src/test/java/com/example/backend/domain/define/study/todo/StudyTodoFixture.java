package com.example.backend.domain.define.study.todo;

import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.StudyTodoPageResponse;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    // 여러 StudyTodoResponse 객체를 생성하여 리스트로 반환
    public static List<StudyTodoResponse> createStudyTodoResponses(Long studyInfoId, int numberOfResponses) {
        List<StudyTodoResponse> responses = new ArrayList<>();
        for (long i = 1; i <= numberOfResponses; i++) {
            responses.add(StudyTodoResponse.builder()
                    .id(i)
                    .studyInfoId(studyInfoId)
                    .title(expectedTitle + i)
                    .detail(expectedDetail + i)
                    .todoLink(expectedTodoLink + i)
                    .todoDate(expectedTodoDate.plusDays(i))
                    .build());
        }
        return responses;
    }


    // 테스트용 StudyTodoPageResponse 생성
    public static StudyTodoPageResponse createStudyTodoPageResponse(List<StudyTodoResponse> todos, Long nextCursorIdx) {
        return new StudyTodoPageResponse(todos, nextCursorIdx);
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