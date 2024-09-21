package com.example.backend.webhook.api.service;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.member.MemberException;
import com.example.backend.common.exception.study.StudyInfoException;
import com.example.backend.common.exception.todo.TodoException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
import com.example.backend.domain.define.study.commit.event.CommitRegisterEvent;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.study.api.service.user.UserService;
import com.example.backend.webhook.api.controller.request.CommitPayload;
import com.example.backend.webhook.api.controller.request.WebhookPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WebhookService {
    private final StudyInfoRepository studyInfoRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final StudyTodoMappingRepository studyTodoMappingRepository;
    private final StudyCommitRepository studyCommitRepository;
    private final StudyTodoRepository studyTodoRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;

    @Transactional
    public void handleCommit(WebhookPayload payload) {

        // 스터디 특정
        StudyInfo study = getStudyByPayLoad(payload.repositoryFullName());

        // 투두 폴더 생성 시 발생하는 커밋이라면 제외
        if (isTodoFolderCreateCommit(payload)) return;

        for (CommitPayload commit : payload.commits()) {
            // 추가/수정 커밋리스트에서 폴더 이름 추출
            Set<String> folderNames = extractFolderNames(commit.commitAdded(), commit.commitModified());

            for (String folderName : folderNames) {
                // 투두 특정
                StudyTodo todo = getTodoByPayLoad(folderName);

                // 권한 검증 및 사용자 특정
                User user = validateAndGetUser(commit.username(), study);

                // 커밋, 투두 정보 업데이트
                updateCommitAndTodoMappingStatus(user.getId(), study.getId(), todo, commit);

                // 마지막 커밋 활동 시간 업데이트
                study.updateLastCommitDateToCurrent();

                // 커밋 스터디장에게 알림처리
                notifyStudyLeader(study, user, todo);
            }
        }
    }

    private void notifyStudyLeader(StudyInfo study, User user, StudyTodo todo) {
        User studyLeader = userService.findUserByIdOrThrowException(study.getUserId());
        eventPublisher.publishEvent(CommitRegisterEvent.builder()
                .isPushAlarmYn(studyLeader.isPushAlarmYn())
                .userId(studyLeader.getId())
                .name(user.getName())
                .studyInfoId(study.getId())
                .studyTopic(study.getTopic())
                .studyTodoTopic(todo.getTitle())
                .build());
    }

    private User validateAndGetUser(String username, StudyInfo study) {
        User user = getUserByPayLoad(username);

        if (!studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(user.getId(), study.getId())) {
            log.warn(">>>> Github ID: {} {} <<<<", username, ExceptionMessage.STUDY_NOT_ACTIVE_MEMBER.getText());
            throw new MemberException(ExceptionMessage.STUDY_NOT_ACTIVE_MEMBER);
        }

        return user;
    }

    private boolean isTodoFolderCreateCommit(WebhookPayload payload) {
        // 전달받은 커밋이 1개인지 확인
        if (payload.commits().size() != 1) return false;

        CommitPayload commit = payload.commits().get(0);

        // 추가된 파일이 1개이면서 수정된 파일은 없는지 확인
        if (commit.commitAdded().size() != 1 || !commit.commitModified().isEmpty()) return false;

        // 추가된 파일이 .md 확장자인지(마크다운 파일인지) 확인
        String addedFilePath = commit.commitAdded().get(0);
        return isMarkdownFile(addedFilePath);
    }

    private boolean isMarkdownFile(String addedFilePath) {
        return addedFilePath.endsWith(".md");
    }

    private Set<String> extractFolderNames(List<String>... fileLists) {
        return Arrays.stream(fileLists)                         // Stream<List<String>>
                .filter(Objects::nonNull)                       // null이 아닌 리스트만 필터링
                .flatMap(List::stream)                          // Stream<String>
                .filter(filePath -> filePath.contains("/"))     // "/"가 있는 경로만 필터링
                .map(filePath -> filePath.substring(0, filePath.indexOf("/")))  // 폴더 이름 추출
                .collect(Collectors.toSet());                   // Set<String>
    }

    private User getUserByPayLoad(String githubId) {
        return userRepository.findByGithubId(githubId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", githubId, ExceptionMessage.USER_NOT_FOUND.getText());
            throw new UserException(ExceptionMessage.USER_NOT_FOUND);
        });
    }

    private StudyTodo getTodoByPayLoad(String folderName) {
        return studyTodoRepository.findByTodoFolderName(folderName).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", folderName, ExceptionMessage.TODO_NOT_FOUND.getText());
            throw new TodoException(ExceptionMessage.TODO_NOT_FOUND);
        });
    }

    private StudyInfo getStudyByPayLoad(String repositoryFullName) {
        // 레포지토리 owner, name 추출
        String[] repoInfo = repositoryFullName.split("/");
        String repoOwner = repoInfo[0];
        String repoName = repoInfo[1];

        // 스터디 특정
        return studyInfoRepository.findByRepositoryFullName(repoOwner, repoName).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", repositoryFullName, ExceptionMessage.STUDY_INFO_NOT_FOUND.getText());
            throw new StudyInfoException(ExceptionMessage.STUDY_INFO_NOT_FOUND);
        });
    }

    private void updateCommitAndTodoMappingStatus(Long userId, Long studyId, StudyTodo todo, CommitPayload commit) {

        // 해당하는 투두 매핑 정보가 없는 경우 예외가 발생한다.
        StudyTodoMapping todoMapping = studyTodoMappingRepository.findByTodoIdAndUserId(todo.getId(), userId).orElseThrow(() -> {
            log.warn(">>>> {} <<<<", ExceptionMessage.STUDY_TODO_MAPPING_NOT_FOUND.getText());
            throw new TodoException(ExceptionMessage.STUDY_TODO_MAPPING_NOT_FOUND);
        });

        // 아직 처리되지 않은 투두의 첫 커밋이거나 지각 처리됐던 투두에 재커밋한 경우
        if (todoMapping.getStatus() == TODO_INCOMPLETE ||
                todoMapping.getStatus() == TODO_OVERDUE) {

            // 지각 여부 체크
            StudyTodoStatus updateStatus = commit.commitDate().isAfter(todo.getTodoDate()) ? TODO_OVERDUE : TODO_COMPLETE;

            todoMapping.updateTodoMappingStatus(updateStatus);
        }

        // 커밋 저장
        studyCommitRepository.save(StudyCommit.builder()
                .userId(userId)
                .studyInfoId(studyId)
                .studyTodoId(todo.getId())
                .commitSHA(commit.commitId())
                .message(commit.message())
                .commitDate(commit.commitDate())
                .status(CommitStatus.COMMIT_WAITING)
                .build());

        log.info(">>>> {}님의 '{}' 투두에 대한 커밋 등록을 완료하였습니다 <<<<", commit.username(), todo.getTitle());
    }
}
