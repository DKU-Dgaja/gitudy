package com.example.backend.domain.define.study.todo.repository;

import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;

import java.util.List;


public interface StudyTodoRepositoryCustom {
    // StudyInfoId로 To do 전체 가져오기
    List<StudyTodoResponse> findStudyTodoListByStudyInfoId_CursorPaging(Long studyInfoId, Long idx, Long limit);

}
