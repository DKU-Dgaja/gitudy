package com.example.backend.study.api.service.todo;


import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.todo.TodoException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyTodoService {


    private final StudyTodoRepository studyTodoRepository;
    private final StudyTodoMappingRepository studyTodoMappingRepository;
    private final StudyInfoRepository studyInfoRepository;

    // Todo 등록
    @Transactional
    public void registerStudyTodo(StudyTodo studyTodo, Long studyInfoId, Long userId) {

        // 스터디 정보 조회
        StudyInfo studyInfo = studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_INFO_NOT_FOUND.getText());
            return new TodoException(ExceptionMessage.STUDY_INFO_NOT_FOUND);
        });

        // 스터디장인지 확인
        if (!studyInfo.getUserId().equals(userId)) {
            throw new TodoException(ExceptionMessage.STUDY_NOT_LEADER);
        }


        studyTodoRepository.save(studyTodo);
        StudyTodoMapping studyTodoMapping = StudyTodoMapping.builder()
                .userId(userId)
                .todoId(studyTodo.getId())
                .status(StudyTodoStatus.TODO_INCOMPLETE)  // default
                .build();
        studyTodoMappingRepository.save(studyTodoMapping);

    }


    // Todo 조회
    public List<StudyTodoResponse> readStudyTodo(Long studyInfoId) {

        // 스터디 정보 조회
        StudyInfo studyInfo = studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_INFO_NOT_FOUND.getText());
            return new TodoException(ExceptionMessage.STUDY_INFO_NOT_FOUND);
        });

        List<StudyTodo> studyTodoList = studyTodoRepository.findByStudyInfoId(studyInfoId);

        return studyTodoList.stream()
                .map(StudyTodoResponse::of)
                .toList();
    }

    // Todo 수정
    @Transactional
    public void updateStudyTodo(Long todoId, StudyTodoUpdateRequest request, Long userId) {

        // 스터디 정보 조회
        StudyInfo studyInfo = studyInfoRepository.findByUserId(userId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", userId, ExceptionMessage.STUDY_NOT_MEMBER.getText());
            return new TodoException(ExceptionMessage.STUDY_NOT_MEMBER);
        });

        // Todo 조회 (todoId = studyTodo.getId())
        StudyTodo studyTodo = studyTodoRepository.findById(todoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", todoId, ExceptionMessage.TODO_NOT_FOUND.getText());
            return new TodoException(ExceptionMessage.TODO_NOT_FOUND);
        });

        // 수정하려는 Todo 아이디로 TodoMapping을 조회
        StudyTodoMapping studyTodoMapping = studyTodoMappingRepository.findByTodoIdAndUserId(todoId, userId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", todoId, ExceptionMessage.TODO_NOT_FOUND.getText());
            return new TodoException(ExceptionMessage.TODO_NOT_FOUND);
        });


        // Todo 할당자 정보
        Long todoUserId = studyTodoMapping.getUserId();
        Long studyLeader = studyInfo.getUserId();

        // 현재 유저가 해당 Todo 할당자, 팀장인지 확인
        if (!todoUserId.equals(userId) || !studyLeader.equals(userId)) {
            throw new TodoException(ExceptionMessage.STUDY_NOT_LEADER);
        }

        // Todo 정보 업데이트
        studyTodo.updateStudyTodo(
                request.getTitle(),
                request.getDetail(),
                request.getTodoLink(),
                request.getEndTime());
        studyTodoMapping.updateStudyTodoMapping(
                request.getStatus());


    }


    // Todo 삭제
    @Transactional
    public void deleteStudyTodo(Long studyInfoId, Long todoId, Long userId) {


        // 스터디 정보 조회
        StudyInfo studyInfo = studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_INFO_NOT_FOUND);
            return new TodoException(ExceptionMessage.STUDY_INFO_NOT_FOUND);
        });


        // StudyTodo 조회
        StudyTodo studyTodo = studyTodoRepository.findById(todoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", todoId, ExceptionMessage.TODO_NOT_FOUND);
            return new TodoException(ExceptionMessage.TODO_NOT_FOUND);
        });




        // 스터디장인지 확인
        if (!studyInfo.getUserId().equals(userId)) {
            throw new TodoException(ExceptionMessage.STUDY_NOT_LEADER);
        }


        // StudyTodoMapping 테이블에서 todoId로 연결된 레코드 삭제
        studyTodoMappingRepository.deleteByTodoId(todoId);


        // StudyTodo 테이블에서 해당 todoId에 해당하는 레코드 삭제
        studyTodoRepository.delete(studyTodo);


    }


}