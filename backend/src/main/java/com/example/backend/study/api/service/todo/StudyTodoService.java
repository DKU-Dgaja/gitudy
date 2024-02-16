package com.example.backend.study.api.service.todo;


import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
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
}