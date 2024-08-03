package com.example.backend.webhook.api.service;

import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.convention.ConventionException;
import com.example.backend.common.exception.member.MemberException;
import com.example.backend.common.exception.study.StudyInfoException;
import com.example.backend.common.exception.todo.TodoException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.constant.CommitStatus;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.webhook.api.controller.request.WebhookPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WebhookService {
    private final static Pattern DEFAULT_CONVENTION_PATTERN = Pattern.compile("^[a-zA-Z0-9]{6} .*");
    private final static int TODO_CODE_START_INDEX = 0;
    private final static int TODO_CODE_END_INDEX = 6;

    private final StudyInfoRepository studyInfoRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final StudyTodoMappingRepository studyTodoMappingRepository;
    private final StudyCommitRepository studyCommitRepository;
    private final StudyTodoRepository studyTodoRepository;
    private final UserRepository userRepository;

    @Transactional
    public void handleCommit(WebhookPayload payload) {

        // 커밋 필터링
        if (!checkConvention(payload.message())) {
            log.warn(">>>> {} : {} <<<<", payload.message(), ExceptionMessage.CONVENTION_NOT_MATCHED.getText());
            throw new ConventionException(ExceptionMessage.CONVENTION_NOT_MATCHED);
        }

        // 스터디 특정
        StudyInfo study = getStudyByPayLoad(payload.repositoryFullName());

        // 투두 특정
        StudyTodo todo = getTodoByPayLoad(payload.message());

        // 사용자 특정
        User user = getUserByPayLoad(payload.username());

        // 권한 검증
        if (!studyMemberRepository.existsStudyMemberByUserIdAndStudyInfoId(user.getId(), study.getId())) {
            log.warn(">>>> Github ID: {} {} <<<<", payload.username(), ExceptionMessage.STUDY_NOT_ACTIVE_MEMBER.getText());
            throw new MemberException(ExceptionMessage.STUDY_NOT_ACTIVE_MEMBER);
        }

        //  커밋, 투두 정보 업데이트
        updateCommitAndTodoMappingStatus(user.getId(), study.getId(), todo, payload);
    }

    private User getUserByPayLoad(String githubId) {
        return userRepository.findByGithubId(githubId).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", githubId, ExceptionMessage.USER_NOT_FOUND.getText());
            throw new UserException(ExceptionMessage.USER_NOT_FOUND);
        });
    }

    private StudyTodo getTodoByPayLoad(String message) {
        // 투두 코드 추출
        String todoCode = message.substring(TODO_CODE_START_INDEX, TODO_CODE_END_INDEX);

        // 투두 특정
        return studyTodoRepository.findByTodoCode(todoCode).orElseThrow(() -> {
            log.warn(">>>> {} : {} <<<<", todoCode, ExceptionMessage.TODO_NOT_FOUND.getText());
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

    private boolean checkConvention(String commitMsg) {
        // 커밋 메세지가 정규식과 일치하는지 반환
        return DEFAULT_CONVENTION_PATTERN.matcher(commitMsg).matches();
    }

    private void updateCommitAndTodoMappingStatus(Long userId, Long studyId, StudyTodo todo, WebhookPayload payload) {

        // 지각 여부 체크
        StudyTodoStatus updateStatus = payload.commitDate().isAfter(todo.getTodoDate()) ?
                StudyTodoStatus.TODO_OVERDUE : StudyTodoStatus.TODO_COMPLETE;

        // 투두 mapping 상태 업데이트
        if (!studyTodoMappingRepository.updateByUserIdAndTodoId(userId, todo.getId(), updateStatus)) {
            log.warn(">>>> Todo Code: {} {} <<<<", todo.getTodoCode(), ExceptionMessage.STUDY_TODO_MAPPING_NOT_FOUND);
            throw new TodoException(ExceptionMessage.STUDY_TODO_MAPPING_NOT_FOUND);
        }

        // 커밋 저장
        studyCommitRepository.save(StudyCommit.builder()
                .userId(userId)
                .studyInfoId(studyId)
                .studyTodoId(todo.getId())
                .commitSHA(payload.commitId())
                .message(payload.message())
                .commitDate(payload.commitDate())
                .status(CommitStatus.COMMIT_APPROVAL)
                .build());
    }

}
