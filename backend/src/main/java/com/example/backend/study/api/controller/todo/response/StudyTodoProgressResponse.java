package com.example.backend.study.api.controller.todo.response;

import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyTodoProgressResponse {

    private StudyTodoResponse todo;     // 마감일이 가장 가까운 To-do 정보

    private int totalMemberCount;       // 총 스터디원 수

    private int completeMemberCount;    // 완료한 스터디원 수

    private StudyTodoStatus myStatus; // 자신의 완료 여부 상태

    public static StudyTodoProgressResponse empty() {
        return StudyTodoProgressResponse.builder()
            .todo(null)
            .totalMemberCount(0)
            .completeMemberCount(0)
            .myStatus(StudyTodoStatus.TODO_INCOMPLETE)
            .build();
    }
}
