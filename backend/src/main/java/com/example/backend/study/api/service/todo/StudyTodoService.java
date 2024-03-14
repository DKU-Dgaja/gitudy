package com.example.backend.study.api.service.todo;


import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.todo.TodoException;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.StudyTodoListAndCursorIdxResponse;
import com.example.backend.study.api.controller.todo.response.StudyTodoResponse;
import com.example.backend.study.api.controller.todo.response.StudyTodoStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyTodoService {


    private final StudyTodoRepository studyTodoRepository;
    private final StudyTodoMappingRepository studyTodoMappingRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final StudyInfoRepository studyInfoRepository;
    private final static Long MAX_LIMIT = 10L;

    // Todo 등록
    @Transactional
    public void registerStudyTodo(StudyTodoRequest studyTodoRequest, Long studyInfoId) {


        // 스터디에 속한 활동중인 스터디원 조회
        List<StudyMember> studyActiveMembers = studyMemberRepository.findActiveMembersByStudyInfoId(studyInfoId);

        StudyTodo studyTodo = createStudyTodo(studyTodoRequest, studyInfoId);
        studyTodoRepository.save(studyTodo);

        // 활동중인 스터디원에게만 TO DO 할당
        List<StudyTodoMapping> todoMappings = studyActiveMembers.stream()
                .map(activeMember -> StudyTodoMapping.builder()
                        .userId(activeMember.getUserId())
                        .todoId(studyTodo.getId())
                        .status(StudyTodoStatus.TODO_INCOMPLETE) // 기본 상태
                        .build())
                .collect(Collectors.toList());

        // 한 번의 쿼리로 모든 매핑 저장
        studyTodoMappingRepository.saveAll(todoMappings);


    }

    // StudyTodo 생성 로직
    private StudyTodo createStudyTodo(StudyTodoRequest studyTodoRequest, Long studyInfoId) {
        return StudyTodo.builder()
                .studyInfoId(studyInfoId)
                .title(studyTodoRequest.getTitle())
                .detail(studyTodoRequest.getDetail())
                .todoLink(studyTodoRequest.getTodoLink())
                .todoDate(studyTodoRequest.getTodoDate())
                .build();
    }


    // Todo 수정
    @Transactional
    public void updateStudyTodo(StudyTodoUpdateRequest request, Long todoId) {

        // To do 조회
        StudyTodo studyTodo = studyTodoRepository.findById(todoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", todoId, ExceptionMessage.TODO_NOT_FOUND.getText());
            return new TodoException(ExceptionMessage.TODO_NOT_FOUND);
        });

        // 기존 To do 업데이트
        studyTodo.updateStudyTodo(
                request.getTitle(),
                request.getDetail(),
                request.getTodoLink(),
                request.getTodoDate());

    }

    // Todo 삭제
    @Transactional
    public void deleteStudyTodo(Long todoId, Long studyInfoId) {

        // 스터디와 관련된 StudyTodo 조회
        StudyTodo studyTodo = studyTodoRepository.findByIdAndStudyInfoId(todoId, studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", todoId, ExceptionMessage.TODO_NOT_FOUND);
            return new TodoException(ExceptionMessage.TODO_NOT_FOUND);
        });

        // StudyTodoMapping 테이블에서 todoId로 연결된 레코드 삭제
        studyTodoMappingRepository.deleteByTodoId(studyTodo.getId());


        // StudyTodo 테이블에서 해당 todoId에 해당하는 레코드 삭제
        studyTodoRepository.delete(studyTodo);

    }

    // Todo 전체조회
    public StudyTodoListAndCursorIdxResponse readStudyTodoList(Long studyInfoId, Long cursorIdx, Long limit) {

        // 스터디 조회 예외처리
        studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_INFO_NOT_FOUND);
            return new TodoException(ExceptionMessage.STUDY_INFO_NOT_FOUND);
        });

        limit = Math.min(limit, MAX_LIMIT);

        List<StudyTodoResponse> studyTodoList = studyTodoRepository.findStudyTodoListByStudyInfoId_CursorPaging(studyInfoId, cursorIdx, limit);

        StudyTodoListAndCursorIdxResponse response = StudyTodoListAndCursorIdxResponse.builder()
                .todoList(studyTodoList)
                .build();

        // 다음 페이지 조회를 위한 cursorIdx 설정
        response.setNextCursorIdx();

        return response;
    }

    // 스터디원들의 Todo 완료여부 조회
    public List<StudyTodoStatusResponse> readStudyTodoStatus(Long studyInfoId, Long todoId) {

        // 스터디와 관련된 To do 예외처리
        studyTodoRepository.findByIdAndStudyInfoId(todoId, studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", todoId, ExceptionMessage.TODO_NOT_FOUND);
            return new TodoException(ExceptionMessage.TODO_NOT_FOUND);
        });

        // 스터디 active 멤버들 찾기
        List<StudyMember> activeMembers = studyMemberRepository.findActiveMembersByStudyInfoId(studyInfoId);

        // active 멤버들의 userId 추출
        List<Long> userIds = extractUserIds(activeMembers);

        // active 멤버들에 대한 특정 Todo의 완료 상태를 조회
        List<StudyTodoMapping> todoMappings = studyTodoMappingRepository.findByTodoIdAndUserIds(todoId, userIds);

        // 조회된 정보를 바탕으로 응답 객체를 생성
        return todoMappings.stream()
                .map(mapping -> new StudyTodoStatusResponse(mapping.getUserId(), mapping.getStatus()))
                .collect(Collectors.toList());
    }

    // 멤버들의 userId만 추출
    private List<Long> extractUserIds(List<StudyMember> activeMembers) {
        return activeMembers.stream()
                .map(StudyMember::getUserId)
                .toList();
    }


}