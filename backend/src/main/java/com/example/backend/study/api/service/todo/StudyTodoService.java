package com.example.backend.study.api.service.todo;


import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.todo.TodoException;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyTodoService {


    private final StudyTodoRepository studyTodoRepository;
    private final StudyTodoMappingRepository studyTodoMappingRepository;

    // Todo 등록
    @Transactional
    public void registerStudyTodo(StudyTodo studyTodo, StudyTodoMapping studyTodoMapping) {
        studyTodoRepository.save(studyTodo);
        studyTodoMappingRepository.save(studyTodoMapping);
    }


    // Todo 조회
    @Transactional
    public List<StudyTodo> readStudyTodo(Long studyInfoId) {
        return studyTodoRepository.findByStudyInfoId(studyInfoId);
    }

    // Todo 수정
    @Transactional
    public void updateStudyTodo(Long todoId, StudyTodoUpdateRequest request, Long userId) {

        // Todo 조회 (todoId = studyTodo.getId())
        StudyTodo studyTodo = studyTodoRepository.findById(todoId).orElseThrow(()->{
            log.warn(">>>> {} : {} <<<<", todoId, ExceptionMessage.TODO_NOT_FOUND.getText());
            return new TodoException(ExceptionMessage.TODO_NOT_FOUND);
        });

        // 수정하려는 Todo 아이디로 TodoMapping을 조회
        StudyTodoMapping studyTodoMapping = studyTodoMappingRepository.findById(todoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", todoId, ExceptionMessage.STUDY_NOT_FOUND.getText());
            return new TodoException(ExceptionMessage.STUDY_NOT_FOUND);
        });


        // Todo 할당자 정보
        Long todoUserId = studyTodoMapping.getUserId();

        // 현재 로그인한 유저가 해당 Todo 할당자인지 확인
        if (!userId.equals(todoUserId)) {
            throw new TodoException(ExceptionMessage.TODO_NOT_ALLOCATOR);
        }

        // Todo 정보 업데이트
        studyTodo.updateStudyTodo(
                request.getTitle(),
                request.getDetail(),
                request.getTodoLink(),
                request.getEndTime());
        studyTodoMapping.updateStudyTodoMapping(
                request.getStatus());

        studyTodoRepository.save(studyTodo);
        studyTodoMappingRepository.save(studyTodoMapping);

    }


    // Todo 삭제
    @Transactional
    public void deleteStudyTodo(Long todoId) {

        // StudyTodoMapping 테이블에서 todoId로 연결된 레코드 삭제
        List<StudyTodoMapping> todoMapping = studyTodoMappingRepository.findByTodoId(todoId);
        studyTodoMappingRepository.deleteAll(todoMapping);

        // StudyTodo 테이블에서 todoId로 찾은 레코드 삭제
        studyTodoRepository.findById(todoId).ifPresent(studyTodoRepository::delete);

    }


}