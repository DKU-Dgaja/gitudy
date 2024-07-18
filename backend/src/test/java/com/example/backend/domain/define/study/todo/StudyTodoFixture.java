package com.example.backend.domain.define.study.todo;

import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

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

    // 테스트용 studyTodoMapping 생성
    public static StudyTodoMapping createStudyTodoDefaultMapping(Long userId) {
        return StudyTodoMapping.builder()
                .userId(userId)
                .status(expectedStatus)
                .build();
    }

    // 테스트용  완료된 studyTodoMapping 생성
    public static StudyTodoMapping createCompleteStudyTodoMapping(Long todoId, Long userId) {
        return StudyTodoMapping.builder()
                .todoId(todoId)
                .userId(userId)
                .status(StudyTodoStatus.TODO_COMPLETE)
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

    // 테스트용 studyTodo 생성
    public static StudyTodo createStudyTodoCustom(Long studyInfoId, String title, String detail, String todoLink, LocalDate todoDate) {

        return StudyTodo.builder()
                .studyInfoId(studyInfoId)
                .title(title)
                .detail(detail)
                .todoLink(todoLink)
                .todoDate(todoDate)
                .build();
    }

    // 테스트용 studyTodo List 제목+스터디Id
    // StudyTodo 객체를 생성하는 메서드에 상세 설명을 추가하여 오버로드
    public static StudyTodo createStudyTodoWithTitle(Long studyInfoId, String title) {
        return StudyTodo.builder()
                .studyInfoId(studyInfoId)
                .title(title) // 테스트를 위한 기본 값 설정
                .detail(expectedDetail) // 파라미터로 전달받은 상세 설명 사용
                .todoLink(expectedTodoLink) // 테스트를 위한 기본 값 설정
                .todoDate(expectedTodoDate) // 현재 날짜로 설정
                .build();


    }

    // 테스트용 날짜 to do 설정
    public static StudyTodo createDateStudyTodo(Long studyInfoId, LocalDate todoDate) {
        return StudyTodo.builder()
                .studyInfoId(studyInfoId)
                .title(expectedTitle)
                .todoDate(todoDate)
                .build();
    }

    public static List<StudyTodo> createStudyTodoList(Long studyId, int count) {

        return IntStream.rangeClosed(1, count)
                        .mapToObj(i -> createStudyTodo(studyId))
                .toList();
    }

}