package com.example.backend.study.api.service.todo;


import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.study.StudyInfoException;
import com.example.backend.common.exception.todo.TodoException;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.event.TodoRegisterMemberEvent;
import com.example.backend.domain.define.study.todo.event.TodoUpdateMemberEvent;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.controller.todo.request.StudyTodoRequest;
import com.example.backend.study.api.controller.todo.request.StudyTodoUpdateRequest;
import com.example.backend.study.api.controller.todo.response.*;
import com.example.backend.study.api.service.commit.response.CommitInfoResponse;
import com.example.backend.study.api.service.info.StudyInfoService;
import com.example.backend.study.api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    private final StudyInfoService studyInfoService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final StudyCommitRepository studyCommitRepository;

    private final static Long MAX_LIMIT = 10L;
    private final static int PAGE_SIZE = 10;

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

        // 활동중인 멤버들의 userId 추출
        List<Long> activeMemberUserIds = extractUserIds(studyActiveMembers);

        // FCM 알림을 받을 수 있는 사용자의 ID 추출
        List<Long> isPushAlarmYUserIds = userService.findIsPushAlarmYsByIdsOrThrowException(activeMemberUserIds);


        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        // 알림 비동기처리
        eventPublisher.publishEvent(TodoRegisterMemberEvent.builder()
                .activesMemberIds(activeMemberUserIds)
                .pushAlarmYMemberIds(isPushAlarmYUserIds)
                .studyTopic(studyInfo.getTopic())
                .build());
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
    public void updateStudyTodo(StudyTodoUpdateRequest request, Long todoId, Long studyInfoId) {
        // 스터디에 속한 활동중인 스터디원 조회
        List<StudyMember> studyActiveMembers = studyMemberRepository.findActiveMembersByStudyInfoId(studyInfoId);

        // To do 조회
        StudyTodo studyTodo = findByIdOrThrowStudyTodoException(todoId);

        // 기존 To do 업데이트
        studyTodo.updateStudyTodo(
                request.getTitle(),
                request.getDetail(),
                request.getTodoLink(),
                request.getTodoDate());

        // 깃허브 api를 사용해 커밋 업데이트
        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        // 활동중인 멤버들의 userId 추출
        List<Long> activeMemberUserIds = extractUserIds(studyActiveMembers);

        // FCM 알림을 받을 수 있는 사용자의 ID 추출
        List<Long> isPushAlarmYUserIds = userService.findIsPushAlarmYsByIdsOrThrowException(activeMemberUserIds);

        // 알림 비동기처리
        eventPublisher.publishEvent(TodoUpdateMemberEvent.builder()
                .activesMemberIds(activeMemberUserIds)
                .pushAlarmYMemberIds(isPushAlarmYUserIds)
                .studyTopic(studyInfo.getTopic())
                .todoTitle(studyTodo.getTitle())
                .build());

    }

    // Todo 삭제
    @Transactional
    public void deleteStudyTodo(Long todoId, Long studyInfoId) {

        // 스터디와 관련된 StudyTodo 조회
        StudyTodo studyTodo = findByIdWithStudyInfoIdOrThrowStudyTodoException(studyInfoId, todoId);

        // StudyTodoMapping 테이블에서 todoId로 연결된 레코드 삭제
        studyTodoMappingRepository.deleteByTodoId(studyTodo.getId());

        // StudyTodo 테이블에서 해당 todoId에 해당하는 레코드 삭제
        studyTodoRepository.delete(studyTodo);

    }

    // Todo 전체조회
    @Transactional
    public StudyTodoListAndCursorIdxResponse readStudyTodoList(Long studyInfoId, Long cursorIdx, Long limit) {

        // 스터디 조회 예외처리
        StudyInfo study = studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        limit = Math.min(limit, MAX_LIMIT);

        List<StudyTodoWithCommitsResponse> studyTodoList = studyTodoRepository.findStudyTodoListByStudyInfoId_CursorPaging(studyInfoId, cursorIdx, limit);

        StudyTodoListAndCursorIdxResponse response = StudyTodoListAndCursorIdxResponse.builder()
                .todoList(studyTodoList)
                .build();

        // 다음 페이지 조회를 위한 cursorIdx 설정
        response.setNextCursorIdx();

        return response;
    }

    // Todo 단일조회
    @Transactional
    public StudyTodoResponse readStudyTodo(Long studyInfoId, Long todoId) {

        // To do 조회
        StudyTodo todo = findByIdOrThrowStudyTodoException(todoId);

        StudyInfo studyInfo = studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", studyInfoId, ExceptionMessage.STUDY_INFO_NOT_FOUND.getText());
            return new StudyInfoException(ExceptionMessage.STUDY_INFO_NOT_FOUND);
        });

        return StudyTodoResponse.of(todo);
    }

    // 스터디원들의 Todo 완료여부 조회
    public List<StudyTodoStatusResponse> readStudyTodoStatus(Long studyInfoId, Long todoId) {

        // 스터디와 관련된 To do 예외처리
        StudyTodo todo = findByIdWithStudyInfoIdOrThrowStudyTodoException(studyInfoId, todoId);

        // 스터디 active 멤버들 찾기
        List<StudyMember> activeMembers = studyMemberRepository.findActiveMembersByStudyInfoId(studyInfoId);

        // active 멤버들의 userId 추출
        List<Long> userIds = extractUserIds(activeMembers);

        // active 멤버들에 대한 특정 Todo의 완료 상태를 조회
        List<StudyTodoMapping> todoMappings = studyTodoMappingRepository.findByTodoIdAndUserIds(todoId, userIds);

        // TODO: 점수 부여 로직 필요

        // 조회된 정보를 바탕으로 응답 객체를 생성
        return todoMappings.stream()
                .map(mapping -> new StudyTodoStatusResponse(mapping.getUserId(), mapping.getStatus()))
                .collect(Collectors.toList());
    }

    public StudyTodo findByIdOrThrowStudyTodoException(Long todoId) {
        return studyTodoRepository.findById(todoId)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", todoId, ExceptionMessage.TODO_NOT_FOUND);
                    return new TodoException(ExceptionMessage.TODO_NOT_FOUND);
                });
    }

    public StudyTodo findByIdWithStudyInfoIdOrThrowStudyTodoException(Long studyInfoId, Long todoId) {
        return studyTodoRepository.findByIdAndStudyInfoId(todoId, studyInfoId)
                .orElseThrow(() -> {
                    log.warn(">>>> {} : {} <<<<", todoId, ExceptionMessage.TODO_NOT_FOUND);
                    return new TodoException(ExceptionMessage.TODO_NOT_FOUND);
                });
    }

    // 멤버들의 userId만 추출
    private List<Long> extractUserIds(List<StudyMember> activeMembers) {
        return activeMembers.stream()
                .map(StudyMember::getUserId)
                .toList();
    }

    @Transactional
    public StudyTodoProgressResponse readStudyTodoProgress(Long studyInfoId) {
        // 해당 스터디에서 활동중인 스터디원 인원수
        int memberCount = studyMemberRepository.findActiveMembersByStudyInfoId(studyInfoId).size();

        // 오늘이거나 오늘 이후의 To-do 중 가장 마감일이 빠른 To-do
        Optional<StudyTodo> todo = studyTodoRepository.findStudyTodoByStudyInfoIdWithEarliestDueDate(studyInfoId);

        // To-do가 없을 경우 null 반환
        if (todo.isEmpty()) {
            return null;
        }

        StudyTodo findTodo = todo.get();

        // 투두 완료 멤버 인원 수
        int completeMemberCount = studyTodoMappingRepository.findCompleteTodoMappingCountByTodoId(findTodo.getId());

        return StudyTodoProgressResponse.builder()
                .todoId(findTodo.getId())
                .totalMemberCount(memberCount)
                .completeMemberCount(completeMemberCount)
                .build();
    }

    public List<CommitInfoResponse> selectTodoCommits(Long todoId) {

        return studyCommitRepository.findByStudyTodoIdOrderByCommitDateDesc(todoId).stream()
                .map(CommitInfoResponse::of)
                .toList();
    }
}