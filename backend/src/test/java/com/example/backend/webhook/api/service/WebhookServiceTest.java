package com.example.backend.webhook.api.service;

import com.example.backend.TestConfig;
import com.example.backend.auth.config.fixture.UserFixture;
import com.example.backend.common.exception.ExceptionMessage;
import com.example.backend.common.exception.member.MemberException;
import com.example.backend.common.exception.study.StudyInfoException;
import com.example.backend.common.exception.todo.TodoException;
import com.example.backend.common.exception.user.UserException;
import com.example.backend.domain.define.account.user.User;
import com.example.backend.domain.define.account.user.repository.UserRepository;
import com.example.backend.domain.define.study.commit.StudyCommit;
import com.example.backend.domain.define.study.commit.repository.StudyCommitRepository;
import com.example.backend.domain.define.study.info.StudyInfo;
import com.example.backend.domain.define.study.info.StudyInfoFixture;
import com.example.backend.domain.define.study.info.repository.StudyInfoRepository;
import com.example.backend.domain.define.study.member.StudyMember;
import com.example.backend.domain.define.study.member.StudyMemberFixture;
import com.example.backend.domain.define.study.member.repository.StudyMemberRepository;
import com.example.backend.domain.define.study.todo.StudyTodoFixture;
import com.example.backend.domain.define.study.todo.info.StudyTodo;
import com.example.backend.domain.define.study.todo.mapping.StudyTodoMapping;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.webhook.api.controller.request.WebhookPayload;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus.TODO_COMPLETE;
import static com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus.TODO_OVERDUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WebhookServiceTest extends TestConfig {

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyCommitRepository studyCommitRepository;

    @Autowired
    StudyInfoRepository studyInfoRepository;

    @Autowired
    StudyTodoRepository studyTodoRepository;

    @Autowired
    StudyMemberRepository studyMemberRepository;

    @Autowired
    StudyTodoMappingRepository studyTodoMappingRepository;

    @Autowired
    WebhookService webhookService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
        studyCommitRepository.deleteAllInBatch();
        studyInfoRepository.deleteAllInBatch();
        studyTodoRepository.deleteAllInBatch();
        studyMemberRepository.deleteAllInBatch();
        studyTodoMappingRepository.deleteAllInBatch();
    }

    @Test
    void 마감일_지킨_커밋_처리_성공_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        StudyMember member = studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));
        StudyTodoMapping todoMapping = studyTodoMappingRepository.save(StudyTodoFixture.createStudyTodoMapping(todo.getId(), user.getId()));

        String commitId = "123";
        String message = "commit message";
        String username = user.getGithubId();
        String repositoryFullName = study.getRepositoryInfo().getOwner() + "/" + study.getRepositoryInfo().getName();
        String todoFolderName = todo.getTodoFolderName();

        // 마감일 지킨 커밋
        LocalDate commitDate = todo.getTodoDate().minusDays(1);

        WebhookPayload payload = new WebhookPayload(commitId, message, username, repositoryFullName, todoFolderName, commitDate);

        // when
        webhookService.handleCommit(payload);

        // when
        StudyCommit commit = studyCommitRepository.findByStudyTodoIdAndUserId(todo.getId(), user.getId()).get();
        StudyTodoMapping mapping = studyTodoMappingRepository.findByTodoIdAndUserId(todo.getId(), user.getId()).get();

        // then
        assertEquals(TODO_COMPLETE, mapping.getStatus());
        assertEquals(commitId, commit.getCommitSHA());
        assertEquals(user.getId(), commit.getUserId());
    }

    @Test
    void 지각한_커밋_처리_성공_테스트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        StudyMember member = studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));
        StudyTodoMapping todoMapping = studyTodoMappingRepository.save(StudyTodoFixture.createStudyTodoMapping(todo.getId(), user.getId()));

        String commitId = "123";
        String message = "commit message";
        String username = user.getGithubId();
        String repositoryFullName = study.getRepositoryInfo().getOwner() + "/" + study.getRepositoryInfo().getName();
        String todoFolderName = todo.getTodoFolderName();

        // 지각한 커밋
        LocalDate commitDate = todo.getTodoDate().plusDays(1);

        WebhookPayload payload = new WebhookPayload(commitId, message, username, repositoryFullName, todoFolderName, commitDate);

        // when
        webhookService.handleCommit(payload);

        // when
        StudyCommit commit = studyCommitRepository.findByStudyTodoIdAndUserId(todo.getId(), user.getId()).get();
        StudyTodoMapping mapping = studyTodoMappingRepository.findByTodoIdAndUserId(todo.getId(), user.getId()).get();

        // then
        assertEquals(TODO_OVERDUE, mapping.getStatus());
        assertEquals(commitId, commit.getCommitSHA());
        assertEquals(user.getId(), commit.getUserId());
    }

    @Test
    void 이미_커밋했던_투두에_다시_커밋했을_때_투두_상태_변화_없음() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        StudyMember member = studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));

        // 이미 완료 상태의 투두 상태
        StudyTodoMapping todoMapping = studyTodoMappingRepository.save(StudyTodoFixture.createCompleteStudyTodoMapping(todo.getId(), user.getId()));

        String commitId = "123";
        String message = "commit message";
        String username = user.getGithubId();
        String repositoryFullName = study.getRepositoryInfo().getOwner() + "/" + study.getRepositoryInfo().getName();
        String todoFolderName = todo.getTodoFolderName();
        LocalDate commitDate = todo.getTodoDate().plusDays(1);

        WebhookPayload payload = new WebhookPayload(commitId, message, username, repositoryFullName, todoFolderName, commitDate);

        // when
        webhookService.handleCommit(payload);

        // when
        StudyCommit commit = studyCommitRepository.findByStudyTodoIdAndUserId(todo.getId(), user.getId()).get();
        StudyTodoMapping mapping = studyTodoMappingRepository.findByTodoIdAndUserId(todo.getId(), user.getId()).get();

        // then
        assertEquals(TODO_COMPLETE, mapping.getStatus());
        assertEquals(commitId, commit.getCommitSHA());
        assertEquals(user.getId(), commit.getUserId());
    }

    @Test
    void 페이로드에_해당하는_유저가_없으면_커밋_처리_실패() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        StudyMember member = studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));
        StudyTodoMapping todoMapping = studyTodoMappingRepository.save(StudyTodoFixture.createStudyTodoMapping(todo.getId(), user.getId()));

        String commitId = "123";
        String message = "commit message";
        String repositoryFullName = study.getRepositoryInfo().getOwner() + "/" + study.getRepositoryInfo().getName();
        LocalDate commitDate = todo.getTodoDate().minusDays(1);
        String todoFolderName = todo.getTodoFolderName();

        // 존재하지 않는 유저(깃허브계정)
        String username = "invalid";

        WebhookPayload payload = new WebhookPayload(commitId, message, username, repositoryFullName, todoFolderName, commitDate);

        // when
        UserException e = assertThrows(UserException.class, () -> webhookService.handleCommit(payload));
        assertEquals(ExceptionMessage.USER_NOT_FOUND.getText(), e.getMessage());
    }

    @Test
    void 페이로드에_해당하는_스터디가_없으면_커밋_처리_실패() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        StudyMember member = studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));
        StudyTodoMapping todoMapping = studyTodoMappingRepository.save(StudyTodoFixture.createStudyTodoMapping(todo.getId(), user.getId()));

        String commitId = "123";
        String username = user.getGithubId();
        String message = "commit message";
        LocalDate commitDate = todo.getTodoDate().minusDays(1);
        String todoFolderName = todo.getTodoFolderName();

        // 존재하지 않는 스터디
        String repositoryFullName = "test/test";

        WebhookPayload payload = new WebhookPayload(commitId, message, username, repositoryFullName, todoFolderName, commitDate);

        // when
        StudyInfoException e = assertThrows(StudyInfoException.class, () -> webhookService.handleCommit(payload));
        assertEquals(ExceptionMessage.STUDY_INFO_NOT_FOUND.getText(), e.getMessage());
    }

    @Test
    void 페이로드에_해당하는_투두가_없으면_커밋_처리_실패() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        StudyMember member = studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));
        StudyTodoMapping todoMapping = studyTodoMappingRepository.save(StudyTodoFixture.createStudyTodoMapping(todo.getId(), user.getId()));

        String commitId = "123";
        String username = user.getGithubId();
        String repositoryFullName = study.getRepositoryInfo().getOwner() + "/" + study.getRepositoryInfo().getName();
        String message = "test commit";
        LocalDate commitDate = todo.getTodoDate().minusDays(1);

        // 존재하지 않는 투두폴더
        String todoFolderName = "invalid-folder-name";

        WebhookPayload payload = new WebhookPayload(commitId, message, username, repositoryFullName, todoFolderName, commitDate);

        // when
        TodoException e = assertThrows(TodoException.class, () -> webhookService.handleCommit(payload));
        assertEquals(ExceptionMessage.TODO_NOT_FOUND.getText(), e.getMessage());
    }

    @Test
    void 커미터가_스터디의_활성화된_멤버가_아닌_경우_커밋_처리_실패() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));
        StudyTodoMapping todoMapping = studyTodoMappingRepository.save(StudyTodoFixture.createStudyTodoMapping(todo.getId(), user.getId()));

        // 강퇴당한 멤버
        StudyMember member = studyMemberRepository.save(StudyMemberFixture.createStudyMemberResigned(user.getId(), study.getId()));

        String commitId = "123";
        String message = "commit message";
        String username = user.getGithubId();
        String repositoryFullName = study.getRepositoryInfo().getOwner() + "/" + study.getRepositoryInfo().getName();
        String todoFolderName = todo.getTodoFolderName();

        // 마감일 지킨 커밋
        LocalDate commitDate = todo.getTodoDate().minusDays(1);

        WebhookPayload payload = new WebhookPayload(commitId, message, username, repositoryFullName, todoFolderName, commitDate);

        // when
        MemberException e = assertThrows(MemberException.class, () -> webhookService.handleCommit(payload));
        assertEquals(ExceptionMessage.STUDY_NOT_ACTIVE_MEMBER.getText(), e.getMessage());
    }
}