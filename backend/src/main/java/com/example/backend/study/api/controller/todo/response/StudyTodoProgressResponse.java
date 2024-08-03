package com.example.backend.study.api.controller.todo.response;

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

    public static StudyTodoProgressResponse empty() {
        return StudyTodoProgressResponse.builder()
                .todo(null)
                .totalMemberCount(0)
                .completeMemberCount(0)
                .build();
    }
}
