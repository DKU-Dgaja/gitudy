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

    @Transactional
    public void handleCommit(WebhookPayload payload) {

        // 스터디 특정
        StudyInfo study = getStudyByPayLoad(payload.repositoryFullName());

        // 투두 특정
        StudyTodo todo = getTodoByPayLoad(payload.folderName());

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

    private void updateCommitAndTodoMappingStatus(Long userId, Long studyId, StudyTodo todo, WebhookPayload payload) {

        // 지각 여부 체크
        StudyTodoStatus updateStatus = payload.commitDate().isAfter(todo.getTodoDate()) ?
                StudyTodoStatus.TODO_OVERDUE : StudyTodoStatus.TODO_COMPLETE;

        // 투두 mapping 상태 업데이트
        if (!studyTodoMappingRepository.updateByUserIdAndTodoId(userId, todo.getId(), updateStatus)) {
            log.warn(">>>> Todo Id: {} {} <<<<", todo.getId(), ExceptionMessage.STUDY_TODO_MAPPING_NOT_FOUND);
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
                .status(CommitStatus.COMMIT_WAITING)
                .build());
    }
}
