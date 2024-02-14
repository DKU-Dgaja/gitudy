package com.example.backend.study.api.service.todo;


import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.todo.TodoException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
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

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyTodoService {


    private final StudyTodoRepository studyTodoRepository;
    private final StudyTodoMappingRepository studyTodoMappingRepository;
    private final StudyInfoRepository studyInfoRepository;
    private final UserRepository userRepository;
    private final StudyMemberRepository studyMemberRepository;

    // Todo 등록
    @Transactional
    public void registerStudyTodo(StudyTodoRequest studyTodoRequest, Long studyInfoId, User userPrincipal) {


        // platformId와 platformType을 이용하여 User 객체 조회
        User user = userRepository.findByPlatformIdAndPlatformType(userPrincipal.getPlatformId(), userPrincipal.getPlatformType()).orElseThrow(() -> {
            log.warn(">>>> {},{} : {} <<<<", userPrincipal.getPlatformId(), userPrincipal.getPlatformType(), ExceptionMessage.USER_NOT_FOUND);
            return new TodoException(ExceptionMessage.USER_NOT_FOUND);
        });

        // 스터디 정보 조회
        StudyInfo studyInfo = studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_INFO_NOT_FOUND.getText());
            return new TodoException(ExceptionMessage.STUDY_INFO_NOT_FOUND);
        });

        // 스터디장인지 확인
        if (!studyInfo.getUserId().equals(user.getId())) {
            throw new TodoException(ExceptionMessage.STUDY_MEMBER_NOT_LEADER);
        }

        // 스터디에 속한 스터디원 조회
        List<StudyMember> studyMembers = studyMemberRepository.findByStudyInfoId(studyInfoId);

        StudyTodo studyTodo = studyTodoRequest.StudyTodoRegister();
        studyTodoRepository.save(studyTodo);

        studyMembers.forEach(studyMember -> {
            StudyTodoMapping studyTodoMapping = StudyTodoMapping.builder()
                    .userId(studyMember.getUserId())
                    .todoId(studyTodo.getId())
                    .status(StudyTodoStatus.TODO_INCOMPLETE) // default
                    .build();
            studyTodoMappingRepository.save(studyTodoMapping);
        });


    }
}