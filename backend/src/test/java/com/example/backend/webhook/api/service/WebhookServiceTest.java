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
import com.example.backend.domain.define.study.commit.StudyCommitFixture;
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
import com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus;
import com.example.backend.domain.define.study.todo.mapping.repository.StudyTodoMappingRepository;
import com.example.backend.domain.define.study.todo.repository.StudyTodoRepository;
import com.example.backend.webhook.api.controller.request.CommitPayload;
import com.example.backend.webhook.api.controller.request.WebhookPayload;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus.TODO_COMPLETE;
import static com.example.backend.domain.define.study.todo.mapping.constant.StudyTodoStatus.TODO_OVERDUE;
import static com.mongodb.assertions.Assertions.assertFalse;
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

        WebhookPayload payload = new WebhookPayload(repositoryFullName, List.of(new CommitPayload(commitId, message, username,
                List.of(todoFolderName + "/" + "solution.java"), List.of(), commitDate)));

        // when
        webhookService.handleCommit(payload);

        // when
        List<StudyCommit> commitList = studyCommitRepository.findByStudyTodoIdAndUserId(todo.getId(), user.getId());
        StudyTodoMapping mapping = studyTodoMappingRepository.findByTodoIdAndUserId(todo.getId(), user.getId()).get();
        StudyInfo findStudy = studyInfoRepository.findById(study.getId()).get();

        // then
        assertEquals(TODO_COMPLETE, mapping.getStatus());
        for (StudyCommit c : commitList) {
            assertEquals(user.getId(), c.getUserId());
            assertEquals(study.getId(), c.getStudyInfoId());
        }
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

        WebhookPayload payload = new WebhookPayload(repositoryFullName, List.of(new CommitPayload(commitId, message, username,
                List.of(todoFolderName + "/" + "solution.java"), List.of(), commitDate)));

        // when
        webhookService.handleCommit(payload);

        // when
        List<StudyCommit> commitList = studyCommitRepository.findByStudyTodoIdAndUserId(todo.getId(), user.getId());
        StudyTodoMapping mapping = studyTodoMappingRepository.findByTodoIdAndUserId(todo.getId(), user.getId()).get();

        // then
        assertEquals(TODO_OVERDUE, mapping.getStatus());
        for (StudyCommit c : commitList) {
            assertEquals(user.getId(), c.getUserId());
            assertEquals(study.getId(), c.getStudyInfoId());
        }
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

        WebhookPayload payload = new WebhookPayload(repositoryFullName, List.of(new CommitPayload(commitId, message, username,
                List.of(todoFolderName + "/" + "solution.java"), List.of(), commitDate)));

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

        WebhookPayload payload = new WebhookPayload(repositoryFullName, List.of(new CommitPayload(commitId, message, username,
                List.of(todoFolderName + "/" + "solution.java"), null, commitDate)));

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

        WebhookPayload payload = new WebhookPayload(repositoryFullName, List.of(new CommitPayload(commitId, message, username,
                List.of(todoFolderName + "/" + "solution.java"), List.of(), commitDate)));

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
        studyTodoMappingRepository.save(StudyTodoFixture.createStudyTodoMapping(todo.getId(), user.getId()));

        // 강퇴당한 멤버
        StudyMember member = studyMemberRepository.save(StudyMemberFixture.createStudyMemberResigned(user.getId(), study.getId()));

        String commitId = "123";
        String message = "commit message";
        String username = user.getGithubId();
        String repositoryFullName = study.getRepositoryInfo().getOwner() + "/" + study.getRepositoryInfo().getName();
        String todoFolderName = todo.getTodoFolderName();

        // 마감일 지킨 커밋
        LocalDate commitDate = todo.getTodoDate().minusDays(1);

        WebhookPayload payload = new WebhookPayload(repositoryFullName, List.of(new CommitPayload(commitId, message, username,
                List.of(todoFolderName + "/" + "solution.java"), List.of(), commitDate)));

        // when
        MemberException e = assertThrows(MemberException.class, () -> webhookService.handleCommit(payload));
        assertEquals(ExceptionMessage.STUDY_NOT_ACTIVE_MEMBER.getText(), e.getMessage());
    }

    @Test
    void 지각했던_투두에_다시_커밋했을_때_커밋_시점이_마감일_전이라면_투두매핑_완료상태로_업데이트() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));

        // 투두에 커밋 등록 및 지각 상태의 투두 매핑 세팅
        studyTodoMappingRepository.save(StudyTodoFixture.createOverdueStudyTodoMapping(todo.getId(), user.getId()));

        String commitId = "123";
        String message = "commit message";
        String username = user.getGithubId();
        String repositoryFullName = study.getRepositoryInfo().getOwner() + "/" + study.getRepositoryInfo().getName();
        String todoFolderName = todo.getTodoFolderName();
        LocalDate commitDate = todo.getTodoDate().minusDays(3);

        WebhookPayload payload = new WebhookPayload(repositoryFullName, List.of(new CommitPayload(commitId, message, username,
                List.of(todoFolderName + "/" + "solution.java"), List.of(), commitDate)));

        // when
        webhookService.handleCommit(payload);

        // when
        List<StudyCommit> commitList = studyCommitRepository.findByStudyTodoIdAndUserId(todo.getId(), user.getId());
        StudyTodoMapping mapping = studyTodoMappingRepository.findByTodoIdAndUserId(todo.getId(), user.getId()).get();

        // then
        assertEquals(TODO_COMPLETE, mapping.getStatus());
        for (StudyCommit c : commitList) {
            assertEquals(user.getId(), c.getUserId());
            assertEquals(study.getId(), c.getStudyInfoId());
        }
    }

    @Test
    void 지각했던_투두에_다시_커밋했을_때_커밋_시점이_마감일_후라면_투두매핑_변경없음() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));
        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));

        // 투두에 커밋 등록 및 지각 상태의 투두 매핑 세팅
        studyCommitRepository.save(StudyCommitFixture.createStudyCommitWithDate(user.getId(), study.getId(), todo.getId(), "1234", LocalDate.now().plusDays(3)));
        studyTodoMappingRepository.save(StudyTodoFixture.createOverdueStudyTodoMapping(todo.getId(), user.getId()));

        String commitId = "123";
        String message = "commit message";
        String username = user.getGithubId();
        String repositoryFullName = study.getRepositoryInfo().getOwner() + "/" + study.getRepositoryInfo().getName();
        String todoFolderName = todo.getTodoFolderName();
        LocalDate commitDate = todo.getTodoDate().plusDays(3);

        WebhookPayload payload = new WebhookPayload(repositoryFullName, List.of(new CommitPayload(commitId, message, username,
                List.of(todoFolderName + "/" + "solution.java"), List.of(), commitDate)));

        // when
        webhookService.handleCommit(payload);

        // when
        List<StudyCommit> commitList = studyCommitRepository.findByStudyTodoIdAndUserId(todo.getId(), user.getId());
        StudyTodoMapping mapping = studyTodoMappingRepository.findByTodoIdAndUserId(todo.getId(), user.getId()).get();

        // then
        assertEquals(TODO_OVERDUE, mapping.getStatus());
        assertEquals(2, commitList.size());
        for (StudyCommit c : commitList) {
            assertEquals(user.getId(), c.getUserId());
            assertEquals(study.getId(), c.getStudyInfoId());
        }
    }


    @Test
    void 하나의_커밋에_여러개의_투두를_묶어서_푸시한_경우_해당하는_투두에_적용해야_한다() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));

        // 미완료 투두 A
        StudyTodo todoA = studyTodoRepository.save(StudyTodoFixture.createStudyTodoCustom(study.getId(), "A", "detail", "link", LocalDate.now()));
        studyTodoMappingRepository.save(StudyTodoFixture.createStudyTodoMapping(todoA.getId(), user.getId()));

        // 미완료 투두 B
        StudyTodo todoB = studyTodoRepository.save(StudyTodoFixture.createStudyTodoCustom(study.getId(), "B", "detail", "link", LocalDate.now()));
        studyTodoMappingRepository.save(StudyTodoFixture.createStudyTodoMapping(todoB.getId(), user.getId()));

        // 완료 투두 C
        StudyTodo todoC = studyTodoRepository.save(StudyTodoFixture.createStudyTodoCustom(study.getId(), "C", "detail", "link", LocalDate.now()));
        studyTodoMappingRepository.save(StudyTodoFixture.createCompleteStudyTodoMapping(todoC.getId(), user.getId()));
        studyCommitRepository.save(StudyCommitFixture.createDefaultStudyCommit(user.getId(), study.getId(), todoC.getId(), "shaB"));

        // 지각 투두 D
        StudyTodo todoD = studyTodoRepository.save(StudyTodoFixture.createStudyTodoCustom(study.getId(), "D", "detail", "link", LocalDate.now()));
        studyTodoMappingRepository.save(StudyTodoFixture.createOverdueStudyTodoMapping(todoD.getId(), user.getId()));
        studyCommitRepository.save(StudyCommitFixture.createStudyCommitWithDate(user.getId(), study.getId(), todoD.getId(), "shaD", LocalDate.now().plusDays(3)));

        // 웹훅 페이로드
        String repositoryFullName = study.getRepositoryInfo().getOwner() + "/" + study.getRepositoryInfo().getName();
        String commitId = "qwerasdfzxcv1234";
        String message = "밀린 숙제들 몰아서 제출합니다.";

        WebhookPayload payload = new WebhookPayload(repositoryFullName, List.of(new CommitPayload(
                commitId, message, user.getGithubId(),
                List.of(todoA.getTodoFolderName() + "/solution.java",
                        todoB.getTodoFolderName() + "/solution.java"),
                List.of(todoC.getTodoFolderName() + "/solution2.java",
                        todoD.getTodoFolderName() + "/solution.java"),
                LocalDate.now().minusDays(3)
        )));

        // when
        webhookService.handleCommit(payload);

        // then
        List<StudyCommit> commitsA = studyCommitRepository.findByStudyTodoIdAndUserId(todoA.getId(), user.getId());
        List<StudyCommit> commitsB = studyCommitRepository.findByStudyTodoIdAndUserId(todoB.getId(), user.getId());
        List<StudyCommit> commitsC = studyCommitRepository.findByStudyTodoIdAndUserId(todoC.getId(), user.getId());
        List<StudyCommit> commitsD = studyCommitRepository.findByStudyTodoIdAndUserId(todoD.getId(), user.getId());

        assertEquals(1, commitsA.size());
        assertEquals(1, commitsB.size());
        assertEquals(2, commitsC.size());
        assertEquals(2, commitsD.size());

        /* 예상 결과)
                A, B -> 커밋 생성 + 투두매핑 COMPLETE로 업데이트
                C -> 커밋 생성 + 투두매핑 변함없음
                D -> 커밋 생성 + 투두패밍 COMPLETE로 업데이트
         */
        StudyTodoMapping mappingA = studyTodoMappingRepository.findByTodoIdAndUserId(todoA.getId(), user.getId()).get();
        StudyTodoMapping mappingB = studyTodoMappingRepository.findByTodoIdAndUserId(todoB.getId(), user.getId()).get();
        StudyTodoMapping mappingC = studyTodoMappingRepository.findByTodoIdAndUserId(todoC.getId(), user.getId()).get();
        StudyTodoMapping mappingD = studyTodoMappingRepository.findByTodoIdAndUserId(todoD.getId(), user.getId()).get();

        assertEquals(TODO_COMPLETE, mappingA.getStatus());
        assertEquals(TODO_COMPLETE, mappingB.getStatus());
        assertEquals(TODO_COMPLETE, mappingC.getStatus());
        assertEquals(TODO_COMPLETE, mappingD.getStatus());
    }

    @Test
    void 하나의_푸시에_여러개의_커밋리스트가_페이로드로_전송되어도_전부_처리되어야_한다() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));

        // 미완료 투두 A
        StudyTodo todoA = studyTodoRepository.save(StudyTodoFixture.createStudyTodoCustom(study.getId(), "A", "detail", "link", LocalDate.now().minusDays(3)));
        studyTodoMappingRepository.save(StudyTodoFixture.createStudyTodoMapping(todoA.getId(), user.getId()));

        // 미완료 투두 B
        StudyTodo todoB = studyTodoRepository.save(StudyTodoFixture.createStudyTodoCustom(study.getId(), "B", "detail", "link", LocalDate.now().plusDays(3)));
        studyTodoMappingRepository.save(StudyTodoFixture.createStudyTodoMapping(todoB.getId(), user.getId()));

        // 웹훅 페이로드
        String repositoryFullName = study.getRepositoryInfo().getOwner() + "/" + study.getRepositoryInfo().getName();
        String commitAId = "A";
        String messageA = "A 제출";
        String commitBId = "B";
        String messageB = "B 제출";

        WebhookPayload payload = new WebhookPayload(repositoryFullName,
                List.of(new CommitPayload(
                                commitAId, messageA, user.getGithubId(),
                                List.of(todoA.getTodoFolderName() + "/solution.java"), null,
                                LocalDate.now()),
                        new CommitPayload(
                                commitBId, messageB, user.getGithubId(),
                                List.of(todoB.getTodoFolderName() + "/solution.java"), null,
                                LocalDate.now())
                ));

        // when
        webhookService.handleCommit(payload);

        // then
        List<StudyCommit> commitsA = studyCommitRepository.findByStudyTodoIdAndUserId(todoA.getId(), user.getId());
        List<StudyCommit> commitsB = studyCommitRepository.findByStudyTodoIdAndUserId(todoB.getId(), user.getId());
        assertEquals(1, commitsA.size());
        assertEquals(1, commitsB.size());

        /* 예상 결과)
                A -> 커밋 생성 + 지각
                B -> 커밋 생성 + 완료
         */
        StudyTodoMapping mappingA = studyTodoMappingRepository.findByTodoIdAndUserId(todoA.getId(), user.getId()).get();
        StudyTodoMapping mappingB = studyTodoMappingRepository.findByTodoIdAndUserId(todoB.getId(), user.getId()).get();

        assertEquals(TODO_OVERDUE, mappingA.getStatus());
        assertEquals(TODO_COMPLETE, mappingB.getStatus());
    }

    @Test
    void 투두_폴더_생성_커밋의_경우_커밋_등록을_하지_않는다() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.generateStudyInfo(user.getId()));
        studyMemberRepository.save(StudyMemberFixture.createDefaultStudyMember(user.getId(), study.getId()));

        StudyTodo todo = studyTodoRepository.save(StudyTodoFixture.createStudyTodo(study.getId()));
        studyTodoMappingRepository.save(StudyTodoFixture.createStudyTodoMapping(todo.getId(), user.getId()));

        String repositoryFullName = study.getRepositoryInfo().getOwner() + "/" + study.getRepositoryInfo().getName();
        WebhookPayload payload = new WebhookPayload(repositoryFullName, List.of(new CommitPayload(
                "e4adeb0d170eca2d4fe8f738876e607a43e94953",
                "Create todo folder",
                user.getGithubId(), List.of(todo.getTodoFolderName() + "/test.md"),
                List.of(), LocalDate.of(2024, 8, 26)
        )));

        // when
        webhookService.handleCommit(payload);
        StudyTodoMapping findTodoMapping = studyTodoMappingRepository.findByTodoIdAndUserId(todo.getId(), user.getId()).get();
        List<StudyCommit> commitList = studyCommitRepository.findByStudyTodoIdAndUserId(todo.getId(), user.getId());

        // then
        assertEquals(StudyTodoStatus.TODO_INCOMPLETE, findTodoMapping.getStatus());
        assertEquals(0, commitList.size());
    }

    @Test
    void 마지막_커밋_활동일_업데이트_성공() {
        // given
        User user = userRepository.save(UserFixture.generateAuthUser());
        StudyInfo study = studyInfoRepository.save(StudyInfoFixture.createDefaultPublicStudyInfoWithDate(user.getId(), LocalDate.now().minusDays(1)));
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

        WebhookPayload payload = new WebhookPayload(repositoryFullName, List.of(new CommitPayload(commitId, message, username,
                List.of(todoFolderName + "/" + "solution.java"), List.of(), commitDate)));

        // when
        webhookService.handleCommit(payload);
        StudyInfo findStudy = studyInfoRepository.findById(study.getId()).get();

        // then
        assertFalse(findStudy.getLastCommitDay() == LocalDate.now().minusDays(1));
        assertEquals(LocalDate.now(), findStudy.getLastCommitDay());
    }
}