package com.example.backend.study.api.service.todo;


import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.study.StudyInfoException;
import com.example.backend.common.exception.todo.TodoException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.github.GithubApiToken;
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
import com.example.backend.study.api.service.github.GithubApiService;
import com.example.backend.study.api.service.github.GithubApiTokenService;
import com.example.backend.study.api.service.info.StudyInfoService;
import com.example.backend.study.api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final StudyInfoService studyInfoService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final StudyCommitRepository studyCommitRepository;
    private final GithubApiService githubApiService;
    private final GithubApiTokenService githubApiTokenService;

    private final static Long MAX_LIMIT = 10L;

    // Todo 등록
    @Transactional
    public void registerStudyTodo(StudyTodoRequest studyTodoRequest, Long studyInfoId) {

        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

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

        // 투두에 해당하는 폴더를 스터디 레포지토리에 생성
        log.info("투두 폴더를 레포지토리에 생성하기 전 토큰 조회 중.. (userId: {})", studyInfo.getUserId());
        GithubApiToken token = githubApiTokenService.getToken(studyInfo.getUserId());
        log.info("투두 폴더를 레포지토리에 생성하기 전 토큰 조회 완료 (userId: {})", studyInfo.getUserId());

        githubApiService.createTodoFolder(token.githubApiToken(), studyTodo, studyInfo.getRepositoryInfo());

        // 알림 비동기처리
        eventPublisher.publishEvent(TodoRegisterMemberEvent.builder()
                .activesMemberIds(activeMemberUserIds)
                .pushAlarmYMemberIds(isPushAlarmYUserIds)
                .studyInfoId(studyInfo.getId())
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

        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

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


        // 활동중인 멤버들의 userId 추출
        List<Long> activeMemberUserIds = extractUserIds(studyActiveMembers);

        // FCM 알림을 받을 수 있는 사용자의 ID 추출
        List<Long> isPushAlarmYUserIds = userService.findIsPushAlarmYsByIdsOrThrowException(activeMemberUserIds);

        // 알림 비동기처리
        eventPublisher.publishEvent(TodoUpdateMemberEvent.builder()
                .activesMemberIds(activeMemberUserIds)
                .pushAlarmYMemberIds(isPushAlarmYUserIds)
                .studyInfoId(studyInfo.getId())
                .studyTopic(studyInfo.getTopic())
                .todoTitle(studyTodo.getTitle())
                .build());

    }

    // Todo 삭제
    @Transactional
    public void deleteStudyTodo(Long todoId, Long studyInfoId) {

        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        // 스터디와 관련된 StudyTodo 조회
        StudyTodo studyTodo = findByIdWithStudyInfoIdOrThrowStudyTodoException(studyInfo.getId(), todoId);

        // StudyTodoMapping 테이블에서 todoId로 연결된 레코드 삭제
        studyTodoMappingRepository.deleteByTodoId(studyTodo.getId());

        // StudyTodo 테이블에서 해당 todoId에 해당하는 레코드 삭제
        studyTodoRepository.delete(studyTodo);

    }

    // Todo 전체조회
    @Transactional
    public StudyTodoListAndCursorIdxResponse readStudyTodoList(Long studyInfoId, Long cursorIdx, Long limit) {

        // 스터디 조회 예외처리
        StudyInfo studyInfo = studyInfoService.findStudyInfoByIdOrThrowException(studyInfoId);

        limit = Math.min(limit, MAX_LIMIT);

        List<StudyTodoWithCommitsResponse> studyTodoList = studyTodoRepository.findStudyTodoListByStudyInfoId_CursorPaging(studyInfo.getId(), cursorIdx, limit);

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

        studyInfoRepository.findById(studyInfoId).orElseThrow(() -> {
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

    public StudyTodoProgressResponse readStudyTodoProgress(Long userId, Long studyInfoId) {

        // 오늘이거나 오늘 이후의 To-do 중 가장 마감일이 빠른 To-do
        return studyTodoRepository.findStudyTodoByStudyInfoIdWithEarliestDueDate(studyInfoId)
                .map(todo -> {
                    // 해당 스터디에서 활동중인 스터디원 인원수
                    int memberCount = studyMemberRepository.findActiveMembersByStudyInfoId(studyInfoId).size();

                    // 투두 완료 멤버 인원 수
                    int completeMemberCount = studyTodoMappingRepository.findCompleteTodoMappingCountByTodoId(todo.getId());

                    // 자신의 투두 완료 여부 확인
                    return studyTodoMappingRepository.findByTodoIdAndUserId(todo.getId(), userId)
                            .map(todoMapping -> StudyTodoProgressResponse.builder()
                                    .todo(StudyTodoResponse.of(todo))
                                    .totalMemberCount(memberCount)
                                    .completeMemberCount(completeMemberCount)
                                    .myStatus(todoMapping.getStatus())
                                    .build())
                            .orElseGet(StudyTodoProgressResponse::empty);
                })
                .orElseGet(StudyTodoProgressResponse::empty);
    }

    public List<CommitInfoResponse> selectTodoCommits(Long todoId) {
        // 커밋 목록 조회
        List<StudyCommit> commits = studyCommitRepository.findByStudyTodoIdOrderByCommitDateDesc(todoId);

        // 커밋의 사용자 이름을 포함한 응답 리스트 생성
        return commits.stream()
                .map(commit -> {
                    // 커밋의 사용자 정보 조회
                    User user = userService.findUserByIdOrThrowException(commit.getUserId());
                    String userName = user.getName();
                    String profileImageUrl = user.getProfileImageUrl();
                    // 사용자 이름을 포함한 CommitInfoResponse 객체 생성
                    return CommitInfoResponse.of(commit, userName, profileImageUrl);
                })
                .collect(Collectors.toList());
    }
}