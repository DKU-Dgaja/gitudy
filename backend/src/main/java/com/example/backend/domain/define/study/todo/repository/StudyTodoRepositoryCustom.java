package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;

import java.util.List;


public interface StudyTodoRepositoryCustom {
    // StudyInfoId로 To do 전체 가져오기
    List<StudyTodoResponse> findStudyTodoListByStudyInfoId_CursorPaging(Long studyInfoId, Long idx, Long limit);

    // StudyInfoId와 userId로 해당 유저에게 할당된 마감일이 지나지 않은 To do 들을 조회해 제거한다.
    void deleteTodoIdsByStudyInfoIdAndUserId(Long studyInfoId, Long userId);

}
